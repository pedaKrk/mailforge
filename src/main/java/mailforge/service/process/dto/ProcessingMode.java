package mailforge.service.process.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

@Introspected
@Serdeable
public enum ProcessingMode {
    DIRECT_TEXT,
    PDF_TEXT,
    OCR_IMAGE,
    EMPTY,
    SKIP,
    UNSUPPORTED
}
