package mailforge.service.process.error;

public class PdfExtractionException extends ExtractionException{
    public PdfExtractionException(String message) {
        super(message);
    }

    public PdfExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
