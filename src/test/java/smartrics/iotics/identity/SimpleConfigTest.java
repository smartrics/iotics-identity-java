package smartrics.iotics.identity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SimpleConfigTest {

    @Test
    public void initialisesFromJSONWithNoKeyID() throws FileNotFoundException {
        SimpleConfig conf = SimpleConfig.readConf(Path.of("src/test/resources/simpleConfig_noId.json"));
        assertTrue(conf.isValid());
        assertEquals("a seed", conf.seed());
        assertEquals("a key name", conf.keyName());
        assertTrue(conf.keyId().startsWith("#id-"));
    }

    @Test
    public void initialisesFromJSONWithKeyID() throws FileNotFoundException {
        SimpleConfig conf = SimpleConfig.readConf(Path.of("src/test/resources/simpleConfig_withId.json"));
        assertTrue(conf.isValid());
        assertEquals("a seed", conf.seed());
        assertEquals("a key name", conf.keyName());
        assertTrue(conf.keyId().startsWith("#a-key-id"));
    }

    @Test
    public void wontInitialisesFromJSONWithNoSeed() {
        assertThrows(RuntimeException.class, () -> SimpleConfig.readConf(Path.of("src/test/resources/simpleConfig_noSeed.json")));
    }
}