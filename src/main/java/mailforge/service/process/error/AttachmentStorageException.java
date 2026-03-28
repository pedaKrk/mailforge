package mailforge.service.process.error;

public class AttachmentStorageException extends Exception {
    public AttachmentStorageException(String message) {
        super(message);
    }
    public AttachmentStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
