package smartrics.iotics.identity;

import java.time.Duration;

/**
 * However agents and users identities are created, at runtime, only new tokens and new twin identities must be created.
 * This interface provides these methods for applications to simply manage their own identity affairs.
 */
public interface IdentityManager {

    /**
     * @param expiry the expiry time of this token
     * @return a new authentication token
     */
    String newAuthenticationToken(Duration expiry);

    /**
     * @param expiry the expiry time of this token
     * @param audience the specific audience this token is for
     * @return a new authentication token
     */
    String newAuthenticationToken(Duration expiry, String audience);

    /**
     *
     * @param twinKeyName the master key name for this twin identity
     * @param controlDelegationID the name of the delegation
     * @return a new twin identity with control delegation for this agent
     */
    Identity newTwinIdentityWithControlDelegation(String twinKeyName, String controlDelegationID);

    default Identity newTwinIdentityWithControlDelegation(String twinKeyName) {
        return newTwinIdentityWithControlDelegation(twinKeyName, "#c-delegation-0");
    }

    /**
     *
     * @param twinKeyName the key name
     * @param twinKeyID key ID (must start with #)
     * @return a new twin identity
     */
    Identity newTwinIdentity(String twinKeyName, String twinKeyID);

    /**
     * @return the agent identity
     */
    Identity agentIdentity();

    /**
     *
     * @return the user identity
     */
    Identity userIdentity();
}
