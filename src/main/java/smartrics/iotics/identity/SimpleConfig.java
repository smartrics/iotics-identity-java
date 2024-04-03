package smartrics.iotics.identity;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Objects of this class can be used by clients to load identitconfig
 */
public record SimpleConfig(String seed, String keyName, String keyId) {

    public SimpleConfig(String seed, String keyName) {
        this(seed, requireNonNull(keyName), null);
    }

    public SimpleConfig(String seed, String keyName, String keyId) {
        this.seed = requireNonNull(seed);
        this.keyName = requireNonNull(keyName);
        this.keyId = Optional.ofNullable(keyId).orElse("#id-" + keyName.hashCode());
    }

    /**
     * This method reads the config from the env. The var name
     * is obtained by concatenating a prefix with the word "SEED" for the seed, and the word "KEYNAME" for the keyname
     *
     * @param prefix a prefix for the env variables being read to get values for this config.
     * @return the config
     */
    public static SimpleConfig fromEnv(String prefix) {
        return new SimpleConfig(
                System.getenv(prefix + "SEED"),
                System.getenv(prefix + "KEYNAME"),
                System.getenv(prefix + "KEYID"));
    }

    public static SimpleConfig readConf(Path p) throws FileNotFoundException {
        Gson gson = new Gson();
        Reader reader = Files.newReader(p.toFile(), StandardCharsets.UTF_8);
        return gson.fromJson(reader, SimpleConfig.class);
    }

    /**
     * Reads the config from a file in ${user.home}/.config/iotics/{name}
     *
     * @param name the config file name
     * @return the config
     * @throws FileNotFoundException if file not found
     */
    public static SimpleConfig readConfFromHome(String name) throws FileNotFoundException {
        Path p = Paths.get(System.getProperty("user.home"), ".config", "iotics", name);
        return readConf(p);
    }

    public String seed() {
        return seed;
    }

    public String keyName() {
        return keyName;
    }

    public String keyId() {
        return keyId;
    }

    public boolean isValid() {
        return !Strings.isNullOrEmpty(this.seed) && !Strings.isNullOrEmpty(this.keyName) && !Strings.isNullOrEmpty(this.keyId);
    }

}
