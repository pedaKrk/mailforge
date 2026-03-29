package mailforge.service.process.impl;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import mailforge.service.process.ExtractionService;
import mailforge.service.process.error.ExtractionException;
import mailforge.service.process.error.PdfExtractionException;
import mailforge.service.storage.dto.StoredAttachmentDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Path;

@Singleton
public class PdfExtractionService implements ExtractionService {
    @Override
    public String extract(StoredAttachmentDto attachment) throws ExtractionException {
        try(PDDocument document = Loader.loadPDF(Path.of(attachment.storagePath()).toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return text == null ? "" : text.trim();
        } catch (IOException e) {
            throw new PdfExtractionException("Error extracting Text from Pdf: " + e.getMessage(), e);
        }
    }
}
