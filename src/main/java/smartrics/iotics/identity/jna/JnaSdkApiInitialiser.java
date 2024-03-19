package smartrics.iotics.identity.jna;

import com.sun.jna.Native;

import java.io.File;


/**
 * Native library loader
 */
public class JnaSdkApiInitialiser implements SdkApiInitialiser {
    private static final String LIB_NAME;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            LIB_NAME = "lib-iotics-id-sdk.dll";
        } else if (osName.contains("mac")) {
            LIB_NAME = "lib-iotics-id-sdk.dylib";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            LIB_NAME = "lib-iotics-id-sdk.so";
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }

    }

    private static String LIB_PATH = new File("./lib/" + LIB_NAME).getAbsolutePath();

    private SdkApi idProxy;

    /**
     * Initialiser with path to the native set to LIB_PATH
     */
    public JnaSdkApiInitialiser() {
        String path = System.getProperty("ioticsIdentityLibraryFile");
        if (path != null) {
            try {
                this.idProxy = Native.loadLibrary(path, SdkApi.class);
            } catch (UnsatisfiedLinkError e) {
                throw new IllegalStateException("unable to load library from path supplied in -DioticsIdentityLibraryFile");
            }
        }
        try {
            this.idProxy = Native.loadLibrary(LIB_NAME, SdkApi.class);
        } catch (UnsatisfiedLinkError e) {
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
    @Override
    public final SdkApi get() {
        return idProxy;
    }

}
