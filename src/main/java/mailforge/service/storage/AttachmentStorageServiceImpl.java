package mailforge.service.storage;

import jakarta.inject.Singleton;
import mailforge.service.parse.dto.ParsedAttachmentDto;
import mailforge.service.storage.dto.StoredAttachmentDto;
import mailforge.service.storage.error.AttachmentStorageException;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Singleton
public class AttachmentStorageServiceImpl implements AttachmentStorageService {
    private final Path baseDirectory = Path.of("build", "mailforge-attachments");

    @Override
    public StoredAttachmentDto store(ParsedAttachmentDto attachment) throws AttachmentStorageException {
        try{
            if (Files.notExists(this.baseDirectory)) {
                Files.createDirectories(this.baseDirectory);
            }

            String attachmentId = UUID.randomUUID().toString();
            Path targetPath = this.baseDirectory.resolve(attachmentId + "." + FilenameUtils.getExtension(attachment.filename()));
            String sha256 = calculateSha256(attachment.content());

            Files.write(targetPath, attachment.content());

            return new StoredAttachmentDto(
                    attachmentId,
                    attachment.filename(),
                    attachment.mimeType(),
                    attachment.size(),
                    attachment.inline(),
                    sha256,
                    targetPath.toAbsolutePath().toString()
            );
        } catch (IOException e) {
            throw new AttachmentStorageException("Error writing Attachment to Filesystem: " + e.getMessage(), e);
        }
    }

    private String calculateSha256(byte[] content) throws AttachmentStorageException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new AttachmentStorageException("Error calculating hash of Attachment: " + e.getMessage(), e);
        }
    }
}
