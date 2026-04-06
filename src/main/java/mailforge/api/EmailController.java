package mailforge.api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import mailforge.service.parse.EmailParsingService;
import mailforge.service.PythonProcessService;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.parse.error.EmailParsingError;
import mailforge.service.process.AttachmentProcessingService;
import mailforge.service.process.dto.ProcessedAttachmentDto;
import mailforge.service.storage.AttachmentStorageService;
import mailforge.service.storage.dto.StoredAttachmentDto;
import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller("/email")
public class EmailController {

    private final EmailParsingService emailParsingService;
    private final PythonProcessService pythonProcessService;
    private final AttachmentStorageService attachmentStorageService;
    private final AttachmentProcessingService attachmentProcessingService;

    public EmailController(EmailParsingService emailParsingService, PythonProcessService pythonProcessService, AttachmentStorageService attachmentStorageService, AttachmentProcessingService attachmentProcessingService) {
        this.emailParsingService = emailParsingService;
        this.pythonProcessService = pythonProcessService;
        this.attachmentStorageService = attachmentStorageService;
        this.attachmentProcessingService = attachmentProcessingService;
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

            return HttpResponse.ok(parsedAttachments);
        } catch (EmailParsingError e) {
            return HttpResponse.serverError("Failed to parse email: " + e.getMessage());
        }
        catch (Exception e){
            return HttpResponse.serverError("Failed to analyze  email: " + e.getMessage());
        }
    }

    @Post(value = "/analyze/contacts", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<?> analyzeContacts(CompletedFileUpload file) {
        if(file == null || file.getSize() == 0) {
            return HttpResponse.badRequest("No file uploaded");
        }
        if (!"eml".equalsIgnoreCase(FilenameUtils.getExtension(file.getFilename()))) {
            return HttpResponse.badRequest("Only .eml files are supported");
        }

        try (InputStream inputStream = file.getInputStream()){
            ParsedEmailDto parsedEmail = emailParsingService.parse(inputStream);
            pythonProcessService.analyze(parsedEmail);
            return HttpResponse.ok();
        } catch (EmailParsingError e) {
            return HttpResponse.serverError("Failed to parse email: " + e.getMessage());
        }
        catch (Exception e){
            return HttpResponse.serverError("Failed to analyze  email: " + e.getMessage());
        }
    }
}
