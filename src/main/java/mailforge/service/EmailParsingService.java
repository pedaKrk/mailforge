package mailforge.service;

import mailforge.service.dto.ParsedEmailDto;
import mailforge.service.error.EmailParsingError;

import java.io.InputStream;

public interface EmailParsingService {
    ParsedEmailDto parse(InputStream fileStream) throws EmailParsingError;
}
