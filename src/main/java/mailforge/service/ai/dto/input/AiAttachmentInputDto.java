package mailforge.service.ai.dto.input;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record AiAttachmentInputDto(
        String attachmentId,
        String filename,
        String mimeType,
        String extractedText
) {}
