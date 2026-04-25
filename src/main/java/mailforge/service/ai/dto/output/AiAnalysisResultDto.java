package mailforge.service.ai.dto.output;

import java.util.List;

public record AiAnalysisResultDto(
        EmailClassificationDto emailClassification,
        List<AttachmentClassificationDto> attachmentClassifications,
        List<ContactDto> contacts,
        List<SemanticLinkDto> semanticLinks
) {
}
