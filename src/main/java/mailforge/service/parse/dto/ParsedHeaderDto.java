package mailforge.service.parse.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Introspected
@Serdeable
public record ParsedHeaderDto(
        String subject,
        String from,
        List<String> to,
        List<String> cc,
        List<String> bcc,
        String messageId,
        String sentDate
) {}
