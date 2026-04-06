package mailforge.service.ai.dto.output;

public record AttachmentClassificationDto(
        String attachmentId,
        String label,
        Double confidence
) {}
