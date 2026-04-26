package mailforge.service.quality.dto;

import mailforge.service.process.dto.ProcessingMode;

import java.util.List;

public record GroundTruthAttachmentDto(
        String filename,
        String mimeType,
        ProcessingMode expectedProcessingMode,
        boolean textExtractionExpected,
        List<String> expectedTextSnippets
) {}
