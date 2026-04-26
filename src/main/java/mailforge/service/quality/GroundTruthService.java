package mailforge.service.quality;

import mailforge.service.quality.dto.GroundTruthDto;

import java.util.Optional;

public interface GroundTruthService {
    Optional<GroundTruthDto> findByMessageId(String messageId);
}
