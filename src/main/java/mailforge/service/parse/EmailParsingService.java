package mailforge.service.parse;

import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.parse.error.EmailParsingError;

import java.io.InputStream;

public interface EmailParsingService {
    ParsedEmailDto parse(InputStream fileStream) throws EmailParsingError;
}
