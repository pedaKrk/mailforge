package mailforge.service.parse;

import jakarta.inject.Singleton;
import mailforge.service.parse.dto.BodyParserResult;
import mailforge.service.parse.dto.ParsedAttachmentDto;
import mailforge.service.parse.error.EmailParsingError;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class BodyParserImpl implements BodyParser {

    public BodyParserResult parse(Entity entity) throws EmailParsingError {
        MutableBodyParserResult result = new MutableBodyParserResult();
        parseInto(entity, result);
        return result.toImmutable();
    }

    private void parseInto(Entity entity, MutableBodyParserResult result) throws EmailParsingError {
        if(entity.isMultipart()){
            Multipart multipart = (Multipart) entity.getBody();
            for(Entity part : multipart.getBodyParts()){
                parseInto(part, result);
            }
            return;
        }

        String mimeType = entity.getMimeType();
        String filename = entity.getFilename();
        boolean inline = isInlineDisposition(entity);

        if (isAttachment(entity)){
            byte[] data = readBinaryBody(entity);
            result.attachments.add(new ParsedAttachmentDto(
                    filename,
                    mimeType,
                    data.length,
                    inline,
                    data
            ));
            return;
        }

        if(entity.getBody() instanceof TextBody textBody){
            String content = readTextBody(textBody);
            if("text/plain".equalsIgnoreCase(mimeType)){
                result.textBody = content;
            } else if ("text/html".equalsIgnoreCase(mimeType)){
                result.htmlBody = content;
            }
        }
    }

    private String readTextBody(TextBody textBody) throws EmailParsingError {
        try (Reader reader = textBody.getReader(); Writer writer = new StringWriter()) {
            reader.transferTo(writer);
            return writer.toString();
        } catch (IOException e) {
            throw new EmailParsingError("Error reading Text body: " + e.getMessage(), e);
        }
    }

    private byte[] readBinaryBody(Entity entity) throws EmailParsingError {
        try {
            if (entity.getBody() instanceof BinaryBody binaryBody) {
                try (InputStream inputStream = binaryBody.getInputStream()) {
                    return inputStream.readAllBytes();
                }
            }
            throw new EmailParsingError("Attachment body is not a BinaryBody");
        }
        catch (IOException e) {
            throw new EmailParsingError("Error reading attachment body: " + e.getMessage(), e);
        }
    }

    private boolean isAttachment(Entity entity){
        String filename = entity.getFilename();
        if(filename != null && !filename.isBlank()){
            return true;
        }
        String disposition = entity.getDispositionType();
        return disposition != null && disposition.equalsIgnoreCase("attachment");
    }

    private boolean isInlineDisposition(Entity entity){
        String disposition = entity.getDispositionType();
        return disposition != null && disposition.equalsIgnoreCase("inline");
    }

    private static class MutableBodyParserResult {
        private String textBody;
        private String htmlBody;
        private final List<ParsedAttachmentDto> attachments = new ArrayList<>();

        BodyParserResult toImmutable() {
            return new BodyParserResult(textBody, htmlBody, List.copyOf(attachments));
        }
    }
}
