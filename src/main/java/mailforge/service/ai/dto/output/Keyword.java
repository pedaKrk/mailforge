package mailforge.service.ai.dto.output;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public record Keyword(
        String value,
        Double confidence
) {}
