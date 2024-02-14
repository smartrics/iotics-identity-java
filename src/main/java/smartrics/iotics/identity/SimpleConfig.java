package smartrics.iotics.identity;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Objects of this class can be used by clients to load identitconfig
 */
public class SimpleConfig {

    private String seed;
    private String keyName;

    /**
     * This method reads the config from the env. The var name
     * is obtained by conctatenating a prefix with the word "SEED" for the seed, and the word "KEYNAME" for the keyname
     * @param prefix a prefix for the env variables being read to get values for this config.
     * @return
     */
    public static SimpleConfig fromEnv(String prefix) {
        return new SimpleConfig(System.getenv(prefix + "SEED"), System.getenv(prefix + "KEYNAME"));
    }

    public static SimpleConfig readConf(Path p) throws FileNotFoundException {
        Gson gson = new Gson();
        Reader reader = Files.newReader(p.toFile(), StandardCharsets.UTF_8);
        return gson.fromJson(reader, SimpleConfig.class);
    }
    public static SimpleConfig readConf(String path, SimpleConfig def) {
        if (path == null){
            if(def == null) {
                throw new IllegalArgumentException("null path and null default");
            }
            return def;
        }
        return SimpleConfig.readConf(Path.of(path), def);
    }

    public static SimpleConfig readConf(Path p, SimpleConfig def) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newReader(p.toFile(), StandardCharsets.UTF_8);
            SimpleConfig sc = gson.fromJson(reader, SimpleConfig.class);
            if(sc == null) {
                if(def == null) {
                    throw new IllegalArgumentException("config at path not available and null default");
                }
                return def;
            }
            return sc.cloneWithDefaults(def);
        } catch (FileNotFoundException e) {
            if(def == null) {
                throw new IllegalArgumentException("file not found and null default");
            }
            return def;
        }
    }

    /**
     * Reads the config from a file in ${user.home}/.config/iotics/{name}
     *
     * @param name the config file name
     * @return
     * @throws FileNotFoundException
     */
    public static SimpleConfig readConfFromHome(String name) throws FileNotFoundException {
        Path p = Paths.get(System.getProperty("user.home"), ".config", "iotics", name);
        return readConf(p);
    }

    public SimpleConfig(String seed, String keyName) {
        this.seed = seed;
        this.keyName = keyName;
    }

    public String seed() {
        return seed;
    }

    public String keyName() {
        return keyName;
    }

    public boolean isValid() {
        return !Strings.isNullOrEmpty(this.seed) && !Strings.isNullOrEmpty(this.keyName) ;
    }

    private SimpleConfig cloneWithDefaults(SimpleConfig def) {
        SimpleConfig sc = new SimpleConfig(this.seed, this.keyName);
        if(sc.seed == null) {
            sc.seed = def.seed;
        }
        if(sc.keyName == null) {
            sc.keyName = def.keyName;
        }
        if(!sc.isValid()) {
            throw new IllegalArgumentException("invalid configuration");
        }
        return sc;
    }
}
