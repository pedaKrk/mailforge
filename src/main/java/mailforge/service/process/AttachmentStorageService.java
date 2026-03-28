package mailforge.service.process;

import mailforge.service.parse.dto.ParsedAttachmentDto;
import mailforge.service.process.dto.StoredAttachmentDto;
import mailforge.service.process.error.AttachmentStorageException;

public interface AttachmentStorageService {
    StoredAttachmentDto store(ParsedAttachmentDto attachment) throws AttachmentStorageException;
}
