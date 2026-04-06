package mailforge.service.parse.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record ParsedBodyDto(
        String textBody,
        String htmlBody
) {}
