package mailforge.service.ai.dto.output;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record SemanticLinkDto(
        String attachmentId,
        String relation,
        String evidence,
        Double confidence
) {}
