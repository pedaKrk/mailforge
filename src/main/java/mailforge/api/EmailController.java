package mailforge.api;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller("/email")
public class EmailController {

    @Get
    @Produces(MediaType.TEXT_HTML)
    public String index(){
        return "Hello World";
    }
}
