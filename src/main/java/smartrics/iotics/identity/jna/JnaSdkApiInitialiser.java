package smartrics.iotics.identity.jna;

import com.sun.jna.Native;

import java.io.File;


/**
 * Native library loader
 */
public class JnaSdkApiInitialiser {
    private static String LIB_NAME = "lib-iotics-id-sdk.so";
    private static String LIB_PATH = new File("./lib/"+LIB_NAME).getAbsolutePath();

    private SdkApi idProxy;

    /**
     * Initialiser with path to the native set to LIB_PATH
     */
    public JnaSdkApiInitialiser() {
        try {
            this.idProxy = Native.loadLibrary(LIB_NAME, SdkApi.class);
        } catch(UnsatisfiedLinkError e) {
            this.idProxy = Native.loadLibrary(LIB_PATH, SdkApi.class);
        }
    }

    /**
     * Initialiser with custom library path
     *
     * @param libPath the library path
     */
    public JnaSdkApiInitialiser(String libPath) {
        this.idProxy = Native.loadLibrary(libPath, SdkApi.class);
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
