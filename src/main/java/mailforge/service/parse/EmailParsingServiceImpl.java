package mailforge.service.parse;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import mailforge.service.parse.dto.BodyParserResult;
import mailforge.service.parse.dto.ParsedBodyDto;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.parse.dto.ParsedHeaderDto;
import mailforge.service.parse.error.EmailParsingError;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.address.*;
import org.apache.james.mime4j.message.DefaultMessageBuilder;

import java.io.*;
import java.util.ArrayList;
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

            var headers = new ParsedHeaderDto(
                    message.getMessageId(),
                    message.getSubject(),
                    message.getDate().toInstant(),
                    extractMailbox(message.getSender()),
                    extractMailboxList(message.getFrom()),
                    extractAddressList(message.getTo()),
                    extractAddressList(message.getCc()),
                    extractAddressList(message.getBcc()),
                    extractAddressList(message.getReplyTo())
            );

            var body = new ParsedBodyDto(
                    bodyParserResult.textBody(),
                    bodyParserResult.htmlBody()
            );

            return new ParsedEmailDto(
                    headers,
                    body,
                    bodyParserResult.attachments()
            );
        } catch (IOException e) {
            throw new EmailParsingError("Failed to parse EML file: " + e.getMessage(), e);
        }
    }

    private List<String> extractMailboxList(MailboxList mailboxList){
        List<String> addresses = new ArrayList<>();
        for(Mailbox mailbox : mailboxList){
            addresses.add(extractMailbox(mailbox));
        }
        return addresses;
    }

    private String extractMailbox(Mailbox mailbox){
        return mailbox.getAddress();
    }

    private List<String> extractAddressList(AddressList addressList){
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
}
