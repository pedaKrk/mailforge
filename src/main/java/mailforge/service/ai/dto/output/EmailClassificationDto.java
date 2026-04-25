package mailforge.service.ai.dto.output;

public record EmailClassificationDto(
        String label,
        Double confidence
) {}
