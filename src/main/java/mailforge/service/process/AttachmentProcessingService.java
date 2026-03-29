package mailforge.service.process;

import mailforge.service.parse.dto.ParsedAttachmentDto;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.process.error.AttachmentProcessingException;

import java.util.List;

public interface AttachmentProcessingService {
    List<ProcessedAttachmentDto> process(List<ParsedAttachmentDto> attachments) throws AttachmentProcessingException;
    ProcessedAttachmentDto process(ParsedAttachmentDto attachment) throws AttachmentProcessingException;
}
