package mailforge.service.process;

import mailforge.service.process.error.ExtractionException;
import mailforge.service.storage.dto.StoredAttachmentDto;

public interface ExtractionService {
    String extract(StoredAttachmentDto attachment) throws ExtractionException;
}
