package mailforge.service.error;

public class EmailParsingError extends Exception {
    public EmailParsingError(String message) {
        super(message);
    }
    public EmailParsingError(String message, Throwable cause) {
        super(message, cause);
    }
}
