package mailforge.service.process.error;

public abstract class ExtractionException extends Exception {
    public ExtractionException(String message) {
        super(message);
    }
    public ExtractionException(String message, Throwable cause) {
    super(message, cause);
  }
}
