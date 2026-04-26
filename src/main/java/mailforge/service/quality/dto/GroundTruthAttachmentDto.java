package mailforge.service.quality.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import mailforge.service.process.dto.ProcessingMode;

import java.util.List;

@Introspected
@Serdeable
public record GroundTruthAttachmentDto(
        String filename,
        String mimeType,
        ProcessingMode expectedProcessingMode,
        boolean textExtractionExpected,
        List<String> expectedTextSnippets
) {}
