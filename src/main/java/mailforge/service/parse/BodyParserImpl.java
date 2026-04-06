package mailforge.service.parse;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import mailforge.service.parse.dto.BodyParserResult;
import mailforge.service.parse.dto.ParsedAttachmentDto;
import mailforge.service.parse.error.EmailParsingError;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.stream.Field;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Todo: mehr von RTX verwenden
@Slf4j
@Singleton
public class BodyParserImpl implements BodyParser {

    public BodyParserResult parse(Entity entity) throws EmailParsingError {
        log.debug("Starting body parsing for root entity: mimeType={}, disposition={}, filename={}, multipart={}", entity.getMimeType(), entity.getDispositionType(), entity.getFilename(), entity.isMultipart());
        MutableBodyParserResult result = new MutableBodyParserResult();
        parseInto(entity, result);
        log.debug("Finished body parsing: textBodyPresent={}, htmlBodyPresent={}, attachmentCount={}", result.textBody != null, result.htmlBody != null, result.attachments.size());
        return result.toImmutable();
    }

    private void parseInto(Entity entity, MutableBodyParserResult result) throws EmailParsingError {
        if (entity == null) {
            return;
        }

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
                    UUID.randomUUID().toString(),
                    filename,
                    mimeType,
                    data.length,
                    inline,
                    getContentId(entity),
                    entity.getDispositionType(),
                    entity.getContentTransferEncoding(),
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

    private String getContentId(Entity entity){
        if(entity.getHeader() == null){
            return null;
        }
        Field field = entity.getHeader().getField("Content-ID");
        if (field == null) {
            return null;
        }

        return field.getBody();
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
