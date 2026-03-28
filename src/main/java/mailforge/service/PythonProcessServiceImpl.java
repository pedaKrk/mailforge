package mailforge.service;

import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import mailforge.service.parse.dto.ParsedEmailDto;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Singleton
public class PythonProcessServiceImpl implements PythonProcessService{

    private final ObjectMapper objectMapper;

    public PythonProcessServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void analyze(ParsedEmailDto parsedEmail) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "python",
                "src/main/java/mailforge/python/contacts/main.py"
        );

        Process process = pb.start();

        // JSON an Python schicken
        try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(objectMapper.writeValueAsString(parsedEmail));
            writer.flush();
        }

        // stdout lesen
        String output;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            output = reader.lines().reduce("", (a, b) -> a + b);
        }

        // stderr lesen
        String error;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
            error = reader.lines().reduce("", (a, b) -> a + b);
        }

        int exitCode = process.waitFor();

        System.out.println("STDOUT: " + output);
        System.out.println("STDERR: " + error);
        System.out.println("EXIT: " + exitCode);
    }
}
