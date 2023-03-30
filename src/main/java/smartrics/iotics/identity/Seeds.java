package smartrics.iotics.identity;

import smartrics.iotics.identity.jna.SdkApi;

import java.util.Objects;

import static smartrics.iotics.identity.Validator.getValueOrThrow;

/**
 * Seed generator delegating to the library via its interface.
 */
public class Seeds {
    private final SdkApi api;

    public Seeds(SdkApi api) {
        this.api = Objects.requireNonNull(api);
    }

    public String CreateDefaultSeed() {
        return getValueOrThrow(api.CreateDefaultSeed());
    }

    public String MnemonicBip39ToSeed(String mnemonics) {
        return getValueOrThrow(api.MnemonicBip39ToSeed(mnemonics));
    }

    public String SeedBip39ToMnemonic(String seed) {
        return getValueOrThrow(api.SeedBip39ToMnemonic(seed));
    }

}
