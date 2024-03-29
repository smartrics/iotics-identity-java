package smartrics.iotics.identity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import smartrics.iotics.identity.jna.SdkApi;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static smartrics.iotics.identity.DataFactory.*;

@ExtendWith(MockitoExtension.class)
public class SimpleIdentityTest {

    @Mock
    SdkApi sdkApi;

    @Test
    void validApiConstruction() {
        assertThrows(NullPointerException.class, () -> {
            new SimpleIdentity(null, "");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new SimpleIdentity(sdkApi, "invalid url", "some seed");
        });
    }

    @Test
    void whenConstructedWithoutSeed_thenGeneratesNewOne() {
        when(sdkApi.CreateDefaultSeed()).thenReturn(validResult("some seed"));
        new SimpleIdentity(sdkApi, validUrl());

        verify(sdkApi).CreateDefaultSeed();
    }

    @Test
    void whenConstructedWithOneSeed_thenUsesItForBothAgentAndUser() {
        SimpleIdentity si = new SimpleIdentity(sdkApi, validUrl(), "some seed");

        assertEquals("some seed", si.getAgentSeed());
        assertEquals("some seed", si.getUserSeed());

        verifyNoInteractions(sdkApi);
    }

    @Test
    void whenConstructedWithTwoSeed_thenUsesOneForUserAndOneForAgent() {
        SimpleIdentity si = new SimpleIdentity(sdkApi, validUrl(), "user seed", "agent seed");

        assertEquals("agent seed", si.getAgentSeed());
        assertEquals("user seed", si.getUserSeed());

        verifyNoInteractions(sdkApi);
    }

    @Test
    void whenCreateTwinDidWithControlDelegation_thenMapsParametersAndDelegatesToApi() {
        String res = validUrl();

        SimpleIdentity si = new SimpleIdentity(sdkApi, res, "some seed");
        Identity id = aValidAgentIdentity();
        when(sdkApi.CreateTwinDidWithControlDelegation(any(), any(), any(), any(), any(), any(), any())).thenReturn(validResult("twin did"));

        Identity twinId = si.CreateTwinIdentityWithControlDelegation(id, "twinKeyName", "twinName");

        assertEquals(twinId.did(), "twin did");
        assertEquals(twinId.keyName(), "twinKeyName");
        assertEquals(twinId.name(), "twinName");
        verify(sdkApi).CreateTwinDidWithControlDelegation(res, id.did(), id.keyName(), id.name(), si.getAgentSeed(), "twinKeyName", "twinName");
    }

    @Test
    void whenRecreateAgentIdentity_thenMapsParametersAndDelegatesToApi() {
        String res = validUrl();

        SimpleIdentity si = new SimpleIdentity(sdkApi, res, "some seed");
        when(sdkApi.RecreateAgentIdentity(any(), any(), any(), any())).thenReturn(validResult("agent did"));

        Identity agentIdentity = si.RecreateAgentIdentity("agentKeyName", "agentName");

        assertEquals(agentIdentity.did(), "agent did");
        assertEquals(agentIdentity.keyName(), "agentKeyName");
        assertEquals(agentIdentity.name(), "agentName");
        verify(sdkApi).RecreateAgentIdentity(res, "agentKeyName", "agentName", "some seed");
    }

    @Test
    void whenCreateAgentIdentity_thenMapsParametersAndDelegatesToApi() {
        String res = validUrl();

        SimpleIdentity si = new SimpleIdentity(sdkApi, res, "some seed");
        when(sdkApi.CreateAgentIdentity(any(), any(), any(), any())).thenReturn(validResult("agent did"));

        Identity agentIdentity = si.CreateAgentIdentity("agentKeyName", "agentName");

        assertEquals(agentIdentity.did(), "agent did");
        assertEquals(agentIdentity.keyName(), "agentKeyName");
        assertEquals(agentIdentity.name(), "agentName");
        verify(sdkApi).CreateAgentIdentity(res, "agentKeyName", "agentName", "some seed");
    }

    @Test
    void whenIsAllowedFor_thenDelegatesToApi() {
        String res = validUrl();

        SimpleIdentity si = new SimpleIdentity(sdkApi, res, "some seed");
        when(sdkApi.IsAllowedFor(any(), any())).thenReturn(validResult("true"));

        String allowed = si.IsAllowedFor("resolver", "token");

        assertEquals(allowed, "true");
        verify(sdkApi).IsAllowedFor("resolver", "token");
    }

