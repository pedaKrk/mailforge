package mailforge.service.result.dto;

import mailforge.service.ai.dto.output.AiAnalysisResultDto;
import mailforge.service.quality.dto.QualityMetricsDto;

import java.util.List;

public record CompactFinalAnalysisDto(
        CompactFinalEmailAnalysisDto email,
        List<CompactFinalAttachmentAnalysisDto> attachments,
        AiAnalysisResultDto aiAnalysis,
        QualityMetricsDto qualityMetrics
) {}
