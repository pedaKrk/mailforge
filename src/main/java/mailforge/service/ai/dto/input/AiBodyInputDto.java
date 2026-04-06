package mailforge.service.ai.dto.input;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record AiBodyInputDto(
        String textBody,
        String htmlBody
) {
}
