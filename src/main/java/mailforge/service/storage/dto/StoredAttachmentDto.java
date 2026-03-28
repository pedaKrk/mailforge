package mailforge.service.storage.dto;

public record StoredAttachmentDto(
    String attachmentId,
    String originalFilename,
    String mimeType,
    long size,
    boolean inline,
    String sha256,
    String storagePath
) {}
