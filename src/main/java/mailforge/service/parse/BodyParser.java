package mailforge.service.parse;

import mailforge.service.parse.dto.BodyParserResult;
import mailforge.service.parse.error.EmailParsingError;
import org.apache.james.mime4j.dom.Entity;

public interface BodyParser {
    BodyParserResult parse(Entity entity) throws EmailParsingError;
}
