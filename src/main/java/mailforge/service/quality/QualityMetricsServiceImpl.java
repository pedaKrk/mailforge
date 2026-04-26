package mailforge.service.quality;

import jakarta.inject.Singleton;
import mailforge.service.ai.dto.output.AiAnalysisResultDto;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.parse.dto.ParsedHeaderDto;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.quality.dto.GroundTruthDto;
import mailforge.service.quality.dto.GroundTruthEmailHeaderDto;
import mailforge.service.quality.dto.QualityMetricsDto;
import mailforge.service.quality.error.QualityMetricsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Singleton
public class QualityMetricsServiceImpl implements QualityMetricsService{
    @Override
    public QualityMetricsDto evaluate(ParsedEmailDto email, List<ProcessedAttachmentDto> attachments, AiAnalysisResultDto aiResult, GroundTruthDto groundTruth) throws QualityMetricsException {
        List<String> warnings = new ArrayList<>();

        double metadataCompleteness = calculateMetadataCompleteness(email.headers(), groundTruth.emailHeader());

        return new QualityMetricsDto(
                true,
                metadataCompleteness,
                null,
                null,
                null,
                null,
                warnings
        );
    }

    private Double calculateMetadataCompleteness(ParsedHeaderDto header, GroundTruthEmailHeaderDto expected){
        int matched = 0;

        if (equalsNormalized(header.messageId(), expected.messageId())) matched++;
        if (equalsNormalized(header.subject(), expected.subject())) matched++;
        if (Objects.equals(header.date(), expected.date())) matched++;
        if (equalsNormalized(header.sender(), expected.sender())) matched++;
        if (listEqualsNormalized(header.from(), expected.from())) matched++;
        if (listEqualsNormalized(header.to(), expected.to())) matched++;
        if (listEqualsNormalized(header.cc(), expected.cc())) matched++;
        if (listEqualsNormalized(header.bcc(), expected.bcc())) matched++;
        if (listEqualsNormalized(header.replyTo(), expected.replyTo())) matched++;

        return matched / (double) GroundTruthEmailHeaderDto.class.getRecordComponents().length;
    }

    private boolean equalsNormalized(String a, String b) {
        return normalize(a).equals(normalize(b));
    }

    private boolean listEqualsNormalized(List<String> a, List<String> b) {
        List<String> normalizedA = normalizeList(a);
        List<String> normalizedB = normalizeList(b);

        return normalizedA.equals(normalizedB);
    }

    private List<String> normalizeList(List<String> values) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
                .filter(Objects::nonNull)
                .map(this::normalize)
                .sorted()
                .toList();
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.toLowerCase().replaceAll("\\s+", " ").trim();
    }
}
