package mailforge.service.ai.dto.output;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record AttachmentClassificationDto(
        String attachmentId,
        String label,
        Double confidence
) {}
