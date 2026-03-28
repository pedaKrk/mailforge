package mailforge.service;

import mailforge.service.parse.dto.ParsedEmailDto;

import java.io.IOException;

public interface PythonProcessService {
    void analyze(ParsedEmailDto parsedEmail) throws InterruptedException, IOException;
}
