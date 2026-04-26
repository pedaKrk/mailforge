package mailforge.service.quality;

import io.micronaut.context.annotation.Value;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import mailforge.service.quality.dto.GroundTruthDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Singleton
public class GroundTruthServiceImpl implements GroundTruthService{

    private final Path datapath;
    private final ObjectMapper objectMapper;

    public GroundTruthServiceImpl(@Value("${ground.truth.datapath}") String datapath, ObjectMapper objectMapper) {
        this.datapath = Path.of(datapath);
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<GroundTruthDto> findByMessageId(String messageId) {
        Path filePath = datapath.resolve(sanitizeMessageId(messageId) + ".json");

        if (!Files.exists(filePath)){
            return Optional.empty();
        }

        try{
            GroundTruthDto groundTruth = objectMapper.readValue(Files.readAllBytes(filePath), GroundTruthDto.class);
            return Optional.of(groundTruth);
        } catch (IOException e) {
            log.error("error reading ground truth for {}", messageId);
            return Optional.empty();
        }
    }

    private String sanitizeMessageId(String messageId){
        if(messageId == null){
            return "";
        }
        return messageId.replaceAll("[<>]", "");
    }
}
