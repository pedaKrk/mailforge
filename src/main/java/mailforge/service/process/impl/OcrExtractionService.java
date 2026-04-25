package mailforge.service.process.impl;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import mailforge.service.process.ExtractionService;
import mailforge.service.process.error.ExtractionException;
import mailforge.service.process.error.OcrExtractionServiceException;
import mailforge.service.storage.dto.StoredAttachmentDto;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.nio.file.Path;

@Singleton
public class OcrExtractionService implements ExtractionService {

    private final Tesseract tesseract;

    public OcrExtractionService(@Value("${ocr.datapath}") String datapath, @Value("${ocr.language}") String language) {
        this.tesseract = new Tesseract();
        tesseract.setDatapath(datapath);
        tesseract.setLanguage(language);
    }

    @Override
    public String extract(StoredAttachmentDto attachment) throws ExtractionException {
        try{
            File file = Path.of(attachment.storagePath()).toFile();
            String text = tesseract.doOCR(file);
            return text == null ? "" : text.trim();
        } catch (TesseractException e) {
            throw new OcrExtractionServiceException("Error while performing OCR: " + e.getMessage(), e);
        }
    }
}
