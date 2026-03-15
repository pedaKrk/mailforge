package mailforge.service.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Introspected
@Serdeable
public record ParsedEmailDto(
        String subject,
        String from,
        List<String> to,
        List<String> cc,
        List<String> bcc,
        String messageId,
        String sentDate,
        String textBody,
        String htmlBody,
        List<AttachmentDto> attachments
) {}
