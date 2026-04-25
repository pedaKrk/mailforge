package mailforge.service.ai;

import mailforge.service.ai.dto.input.AiEmailInputDto;
import mailforge.service.ai.dto.output.AiAnalysisResultDto;
import mailforge.service.ai.error.AiAnalysisException;

public interface AiClient {
    AiAnalysisResultDto analyze(AiEmailInputDto input) throws AiAnalysisException;
}
