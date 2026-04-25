package mailforge.service.ai;

import jakarta.inject.Singleton;
import mailforge.service.ai.dto.input.AiAttachmentInputDto;
import mailforge.service.ai.dto.input.AiBodyInputDto;
import mailforge.service.ai.dto.input.AiEmailInputDto;
import mailforge.service.ai.dto.input.AiHeaderInputDto;
import mailforge.service.parse.dto.ParsedEmailDto;
import mailforge.service.process.dto.ProcessedAttachmentDto;

import java.util.List;

@Singleton
public class AiInputPrepareServiceImpl implements AiInputPrepareService{
    @Override
    public AiEmailInputDto prepare(ParsedEmailDto email, List<ProcessedAttachmentDto> attachments) {
        return new AiEmailInputDto(
                new AiHeaderInputDto(
                        email.headers().messageId(),
                        email.headers().subject(),
                        email.headers().date(),
                        email.headers().sender(),
                        email.headers().from(),
                        email.headers().to(),
                        email.headers().cc(),
                        email.headers().bcc(),
                        email.headers().replyTo()
                ),
                new AiBodyInputDto(
                        email.body().textBody(),
                        email.body().htmlBody()
                ),
                attachments.stream().map(attachment -> new AiAttachmentInputDto(
                        attachment.attachmentId(),
                        attachment.filename(),
                        attachment.mimeType(),
                        attachment.extractedText()
                )).toList()
        );
    }
}
