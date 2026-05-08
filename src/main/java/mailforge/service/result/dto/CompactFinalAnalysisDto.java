package mailforge.service.result.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import mailforge.service.ai.dto.output.AiAnalysisResultDto;
import mailforge.service.quality.dto.QualityMetricsDto;

import java.util.List;

@Introspected
@Serdeable
public record CompactFinalAnalysisDto(
        CompactFinalEmailAnalysisDto email,
        List<CompactFinalAttachmentAnalysisDto> attachments,
        AiAnalysisResultDto aiAnalysis,
        QualityMetricsDto qualityMetrics
) {}
