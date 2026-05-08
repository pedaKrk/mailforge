package mailforge.service.result;

import mailforge.service.ai.dto.output.AiAnalysisResultDto;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.quality.dto.QualityMetricsDto;
import mailforge.service.result.dto.FinalAnalysisDto;
import mailforge.service.result.dto.CompactFinalAnalysisDto;

import java.util.List;

public interface FinalResultAssembler {
    FinalAnalysisDto assemble(ParsedEmailDto email, List<ProcessedAttachmentDto> attachments, AiAnalysisResultDto aiAnalysis, QualityMetricsDto qualityMetrics);
    CompactFinalAnalysisDto assembleCompact(ParsedEmailDto email, List<ProcessedAttachmentDto> attachments, AiAnalysisResultDto aiAnalysis, QualityMetricsDto qualityMetrics);
}
