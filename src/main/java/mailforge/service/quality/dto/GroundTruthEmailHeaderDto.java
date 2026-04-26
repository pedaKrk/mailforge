package mailforge.service.quality.dto;

import java.time.Instant;
import java.util.List;

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
