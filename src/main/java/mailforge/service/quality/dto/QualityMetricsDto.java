package mailforge.service.quality.dto;

import java.util.List;

public record QualityMetricsDto(
        boolean evaluated,
        Double metadataCompleteness,
        Double parsingAccuracy,
        Double attachmentExtractionAccuracy,
        Double textExtractionSuccess,
        Double overallScore,
        List<String> warnings
) {
    public static QualityMetricsDto skipped(String reason) {
        return new QualityMetricsDto(
                false,
                null,
                null,
                null,
                null,
                null,
                List.of(reason)
        );
    }
}
