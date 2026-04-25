package mailforge.service.ai;

import mailforge.service.ai.dto.input.AiEmailInputDto;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.process.dto.ProcessedAttachmentDto;

import java.util.List;

public interface AiInputPrepareService {
    AiEmailInputDto prepare(ParsedEmailDto email, List<ProcessedAttachmentDto> attachments);
}
