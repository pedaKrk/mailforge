package mailforge.service.ai.dto.output;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record ContactDto(
        String name,
        String email,
        String phone,
        String organization,
        String source,
        String confidence
) {}
