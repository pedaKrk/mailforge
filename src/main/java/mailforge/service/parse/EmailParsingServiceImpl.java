package mailforge.service.parse;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import mailforge.service.parse.dto.BodyParserResult;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.parse.error.EmailParsingError;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.address.*;
import org.apache.james.mime4j.message.DefaultMessageBuilder;

import java.io.*;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class EmailParsingServiceImpl implements EmailParsingService {

    @Inject
    private BodyParser bodyParser;

    @Override
    public ParsedEmailDto parse(InputStream fileStream) throws EmailParsingError {
        DefaultMessageBuilder defaultMessageBuilder = new DefaultMessageBuilder();

        try{
            Message message = defaultMessageBuilder.parseMessage(fileStream);
            BodyParserResult bodyParserResult = bodyParser.parse(message);

            return new ParsedEmailDto(
                    message.getSubject(),
                    extractFrom(message.getFrom()),
                    extractAddresses(message.getTo()),
                    extractAddresses(message.getCc()),
                    extractAddresses(message.getBcc()),
                    message.getMessageId(),
                    extractDate(message.getDate()),
                    bodyParserResult.textBody(),
                    bodyParserResult.htmlBody(),
                    bodyParserResult.attachments()
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
}
