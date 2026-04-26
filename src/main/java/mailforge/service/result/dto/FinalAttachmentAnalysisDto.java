package mailforge.service.result.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import mailforge.service.process.dto.ProcessingMode;

@Introspected
@Serdeable
public record FinalAttachmentAnalysisDto(
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
        String warnings
) {}
