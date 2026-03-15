package mailforge.api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;
import mailforge.service.EmailParsingService;
import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;

@Controller("/email")
public class EmailController {

    @Inject
    EmailParsingService emailParsingService;

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
