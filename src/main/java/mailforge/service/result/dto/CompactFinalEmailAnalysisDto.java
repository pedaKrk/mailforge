package mailforge.service.result.dto;

import mailforge.service.parse.dto.ParsedHeaderDto;

public record CompactFinalEmailAnalysisDto(
        ParsedHeaderDto headers
) {
}
