package smartrics.iotics.identity.jna;

import com.sun.jna.Native;

import java.io.File;


/**
 * Native library loader
 */
public class JnaSdkApiInitialiser {
    public final static String LIB_PATH = new File("./lib/lib-iotics-id-sdk.so").getAbsolutePath();
    private final String libPath;

    private final SdkApi idProxy;

    /**
     * Initialiser with path to the native set to LIB_PATH
     */
    public JnaSdkApiInitialiser() {
        this(LIB_PATH);
    }

    /**
     * Initialiser with custom library path
     *
     * @param libPath the library path
     */
    public JnaSdkApiInitialiser(String libPath) {
        this.libPath = libPath;
        this.idProxy = Native.loadLibrary(this.libPath, SdkApi.class);
    }

    /**
     * An instance of the library interface is created at construction and set as a reference in this object.
     * Not thread safe.
     *
     * @return the library interface
     */
    public final SdkApi get() {
        return idProxy;
    }

}
