package mailforge.service.quality.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.List;

@Introspected
@Serdeable
public record GroundTruthEmailHeaderDto(
        String messageId,
        String subject,
        Instant date,
        String sender,
        List<String> from,
        List<String> to,
        List<String> cc,
        List<String> bcc,
        List<String> replyTo
) {}
