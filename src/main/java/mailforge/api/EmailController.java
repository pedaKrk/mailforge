package mailforge.api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;

@Controller("/email")
public class EmailController {

    @Post(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<String> parseEmail(CompletedFileUpload file) {
        String filename = file.getFilename();

        return HttpResponse.ok("Received file: " + filename);
    }
}
