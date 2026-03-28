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

    public AttachmentProcessingServiceImpl(AttachmentStorageService storageService, ProcessingModeResolver processingModeResolver, TextExtractionService textExtractionService, PdfExtractionService pdfExtractionService) {
        this.storageService = storageService;
        this.processingModeResolver = processingModeResolver;
        this.textExtractionService = textExtractionService;
        this.pdfExtractionService = pdfExtractionService;
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
                    yield buildResult(storedAttachment, ProcessingMode.TEXT, true, false, text, null);
                }
                case PDF -> {
                    String text = pdfExtractionService.extract(storedAttachment);
                    if(!text.isEmpty()) {
                        yield buildResult(storedAttachment, ProcessingMode.PDF, true, false, null, null);
                    }
                    yield buildResult(storedAttachment, ProcessingMode.PDF, false, true, null, "not implemented");
                }
                case OCR -> buildResult(storedAttachment, ProcessingMode.PDF, false, true, null, "not implemented");
            };
        } catch (AttachmentStorageException | ExtractionException e) {
            throw new AttachmentProcessingException("Error while processing Attachment: " + e.getMessage(), e);
        }
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
