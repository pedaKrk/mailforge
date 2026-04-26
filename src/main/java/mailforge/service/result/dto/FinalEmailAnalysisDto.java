package mailforge.service.result.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import mailforge.service.parse.dto.ParsedHeaderDto;

@Introspected
@Serdeable
public record FinalEmailAnalysisDto(
        ParsedHeaderDto headers
) {}