    @Test
    void whenRecreateUserIdentity_thenMapsParametersAndDelegatesToApi() {
        String res = validUrl();

        SimpleIdentity si = new SimpleIdentity(sdkApi, res, "some seed");
        when(sdkApi.RecreateUserIdentity(any(), any(), any(), any())).thenReturn(validResult("user did"));

        Identity userIdentity = si.RecreateUserIdentity("userKeyName", "userName");

        assertEquals(userIdentity.did(), "user did");
        assertEquals(userIdentity.keyName(), "userKeyName");
        assertEquals(userIdentity.name(), "userName");
        verify(sdkApi).RecreateUserIdentity(res, "userKeyName", "userName", "some seed");
    }

    @Test
    void whenCreateUserIdentity_thenMapsParametersAndDelegatesToApi() {
        String res = validUrl();

        SimpleIdentity si = new SimpleIdentity(sdkApi, res, "some seed");
        when(sdkApi.CreateUserIdentity(any(), any(), any(), any())).thenReturn(validResult("user did"));

        Identity userIdentity = si.CreateUserIdentity("userKeyName", "userName");

        assertEquals(userIdentity.did(), "user did");
        assertEquals(userIdentity.keyName(), "userKeyName");
        assertEquals(userIdentity.name(), "userName");
        verify(sdkApi).CreateUserIdentity(res, "userKeyName", "userName", "some seed");
    }

    @Test
    void whenCreateUserIdentityFails_thenThrows() {
        SimpleIdentity si = new SimpleIdentity(sdkApi, validUrl(), "some seed");
        when(sdkApi.CreateUserIdentity(any(), any(), any(), any())).thenReturn(errorResult("some error"));

        assertThrows(SimpleIdentityException.class, () -> {
            si.CreateUserIdentity("userKeyName", "userName");
        });
    }

    @Test
    void whenCreateAgentAuthToken_thenMapsParametersAndDelegatesToApi() {
        String res = validUrl();

        SimpleIdentity si = new SimpleIdentity(sdkApi, res, "some seed");
        when(sdkApi.CreateAgentAuthToken(any(), any(), any(), any(), any(), any(), anyLong())).thenReturn(validResult("some token"));

        Identity i = aValidAgentIdentity();
        String token = si.CreateAgentAuthToken(i, "did:iotics:user", "aud", Duration.ofSeconds(123));

        assertEquals(token, "some token");
        verify(sdkApi).CreateAgentAuthToken(i.did(), i.keyName(), i.name(), si.getAgentSeed(), "did:iotics:user", "aud", Integer.valueOf(123));
    }

    @Test
    void whenCreateAgentAuthToken_thenMapsParametersAndDelegatesToApiWithDefaultAudience() {
        String res = validUrl();

        SimpleIdentity si = new SimpleIdentity(sdkApi, res, "some seed");
        when(sdkApi.CreateAgentAuthToken(any(), any(), any(), any(), any(), any(), anyLong())).thenReturn(validResult("some token"));

        Identity i = aValidAgentIdentity();
        String token = si.CreateAgentAuthToken(i, "did:iotics:user", Duration.ofSeconds(123));

        assertEquals(token, "some token");
        verify(sdkApi).CreateAgentAuthToken(i.did(), i.keyName(), i.name(), si.getAgentSeed(), "did:iotics:user", res, Integer.valueOf(123));
    }

    @Test
    void whenUserDelegatesAuthenticationToAgent_thenMapsParametersAndDelegatesToApi() {
        String res = validUrl();
        String as = "agentSeed";
        String us = "userSeed";
        SimpleIdentity si = new SimpleIdentity(sdkApi, res, us, as);

        Identity i = aValidAgentIdentity();
        Identity u = aValidUserIdentity();
        si.UserDelegatesAuthenticationToAgent(i, u, "#foobar");

        verify(sdkApi).UserDelegatesAuthenticationToAgent(res,
                i.did(), i.keyName(), i.name(), si.getAgentSeed(),
                u.did(), u.keyName(), u.name(), si.getUserSeed(),
                "#foobar");
    }

    @Test
    void whenTwinDelegatesControlToAgent_thenMapsParametersAndDelegatesToApi() {
        String res = validUrl();
        String as = "agentSeed";
        String us = "userSeed";
        SimpleIdentity si = new SimpleIdentity(sdkApi, res, us, as);

        Identity i = aValidAgentIdentity();
        Identity u = aValidUserIdentity();
        si.TwinDelegatesControlToAgent(i, u, "#foobar");

        verify(sdkApi).TwinDelegatesControlToAgent(res,
                i.did(), i.keyName(), i.name(), si.getAgentSeed(),
                u.did(), u.keyName(), u.name(), si.getAgentSeed(),
                "#foobar");
    }

}
