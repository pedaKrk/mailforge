package mailforge.service.storage;

import mailforge.service.parse.dto.ParsedAttachmentDto;
import mailforge.service.storage.dto.StoredAttachmentDto;
import mailforge.service.storage.error.AttachmentStorageException;

public interface AttachmentStorageService {
    StoredAttachmentDto store(ParsedAttachmentDto attachment) throws AttachmentStorageException;
}
