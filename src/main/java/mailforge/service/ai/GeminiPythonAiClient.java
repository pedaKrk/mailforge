package mailforge.service.ai;

import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import mailforge.service.ai.dto.input.AiEmailInputDto;
import mailforge.service.ai.dto.output.AiAnalysisResultDto;
import mailforge.service.ai.error.AiAnalysisException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Singleton
public class GeminiPythonAiClient implements AiClient{

    private final ObjectMapper objectMapper;

    public GeminiPythonAiClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public AiAnalysisResultDto analyze(AiEmailInputDto input) throws AiAnalysisException {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "python3",
                    "scripts/gemini_analyze.py"
            );

            Process process = pb.start();

            try (var stdin = process.getOutputStream()) {
                objectMapper.writeValue(stdin, input);
            }

            String stdOut;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                stdOut = reader.lines().reduce("", (a, b) -> a + b);
            }


            String stdError;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                stdError = reader.lines().reduce("", (a, b) -> a + b);
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new AiAnalysisException("Gemini script failed: " + stdError);
            }

            return objectMapper.readValue(stdOut, AiAnalysisResultDto.class);

        } catch (InterruptedException | IOException e) {
            throw new AiAnalysisException("AI analysis failed", e);
        }
    }
}
