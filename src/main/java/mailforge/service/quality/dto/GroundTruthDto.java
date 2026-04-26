package mailforge.service.quality.dto;

import java.util.List;

public record GroundTruthDto(
        GroundTruthEmailHeaderDto emailHeader,
        List<GroundTruthAttachmentDto> attachments
) {}
