package smartrics.iotics.identity;

import smartrics.iotics.identity.jna.SdkApi;
import smartrics.iotics.identity.resolver.HttpResolverClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;

import static smartrics.iotics.identity.Validator.getValueOrThrow;
import static smartrics.iotics.identity.Validator.throwIfNotNull;

/**
 * Simpler and more java friendly interface to access IOTICS' identity library functions.
 * Wrapper class to manage identities via the identity library.
 */
public class SimpleIdentity {
    private final SdkApi api;
    private final String userSeed;
    private final String agentSeed;
    private final URL resolverAddress;
    private final HttpResolverClient resolverClient;

    /**
     * Abstraction over the library interface with added validation and high level data bags. This abstraction provides
     * access to the basic high level functionality of the
     *
     * @param api             the api proxy
     * @param resolverAddress
     */
    public SimpleIdentity(SdkApi api, String resolverAddress) {
        this(api, resolverAddress, getValueOrThrow(api.CreateDefaultSeed()));
    }

    public SimpleIdentity(SdkApi api, String resolverAddress, String seed) {
        this(api, resolverAddress, seed, seed);
    }

    /**
     * @param api             the proxy to the library interface
     * @param resolverAddress the https url of the resolver
     * @param userSeed        the user seed
     * @param agentSeed       the agent seed
     */
    public SimpleIdentity(SdkApi api, String resolverAddress, String userSeed, String agentSeed) {
        this.api = Objects.requireNonNull(api);
        this.userSeed = Objects.requireNonNull(userSeed);
        this.agentSeed = Objects.requireNonNull(agentSeed);
        try {
            this.resolverAddress = URI.create(resolverAddress).toURL();
            this.resolverClient = new HttpResolverClient(this.resolverAddress);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("resolver address not a valid URL: " + resolverAddress);
        }
    }

    /**
     * Makes an agent identity. If the identity exists it's returned.
     *
     * @param keyName the key name for this identity, used as passphrase to create the hash of the key from seed
     * @param name    the identifier of the public key in the did document
     * @return the identity data
     */
    public Identity CreateAgentIdentity(String keyName, String name) {
        String did = getValueOrThrow(api.CreateAgentIdentity(resolverAddress.toString(), keyName, name, agentSeed));
        return new Identity(keyName, name, did);
    }

    /**
     * Makes an agent identity. If the identity exists it's recreated and the existing delegations wiped out.
     *
     * @param keyName the key name for this identity, used as passphrase to create the hash of the key from seed
     * @param name    the identifier of the public key in the did document
     * @return the identity data
     */
    public Identity RecreateAgentIdentity(String keyName, String name) {
        String did = getValueOrThrow(api.RecreateAgentIdentity(resolverAddress.toString(), keyName, name, agentSeed));
        return new Identity(keyName, name, did);
    }

    /**
     * Makes a twin identity. If the identity exists it's returned.
     *
     * @param keyName the key name for this identity, used as passphrase to create the hash of the key from seed
     * @param name    the identifier of the public key in the did document
     * @return the identity data
     */
    public Identity CreateTwinIdentity(String keyName, String name) {
        String did = getValueOrThrow(api.CreateTwinIdentity(resolverAddress.toString(), keyName, name, agentSeed));
        return new Identity(keyName, name, did);
    }

    /**
     * Makes a twin identity. If the identity exists it's recreated and the existing delegations wiped out.
     *
     * @param keyName the key name for this identity, used as passphrase to create the hash of the key from seed
     * @param name    the identifier of the public key in the did document
     * @return the identity data
     */
    public Identity RecreateTwinIdentity(String keyName, String name) {
        String did = getValueOrThrow(api.RecreateTwinIdentity(resolverAddress.toString(), keyName, name, agentSeed));
        return new Identity(keyName, name, did);
    }

    /**
     * Makes a user identity. If the identity exists it's returned.
     *
     * @param keyName the key name for this identity, used as passphrase to create the hash of the key from seed
     * @param name    the identifier of the public key in the did document
     * @return the identity data
     */
    public Identity CreateUserIdentity(String keyName, String name) {
        String did = getValueOrThrow(api.CreateUserIdentity(resolverAddress.toString(), keyName, name, userSeed));
        return new Identity(keyName, name, did);
    }

