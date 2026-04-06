package mailforge.service.process.impl;

import jakarta.inject.Singleton;
import mailforge.service.process.AttachmentProcessingService;
import mailforge.service.process.ProcessingModeResolver;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.process.dto.ProcessingMode;
import mailforge.service.process.error.AttachmentProcessingException;
import mailforge.service.process.error.ExtractionException;
import mailforge.service.storage.dto.StoredAttachmentDto;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class AttachmentProcessingServiceImpl implements AttachmentProcessingService {

    private final ProcessingModeResolver processingModeResolver;
    private final TextExtractionService textExtractionService;
    private final PdfExtractionService pdfExtractionService;
    private final OcrExtractionService ocrExtractionService;

    public AttachmentProcessingServiceImpl(ProcessingModeResolver processingModeResolver, TextExtractionService textExtractionService, PdfExtractionService pdfExtractionService, OcrExtractionService ocrExtractionService) {
        this.processingModeResolver = processingModeResolver;
        this.textExtractionService = textExtractionService;
        this.pdfExtractionService = pdfExtractionService;
        this.ocrExtractionService = ocrExtractionService;
    }

    @Override
    public List<ProcessedAttachmentDto> process(List<StoredAttachmentDto> attachments) throws AttachmentProcessingException {
        List<ProcessedAttachmentDto> result = new ArrayList<>();
        for (var attachment : attachments){
            result.add(process(attachment));
        }
        return result;
    }

    @Override
    public ProcessedAttachmentDto process(StoredAttachmentDto attachment) throws AttachmentProcessingException {
        try {
            return switch (processingModeResolver.resolve(attachment)){
                case SKIP -> buildResult(attachment, ProcessingMode.SKIP, false, false, null, "Inline attachment skipped");
                case EMPTY -> buildResult(attachment, ProcessingMode.EMPTY, false, false, null, "Attachment is empty");
                case UNSUPPORTED -> buildResult(attachment, ProcessingMode.UNSUPPORTED, false, false, null, "Unsupported attachment type");
                case TEXT -> {
                    String text = textExtractionService.extract(attachment);
                    boolean extracted = hasText(text);
                    yield buildResult(attachment, ProcessingMode.TEXT, extracted, false, text, extracted ? null : "Text extraction returned no text");
                }
                case PDF -> {
                    String text = pdfExtractionService.extract(attachment);
                    if(hasText(text)) {
                        yield buildResult(attachment, ProcessingMode.PDF, true, false, text, null);
                    }
                    String ocrText = ocrExtractionService.extract(attachment);
                    boolean extracted = hasText(ocrText);
                    yield buildResult(attachment, ProcessingMode.PDF, extracted, true, ocrText, extracted ? null : "PDF contained no extractable text and OCR returned no text");
                }
                case OCR -> {
                    String ocrText = ocrExtractionService.extract(attachment);
                    boolean extracted = hasText(ocrText);
                    yield buildResult(attachment, ProcessingMode.OCR, extracted, true, ocrText, extracted ? null : "OCR returned no text");
                }
            };
        } catch (ExtractionException e) {
            throw new AttachmentProcessingException("Error while processing Attachment: " + e.getMessage(), e);
        }
    }

    private boolean hasText(String text) {
        return text != null && !text.isBlank();
    }

    private ProcessedAttachmentDto buildResult(StoredAttachmentDto storedAttachment, ProcessingMode mode, boolean textExtracted, boolean ocrApplied, String extractedText, String processingWarning){
        return new ProcessedAttachmentDto(
                storedAttachment.attachmentId(),
                storedAttachment.filename(),
                storedAttachment.mimeType(),
                storedAttachment.sizeBytes(),
                storedAttachment.inline(),
                storedAttachment.contentId(),
                storedAttachment.contentDisposition(),
                storedAttachment.contentTransferEncoding(),
                storedAttachment.sha256(),
                storedAttachment.storagePath(),
                mode,
                textExtracted,
                ocrApplied,
                extractedText,
                processingWarning
        );
    }
}
