package mailforge.service.process.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record ProcessedAttachmentDto(
        String attachmentId,
        String originalFilename,
        String mimeType,
        long size,
        boolean inline,
        String sha256,
        String storagePath,
        ProcessingMode processingMode,
        boolean textExtracted,
        boolean ocrApplied,
        String extractedText,
        String warning
) {}