    /**
     * Makes an user identity. If the identity exists it's recreated and the existing delegations wiped out.
     *
     * @param keyName the key name for this identity, used as passphrase to create the hash of the key from seed
     * @param name    the identifier of the public key in the did document
     * @return the identity data
     */
    public Identity RecreateUserIdentity(String keyName, String name) {
        String did = getValueOrThrow(api.RecreateUserIdentity(resolverAddress.toString(), keyName, name, userSeed));
        return new Identity(keyName, name, did);
    }

    /**
     * Creates a new twin and automatically sets the control delegation for this agent identity.
     *
     * @param agentIdentity the agent identity that receives the control delegation
     * @param twinKeyName   the key name for this identity, used as passphrase to create the hash of the key from seed
     * @param twinName      the identifier of the public key in the did document
     * @return the new twin identity
     */
    public Identity CreateTwinIdentityWithControlDelegation(Identity agentIdentity, String twinKeyName, String twinName) {
        String did = getValueOrThrow(api.CreateTwinDidWithControlDelegation(resolverAddress.toString(),
                agentIdentity.did(), agentIdentity.keyName(), agentIdentity.name(), agentSeed, twinKeyName, twinName));
        return new Identity(twinKeyName, twinName, did);
    }

    /**
     * @param agentIdentity the agent needing the token
     * @param userDid       the user that the agent claims it's authorising this request
     * @param audience      the purpose of this token
     * @param duration      validity of this token
     * @return JWT token usable as claim for this agent
     */
    public String CreateAgentAuthToken(Identity agentIdentity, String userDid, String audience, Duration duration) {
        return getValueOrThrow(api.CreateAgentAuthToken(
                agentIdentity.did(), agentIdentity.keyName(), agentIdentity.name(), agentSeed, userDid, audience, duration.toSeconds()));
    }

    /**
     * Uses a default audience.
     *
     * @param agentIdentity the agent needing the token
     * @param userDid       the user that the agent claims it's authorising this request
     * @param duration      validity of this token
     * @return JWT token usable as claim for this agent
     */
    public String CreateAgentAuthToken(Identity agentIdentity, String userDid, Duration duration) {
        return CreateAgentAuthToken(agentIdentity, userDid, resolverAddress.toString(), duration);
    }

    public String RecreateAgentAuthToken(Identity agentIdentity, String userDid, String audience, Duration duration) {
        return getValueOrThrow(api.CreateAgentAuthToken(
                agentIdentity.did(), agentIdentity.keyName(), agentIdentity.name(), agentSeed, userDid, audience, duration.toSeconds()));
    }

    public String RecreateAgentAuthToken(Identity agentIdentity, String userDid, Duration duration) {
        return CreateAgentAuthToken(agentIdentity, userDid, resolverAddress.toString(), duration);
    }

    public String IsAllowedFor(String resolverAddress, String token) {
        return getValueOrThrow(api.IsAllowedFor(resolverAddress, token));
    }

    /**
     * Creates an authentication delegation from this user to this agent.
     * It assumes secrets for user and agent are avaliable in this context
     *
     * @param agentId        the identity of the agent
     * @param userId         the identity of the user
     * @param delegationName the delegation name
     */
    public void UserDelegatesAuthenticationToAgent(Identity agentId, Identity userId, String delegationName) {
        throwIfNotNull(api.UserDelegatesAuthenticationToAgent(resolverAddress.toString(),
                agentId.did(), agentId.keyName(), agentId.name(), agentSeed,
                userId.did(), userId.keyName(), userId.name(), userSeed, delegationName));

    }

    /**
     * Creates a control delegation from this twin to this agent.
     * It assumes secrets for user and agent are avaliable in this context
     *
     * @param agentId        the identity of the agent
     * @param twinId         the identity of the twin
     * @param delegationName the delegation name
     */
    public void TwinDelegatesControlToAgent(Identity agentId, Identity twinId, String delegationName) {
        throwIfNotNull(api.TwinDelegatesControlToAgent(resolverAddress.toString(),
                agentId.did(), agentId.keyName(), agentId.name(), agentSeed,
                twinId.did(), twinId.keyName(), twinId.name(), agentSeed, delegationName));
    }

    String getAgentSeed() {
        return agentSeed;
    }

    String getUserSeed() {
        return userSeed;
    }

    public HttpResolverClient resolverClient() {
        return resolverClient;
    }

    public URL getResolverAddress() {
        return resolverAddress;
    }
}
