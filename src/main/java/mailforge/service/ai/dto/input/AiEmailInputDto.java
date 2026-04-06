package mailforge.service.ai.dto.input;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Introspected
@Serdeable
public record AiEmailInputDto(
        AiHeaderInputDto headers,
        AiBodyInputDto body,
        List<AiAttachmentInputDto> attachments
) {}
