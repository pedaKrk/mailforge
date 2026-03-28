package mailforge.service.process.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public enum ProcessingMode {
    TEXT,
    PDF,
    OCR,
    EMPTY,
    SKIP,
    UNSUPPORTED
}
