package mailforge.service.process.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record ProcessedAttachmentDto(
        String attachmentId,
        String filename,
        String mimeType,
        long sizeBytes,
        boolean inline,
        String contentId,
        String contentDisposition,
        String contentTransferEncoding,
        String sha256,
        String storagePath,
        ProcessingMode processingMode,
        boolean extractionSuccessful,
        boolean ocrApplied,
        String extractedText,
        String warnings
) {}
