package mailforge.service;

import mailforge.service.dto.AttachmentDto;
import mailforge.service.dto.ParsedEmailDto;
import mailforge.service.error.EmailParsingError;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.dom.address.*;
import org.apache.james.mime4j.message.DefaultMessageBuilder;

import java.io.*;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmailParsingServiceImpl implements EmailParsingService{
    @Override
    public ParsedEmailDto parse(InputStream fileStream) throws EmailParsingError {
        DefaultMessageBuilder defaultMessageBuilder = new DefaultMessageBuilder();
        BodyParser bodyParser = new BodyParser();

        try{
            Message message = defaultMessageBuilder.parseMessage(fileStream);
            bodyParser.parse(message);

            return new ParsedEmailDto(
                    message.getSubject(),
                    extractFrom(message.getFrom()),
                    extractAddresses(message.getTo()),
                    extractAddresses(message.getCc()),
                    extractAddresses(message.getBcc()),
                    message.getMessageId(),
                    extractDate(message.getDate()),
                    bodyParser.getTextBody(),
                    bodyParser.getHtmlBody(),
                    bodyParser.getAttachments()
            );
        } catch (IOException e) {
            throw new EmailParsingError("Failed to parse EML file: " + e.getMessage(), e);
        }
    }

    private String extractFrom(MailboxList mailboxList){
        if(mailboxList == null || mailboxList.isEmpty()){
            return null;
        }
        return mailboxList.getFirst().getAddress();
    }

    private List<String> extractAddresses(AddressList addressList){
        List<String> result = new ArrayList<>();
        if(addressList == null){
            return result;
        }

        for(Address address : addressList){
            if(address instanceof Mailbox mailbox){
                result.add(mailbox.getAddress());
            } else if (address instanceof Group group){
                for (Mailbox mailbox : group.getMailboxes()){
                    result.add(mailbox.getAddress());
                }
            }
        }
        return result;
    }

    private String extractDate(Date date){
        if(date == null){
            return null;
        }
        return date.toInstant().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private static class BodyParser{
        private String textBody;
        private String htmlBody;

        public String getTextBody() {
            return textBody;
        }

        public String getHtmlBody() {
            return htmlBody;
        }

        public List<AttachmentDto> getAttachments() {
            return attachments;
        }

        private final List<AttachmentDto> attachments = new ArrayList<>();

        public void parse(Entity entity) throws EmailParsingError {
            if(entity.isMultipart()){
                Multipart multipart = (Multipart) entity.getBody();
                for(Entity part : multipart.getBodyParts()){
                    parse(part);
                }
                return;
            }

            String mimeType = entity.getMimeType();
            String filename = entity.getFilename();
            boolean inline = isInlineDisposition(entity);

            if (isAttachment(entity)){
                this.attachments.add(new AttachmentDto(
                        filename,
                        mimeType,
                        0,
                        inline
                ));
                return;
            }

            if(entity.getBody() instanceof TextBody textBody){
                String content = readTextBody(textBody);
                if("text/plain".equalsIgnoreCase(mimeType)){
                    this.textBody = content;
                } else if ("text/html".equalsIgnoreCase(mimeType)){
                    this.htmlBody = content;
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
    }
}
