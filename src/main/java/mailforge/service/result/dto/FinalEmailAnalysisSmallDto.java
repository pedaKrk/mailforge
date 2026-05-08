package mailforge.service.result.dto;

import mailforge.service.parse.dto.ParsedHeaderDto;

public record FinalEmailAnalysisSmallDto(
        ParsedHeaderDto headers
) {
}
