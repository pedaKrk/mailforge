package mailforge.service.quality.error;

public class QualityMetricsException extends RuntimeException {
    public QualityMetricsException(String message) {
        super(message);
    }
    public QualityMetricsException(String message, Throwable cause) {
        super(message, cause);
    }
}
