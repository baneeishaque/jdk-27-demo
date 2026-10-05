package io.github.baneeishaque.jdk27.jfr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * JEP 536 — sensitive values never reach the recording in clear text.
 */
class JfrRedactionTest {

    @Test
    void systemPropertyValuesAreRedacted(@TempDir Path directory) throws Exception {
        Path recording = JfrRedaction.recordChild(directory);
        assertEquals(List.of("[REDACTED]"),
                JfrRedaction.recordedSystemProperty(recording, "db.password"));
    }

    @Test
    void rawSecretDoesNotAppearAnywhere(@TempDir Path directory) throws Exception {
        Path recording = JfrRedaction.recordChild(directory);
        assertFalse(JfrRedaction.containsRawSecret(recording));
    }
}
