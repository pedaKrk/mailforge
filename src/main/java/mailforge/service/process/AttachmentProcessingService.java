package mailforge.service.process;

import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.process.error.AttachmentProcessingException;
import mailforge.service.storage.dto.StoredAttachmentDto;

import java.util.List;

public interface AttachmentProcessingService {
    List<ProcessedAttachmentDto> process(List<StoredAttachmentDto> attachments) throws AttachmentProcessingException;
    ProcessedAttachmentDto process(StoredAttachmentDto attachment) throws AttachmentProcessingException;
}
