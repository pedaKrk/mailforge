package mailforge.service.quality.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Introspected
@Serdeable
public record GroundTruthDto(
        GroundTruthEmailHeaderDto emailHeader,
        List<GroundTruthAttachmentDto> attachments
) {}
