package mailforge.service.dto;

import io.micronaut.core.annotation.Introspected;

@Introspected
public record AttachmentDto(
        String filename,
        String mimeType,
        long size,
        boolean inline
) {}
