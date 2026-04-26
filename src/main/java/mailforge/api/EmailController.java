package mailforge.api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import mailforge.service.ai.AiClient;
import mailforge.service.ai.AiInputPrepareService;
import mailforge.service.ai.dto.input.AiEmailInputDto;
import mailforge.service.ai.dto.output.AiAnalysisResultDto;
import mailforge.service.parse.EmailParsingService;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.parse.error.EmailParsingError;
import mailforge.service.process.AttachmentProcessingService;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.quality.GroundTruthService;
import mailforge.service.quality.QualityMetricsService;
import mailforge.service.quality.dto.GroundTruthDto;
import mailforge.service.quality.dto.QualityMetricsDto;
import mailforge.service.storage.AttachmentStorageService;
import mailforge.service.storage.dto.StoredAttachmentDto;
import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller("/email")
public class EmailController {

    private final EmailParsingService emailParsingService;
    private final AttachmentStorageService attachmentStorageService;
    private final AttachmentProcessingService attachmentProcessingService;
    private final AiInputPrepareService aiInputPrepareService;
    private final AiClient aiClient;
    private final GroundTruthService groundTruthService;
    private final QualityMetricsService qualityMetricsService;

    public EmailController(EmailParsingService emailParsingService, AttachmentStorageService attachmentStorageService, AttachmentProcessingService attachmentProcessingService, AiInputPrepareService aiInputPrepareService, AiClient aiClient, GroundTruthService groundTruthService, QualityMetricsService qualityMetricsService) {
        this.emailParsingService = emailParsingService;
        this.attachmentStorageService = attachmentStorageService;
        this.attachmentProcessingService = attachmentProcessingService;
        this.aiInputPrepareService = aiInputPrepareService;
        this.aiClient = aiClient;
        this.groundTruthService = groundTruthService;
        this.qualityMetricsService = qualityMetricsService;
    }

    @Post(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<?> analyze(CompletedFileUpload file) {
        if(file == null || file.getSize() == 0) {
            return HttpResponse.badRequest("No file uploaded");
        }
        if (!"eml".equalsIgnoreCase(FilenameUtils.getExtension(file.getFilename()))) {
            return HttpResponse.badRequest("Only .eml files are supported");
        }

        try (InputStream inputStream = file.getInputStream()){
            ParsedEmailDto parsedEmail = emailParsingService.parse(inputStream);

            List<ProcessedAttachmentDto> parsedAttachments = new ArrayList<>();
            for(var attachment : parsedEmail.attachments()){
                StoredAttachmentDto storedAttachment = attachmentStorageService.store(attachment);
                ProcessedAttachmentDto processedAttachment = attachmentProcessingService.process(storedAttachment);

                parsedAttachments.add(processedAttachment);
            }

            AiEmailInputDto aiEmailInput = aiInputPrepareService.prepare(parsedEmail, parsedAttachments);
            AiAnalysisResultDto aiAnalysisResult = aiClient.analyze(aiEmailInput);

            Optional<GroundTruthDto> groundTruth = groundTruthService.findByMessageId(parsedEmail.headers().messageId());
            QualityMetricsDto qualityMetricsDto = groundTruth
                    .map(gt -> qualityMetricsService.evaluate(parsedEmail, parsedAttachments, aiAnalysisResult, gt))
                    .orElseGet(() -> QualityMetricsDto.skipped("No ground truth found for messageId: " + parsedEmail.headers().messageId()));
            /*
             Todo:
                - implement logging
                - compare with ground truth and create Metrics
                - finalize result and return
            */

            return HttpResponse.ok(aiAnalysisResult);
        } catch (EmailParsingError e) {
            return HttpResponse.serverError("Failed to parse email: " + e.getMessage());
        }
        catch (Exception e){
            return HttpResponse.serverError("Failed to analyze  email: " + e.getMessage());
        }
    }

    @Post(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<?> parseEmail(CompletedFileUpload file) {
        if(file == null || file.getSize() == 0) {
            return HttpResponse.badRequest("No file uploaded");
        }
        if (!"eml".equalsIgnoreCase(FilenameUtils.getExtension(file.getFilename()))) {
            return HttpResponse.badRequest("Only .eml files are supported");
        }

        try (InputStream inputStream = file.getInputStream()){
            return HttpResponse.ok(emailParsingService.parse(inputStream));
        } catch (Exception e){
            return HttpResponse.serverError("Failed to parse email: " + e.getMessage());
        }
    }
}
