package mailforge.service.parse.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record ParsedAttachmentDto(
        String filename,
        String mimeType,
        long size,
        boolean inline,
        byte[] content
) {}
