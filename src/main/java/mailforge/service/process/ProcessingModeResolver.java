package mailforge.service.process;

import mailforge.service.process.dto.ProcessingMode;
import mailforge.service.storage.dto.StoredAttachmentDto;

public interface ProcessingModeResolver {
    ProcessingMode resolve(StoredAttachmentDto attachment);
}
