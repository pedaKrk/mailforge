package mailforge.service.process.impl;

import io.micronaut.http.MediaType;
import jakarta.inject.Singleton;
import mailforge.service.process.ProcessingModeResolver;
import mailforge.service.process.dto.ProcessingMode;
import mailforge.service.storage.dto.StoredAttachmentDto;
import org.apache.commons.io.FilenameUtils;

@Singleton
public class ProcessingModeResolverImpl implements ProcessingModeResolver {

    private static final MediaType IMAGE_ANY = MediaType.of("image/*");

    @Override
    public ProcessingMode resolve(StoredAttachmentDto attachment) {
        if(attachment.inline()){
            return ProcessingMode.SKIP;
        }
        if(attachment.size() <= 0){
            return ProcessingMode.EMPTY;
        }

        return MediaType.forExtension(FilenameUtils.getExtension(attachment.originalFilename()))
                .map(this::mapMediaTypeToProcessingMode)
                .orElse(ProcessingMode.UNSUPPORTED);
    }

    private ProcessingMode mapMediaTypeToProcessingMode(MediaType mediaType) {
        if (mediaType.isTextBased()) {
            return ProcessingMode.TEXT;
        }

        if (mediaType.equals(MediaType.APPLICATION_PDF_TYPE)) {
            return ProcessingMode.PDF;
        }

        if (IMAGE_ANY.matches(mediaType)) {
            return ProcessingMode.OCR;
        }

        return ProcessingMode.UNSUPPORTED;
    }
}
