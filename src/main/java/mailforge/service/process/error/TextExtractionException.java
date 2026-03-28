package mailforge.service.process.error;

public class TextExtractionException extends ExtractionException {
    public TextExtractionException(String message) {
        super(message);
    }
    public TextExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
