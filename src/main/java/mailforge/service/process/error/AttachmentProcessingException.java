package mailforge.service.process.error;

public class AttachmentProcessingException extends RuntimeException {
    public AttachmentProcessingException(String message) {
        super(message);
    }
    public AttachmentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
