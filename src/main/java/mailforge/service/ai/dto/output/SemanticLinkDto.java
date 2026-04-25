package mailforge.service.ai.dto.output;

public record SemanticLinkDto(
        String attachmentId,
        String relation,
        String evidence,
        Double confidence
) {
}
