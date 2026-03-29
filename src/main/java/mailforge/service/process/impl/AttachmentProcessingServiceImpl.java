package mailforge.service.process.impl;

import jakarta.inject.Singleton;
import mailforge.service.parse.dto.ParsedAttachmentDto;
import mailforge.service.process.AttachmentProcessingService;
import mailforge.service.process.ProcessingModeResolver;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.process.dto.ProcessingMode;
import mailforge.service.process.error.AttachmentProcessingException;
import mailforge.service.process.error.ExtractionException;
import mailforge.service.storage.AttachmentStorageService;
import mailforge.service.storage.dto.StoredAttachmentDto;
import mailforge.service.storage.error.AttachmentStorageException;

@Singleton
public class AttachmentProcessingServiceImpl implements AttachmentProcessingService {

    private final AttachmentStorageService storageService;
    private final ProcessingModeResolver processingModeResolver;
    private final TextExtractionService textExtractionService;
    private final PdfExtractionService pdfExtractionService;
    private final OcrExtractionService ocrExtractionService;

    public AttachmentProcessingServiceImpl(AttachmentStorageService storageService, ProcessingModeResolver processingModeResolver, TextExtractionService textExtractionService, PdfExtractionService pdfExtractionService, OcrExtractionService ocrExtractionService) {
        this.storageService = storageService;
        this.processingModeResolver = processingModeResolver;
        this.textExtractionService = textExtractionService;
        this.pdfExtractionService = pdfExtractionService;
        this.ocrExtractionService = ocrExtractionService;
    }

    @Override
    public ProcessedAttachmentDto process(ParsedAttachmentDto attachment) throws AttachmentProcessingException {
        try {
            StoredAttachmentDto storedAttachment = storageService.store(attachment);

            return switch (processingModeResolver.resolve(storedAttachment)){
                case SKIP -> buildResult(storedAttachment, ProcessingMode.SKIP, false, false, null, "Inline attachment skipped");
                case EMPTY -> buildResult(storedAttachment, ProcessingMode.EMPTY, false, false, null, "Attachment is empty");
                case UNSUPPORTED -> buildResult(storedAttachment, ProcessingMode.UNSUPPORTED, false, false, null, "Unsupported attachment type");
                case TEXT -> {
                    String text = textExtractionService.extract(storedAttachment);
                    boolean extracted = hasText(text);
                    yield buildResult(storedAttachment, ProcessingMode.TEXT, extracted, false, text, extracted ? null : "Text extraction returned no text");
                }
                case PDF -> {
                    String text = pdfExtractionService.extract(storedAttachment);
                    if(hasText(text)) {
                        yield buildResult(storedAttachment, ProcessingMode.PDF, true, false, text, null);
                    }
                    String ocrText = ocrExtractionService.extract(storedAttachment);
                    boolean extracted = hasText(ocrText);
                    yield buildResult(storedAttachment, ProcessingMode.PDF, extracted, true, ocrText, extracted ? null : "PDF contained no extractable text and OCR returned no text");
                }
                case OCR -> {
                    String ocrText = ocrExtractionService.extract(storedAttachment);
                    boolean extracted = hasText(ocrText);
                    yield buildResult(storedAttachment, ProcessingMode.OCR, extracted, true, ocrText, extracted ? null : "OCR returned no text");
                }
            };
        } catch (AttachmentStorageException | ExtractionException e) {
            throw new AttachmentProcessingException("Error while processing Attachment: " + e.getMessage(), e);
        }
    }

    private boolean hasText(String text) {
        return text != null && !text.isBlank();
    }

    private ProcessedAttachmentDto buildResult(StoredAttachmentDto storedAttachment, ProcessingMode mode, boolean textExtracted, boolean ocrApplied, String extractedText, String warning){
        return new ProcessedAttachmentDto(
                storedAttachment.attachmentId(),
                storedAttachment.originalFilename(),
                storedAttachment.mimeType(),
                storedAttachment.size(),
                storedAttachment.inline(),
                storedAttachment.sha256(),
                storedAttachment.storagePath(),
                mode,
                textExtracted,
                ocrApplied,
                extractedText,
                warning
        );
    }
}
