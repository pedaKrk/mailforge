package mailforge.service.quality.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.Map;

@Introspected
@Serdeable
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
