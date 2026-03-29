package mailforge.service.process.impl;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import mailforge.service.process.ExtractionService;
import mailforge.service.process.error.TextExtractionException;
import mailforge.service.storage.dto.StoredAttachmentDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class TextExtractionService implements ExtractionService {
    @Override
    public String extract(StoredAttachmentDto attachment) throws TextExtractionException {
        try{
            return Files.readString(Path.of(attachment.storagePath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TextExtractionException("Error extracting text: " + e.getMessage(), e);
        }
    }
}
