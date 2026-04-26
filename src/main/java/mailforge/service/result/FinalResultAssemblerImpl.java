package mailforge.service.result;

import jakarta.inject.Singleton;
import mailforge.service.ai.dto.output.AiAnalysisResultDto;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.quality.dto.QualityMetricsDto;
import mailforge.service.result.dto.FinalAttachmentAnalysisDto;
import mailforge.service.result.dto.FinalAnalysisDto;
import mailforge.service.result.dto.FinalEmailAnalysisDto;

import java.util.List;

@Singleton
public class FinalResultAssemblerImpl implements FinalResultAssembler{

    @Override
    public FinalAnalysisDto assemble(ParsedEmailDto email, List<ProcessedAttachmentDto> attachments, AiAnalysisResultDto aiAnalysis, QualityMetricsDto qualityMetrics) {
        return new FinalAnalysisDto(
                new FinalEmailAnalysisDto(email.headers()),
                attachments.stream().map(attachment -> new FinalAttachmentAnalysisDto(
                        attachment.attachmentId(),
                        attachment.filename(),
                        attachment.mimeType(),
                        attachment.sizeBytes(),
                        attachment.inline(),
                        attachment.contentId(),
                        attachment.contentDisposition(),
                        attachment.contentTransferEncoding(),
                        attachment.sha256(),
                        attachment.storagePath(),
                        attachment.processingMode(),
                        attachment.extractionSuccessful(),
                        attachment.ocrApplied(),
                        attachment.warnings()
                )).toList(),
                aiAnalysis,
                qualityMetrics);
    }
}
