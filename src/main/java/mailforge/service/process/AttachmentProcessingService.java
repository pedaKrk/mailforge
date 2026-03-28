package mailforge.service.process;

import mailforge.service.parse.dto.ParsedAttachmentDto;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.process.error.AttachmentProcessingException;

public interface AttachmentProcessingService {
    ProcessedAttachmentDto process(ParsedAttachmentDto attachment) throws AttachmentProcessingException;
}
