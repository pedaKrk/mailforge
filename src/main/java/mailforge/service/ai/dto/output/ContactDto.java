package mailforge.service.ai.dto.output;

public record ContactDto(
        String name,
        String email,
        String phone,
        String organization,
        String source,
        String confidence
) {}
