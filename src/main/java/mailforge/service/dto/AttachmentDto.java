package mailforge.service.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record AttachmentDto(
        String filename,
        String mimeType,
        long size,
        boolean inline
) {}
