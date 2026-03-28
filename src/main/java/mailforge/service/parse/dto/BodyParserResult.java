package mailforge.service.parse.dto;

import java.util.List;

public record BodyParserResult(
    String textBody,
    String htmlBody,
    List<ParsedAttachmentDto> attachments
) {}
