package mailforge.service.ai.dto.output;

public record EmailClassificationDto(
        String messageId,
        String label,
        Double confidence
) {}
