package mailforge.service.quality;

import mailforge.service.ai.dto.output.AiAnalysisResultDto;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.quality.dto.GroundTruthDto;
import mailforge.service.quality.dto.QualityMetricsDto;
import mailforge.service.quality.error.QualityMetricsException;

import java.util.List;

public interface QualityMetricsService {
    QualityMetricsDto evaluate(ParsedEmailDto email, List<ProcessedAttachmentDto> attachments, AiAnalysisResultDto aiResult, GroundTruthDto groundTruth) throws QualityMetricsException;
}
