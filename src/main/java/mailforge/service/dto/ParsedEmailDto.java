package mailforge.service.dto;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
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
