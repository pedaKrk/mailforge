package mailforge.service.quality.dto;

import java.util.List;
import java.util.Map;

public record QualityMetricsDto(
        boolean evaluated,
        Double metadataCompleteness,
        Double parsingAccuracy,
        Map<String, Double> attachmentExtractionAccuracy,
        Map<String, Double> textExtractionSuccess,
        List<String> warnings
) {
    public static QualityMetricsDto skipped(String reason) {
        return new QualityMetricsDto(
                false,
                null,
                null,
                null,
                null,
                List.of(reason)
        );
    }
}
