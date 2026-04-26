package mailforge.service.ai.dto.output;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Introspected
@Serdeable
public record AiAnalysisResultDto(
        EmailClassificationDto emailClassification,
        List<AttachmentClassificationDto> attachmentClassifications,
        List<Keyword> keywords,
        List<ContactDto> contacts,
        List<SemanticLinkDto> semanticLinks
) {}
