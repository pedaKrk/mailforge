package mailforge.service.process.error;

public class OcrExtractionServiceException extends ExtractionException {

    public OcrExtractionServiceException(String message) {
        super(message);
    }

    public OcrExtractionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
