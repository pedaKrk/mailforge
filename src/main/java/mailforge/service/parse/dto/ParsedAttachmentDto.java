package mailforge.service.parse.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record ParsedAttachmentDto(
        String attachmentId,
        String filename,
        String mimeType,
        long sizeBytes,
        boolean inline,
        String contentId,
        String contentDisposition,
        String contentTransferEncoding,
        byte[] content
) {}
