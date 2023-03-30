# Java wrapper for the ID SDK

IOTICS ID Library - Java wrapper using JNA over ffi

## Build

Build the java library with:

`mvn pakage`

In order for the library to work, iotics-identity-go must be compiled as library.
Follow instructions here `https://github.com/Iotic-Labs/iotics-identity-go/blob/main/ffi/README.md` for how to build it.

Release to mvn central

`mvn clean deploy -Prelease`

## Test

A sample app is in the test/java directory: `smartrics.iotics.identity.App`

## Use

Initialise the API with:

```java
String libPath = "<path to library>";
SdkApi api = new JnaSdkApiInitialiser(libPath).get();
```

Usages: see also `src/test/java/smartrics/iotics/identity/App.java`

```java

    // Generate seeds
    public static void seeds(SdkApi api) {

        Seeds seeds = new Seeds(api);

        String res;

        res = seeds.CreateDefaultSeed();
        System.out.println("CreateDefaultSeed: " + res);

        res = seeds.SeedBip39ToMnemonic(res);
        System.out.println("SeedBip39ToMnemonic: " + res);

        res = seeds.MnemonicBip39ToSeed(res);
        System.out.println("MnemonicBip39ToSeed: " + res);
    }

    // Generate identities
    public static void identities(SdkApi api) {
        SimpleIdentity idSdk = new SimpleIdentity(api, resolver, seed);

        Identity agentIdentity = idSdk.CreateAgentIdentity("aKey1", "#app1");
        System.out.println("CreateAgentIdentity: " + agentIdentity );

        Identity userIdentity = idSdk.CreateUserIdentity("uKey1", "#user1");
        System.out.println("CreateUserIdentity: " + userIdentity);

        Identity twinIdentity = idSdk.CreateTwinIdentityWithControlDelegation(agentIdentity, "tKey1", "#tName");
        System.out.println("CreateTwinDidWithControlDelegation: " + twinIdentity);
    }

    // Generate an authentication token
    public static void token(SdkApi api) {
        SimpleIdentity idSdk = new SimpleIdentity(api, resolver, seed);
        String token = idSdk.CreateAgentAuthToken(agentIdentity, uDiD, Duration.ofHours(10));
        System.out.println("CreateAgentAuthToken: " + token);
    }

```
