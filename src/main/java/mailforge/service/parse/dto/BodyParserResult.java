package mailforge.service.parse.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Introspected
@Serdeable
public record BodyParserResult(
    String textBody,
    String htmlBody,
    List<ParsedAttachmentDto> attachments
) {}
