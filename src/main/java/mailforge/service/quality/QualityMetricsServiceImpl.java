package mailforge.service.quality;

import jakarta.inject.Singleton;
import mailforge.service.ai.dto.output.AiAnalysisResultDto;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.parse.dto.ParsedHeaderDto;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.quality.dto.GroundTruthAttachmentDto;
import mailforge.service.quality.dto.GroundTruthDto;
import mailforge.service.quality.dto.GroundTruthEmailHeaderDto;
import mailforge.service.quality.dto.QualityMetricsDto;
import mailforge.service.quality.error.QualityMetricsException;

import java.util.*;

@Singleton
public class QualityMetricsServiceImpl implements QualityMetricsService{

    @Override
    public QualityMetricsDto evaluate(ParsedEmailDto email, List<ProcessedAttachmentDto> attachments, AiAnalysisResultDto aiResult, GroundTruthDto groundTruth) throws QualityMetricsException {
        return new QualityMetricsDto(
                true,
                calculateMetadataCompleteness(email.headers(), groundTruth.emailHeader()),
                calculateParsingAccuracy(attachments, groundTruth.attachments()),
                calculateAttachmentExtractionAccuracy(attachments, groundTruth.attachments()),
                calculateTextExtractionSuccess(attachments, groundTruth.attachments()),
                new ArrayList<>()
        );
    }

    private Double calculateMetadataCompleteness(ParsedHeaderDto actualHeader, GroundTruthEmailHeaderDto expectedHeader){
        int matched = 0;

        if (equalsNormalized(actualHeader.messageId(), expectedHeader.messageId())) matched++;
        if (equalsNormalized(actualHeader.subject(), expectedHeader.subject())) matched++;
        if (Objects.equals(actualHeader.date(), expectedHeader.date())) matched++;
        if (equalsNormalized(actualHeader.sender(), expectedHeader.sender())) matched++;
        if (listEqualsNormalized(actualHeader.from(), expectedHeader.from())) matched++;
        if (listEqualsNormalized(actualHeader.to(), expectedHeader.to())) matched++;
        if (listEqualsNormalized(actualHeader.cc(), expectedHeader.cc())) matched++;
        if (listEqualsNormalized(actualHeader.bcc(), expectedHeader.bcc())) matched++;
        if (listEqualsNormalized(actualHeader.replyTo(), expectedHeader.replyTo())) matched++;

        return matched / (double) GroundTruthEmailHeaderDto.class.getRecordComponents().length;
    }

    private Double calculateParsingAccuracy(List<ProcessedAttachmentDto> actualAttachments, List<GroundTruthAttachmentDto> expectedAttachments){
        if(expectedAttachments.isEmpty()){
            return 1.0;
        }

        List<ProcessedAttachmentDto> relevantAttachments = actualAttachments.stream().filter(attachment -> !attachment.inline()).toList();
        return relevantAttachments.size() / (double) expectedAttachments.size();
    }

    private Map<String, Double> calculateAttachmentExtractionAccuracy(List<ProcessedAttachmentDto> actualAttachments, List<GroundTruthAttachmentDto> expectedAttachments){
        Map<String, Double> attachmentExtractionAccuracy = new HashMap<>();

        if(expectedAttachments.isEmpty()){
            return attachmentExtractionAccuracy;
        }

        for(GroundTruthAttachmentDto expected : expectedAttachments) {
            ProcessedAttachmentDto actual = findByFileName(actualAttachments, expected.filename());
            if (actual == null) {
                attachmentExtractionAccuracy.put(expected.filename(), 0.0);
                continue;
            }

            int matches = 0;
            if(equalsNormalized(actual.filename(), expected.filename())) matches++;
            if(equalsNormalized(actual.mimeType(), expected.mimeType())) matches++;
            if(actual.processingMode() == expected.expectedProcessingMode()) matches++;

            attachmentExtractionAccuracy.put(expected.filename(), matches / 3.0);
        }

        return attachmentExtractionAccuracy;
    }

    private Map<String, Double> calculateTextExtractionSuccess(List<ProcessedAttachmentDto> actualAttachments, List<GroundTruthAttachmentDto> expectedAttachments){
        Map<String, Double> textExtractionSuccess = new HashMap<>();

        if(expectedAttachments.isEmpty()){
            return textExtractionSuccess;
        }

        for(GroundTruthAttachmentDto expected : expectedAttachments){
            ProcessedAttachmentDto actual = findByFileName(actualAttachments, expected.filename());
            if(actual == null){
                textExtractionSuccess.put(expected.filename(), 0.0);
                continue;
            }

            long matchedSnippets = expected.expectedTextSnippets().stream()
                    .filter(snippet -> containsNormalized(actual.extractedText(), snippet))
                    .count();

            textExtractionSuccess.put(expected.filename(), matchedSnippets / (double) expected.expectedTextSnippets().size());
        }

        return textExtractionSuccess;
    }

    private ProcessedAttachmentDto findByFileName(List<ProcessedAttachmentDto> attachments, String filename){
        return attachments.stream()
                .filter(attachment -> equalsNormalized(attachment.filename(), filename))
                .findFirst()
                .orElse(null);
    }

    private boolean equalsNormalized(String a, String b) {
        return normalize(a).equals(normalize(b));
    }

    private boolean containsNormalized(String text, String snippet) {
        return normalize(text).contains(normalize(snippet));
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
