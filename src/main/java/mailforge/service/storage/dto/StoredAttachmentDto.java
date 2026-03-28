package mailforge.service.storage.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record StoredAttachmentDto(
    String attachmentId,
    String originalFilename,
    String mimeType,
    long size,
    boolean inline,
    String sha256,
    String storagePath
) {}
