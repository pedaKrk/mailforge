package mailforge.service.quality.dto;

import mailforge.service.process.dto.ProcessingMode;

public record GroundTruthAttachmentDto(
        String filename,
        String mimeType,
        ProcessingMode expectedProcessingMode
) {}
