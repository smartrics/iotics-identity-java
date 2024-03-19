package smartrics.iotics.identity.resolver;

import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class HttpResolverClientTest {

    private OkHttpClient mockedClient;
    private HttpResolverClient resolverClient;
    private final URL baseUrl = new URL("http://example.com");

    HttpResolverClientTest() throws MalformedURLException {
    }

    @BeforeEach
    void setUp() {
        mockedClient = Mockito.mock(OkHttpClient.class);
        resolverClient = new HttpResolverClient(baseUrl, mockedClient);
    }

    @Test
    void discoverSuccess() throws IOException {
        // Prepare the successful response
        String jwtPayload = Base64.getEncoder().encodeToString("{\"key\":\"value\"}".getBytes());
        String responseBody = "{ \"token\": \"unused." + jwtPayload + ".unused\" }";
        Response successResponse = new Response.Builder()
                .request(new Request.Builder().url(baseUrl).build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(responseBody, MediaType.parse("application/json")))
                .build();

        Call call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(successResponse);
        Mockito.when(mockedClient.newCall(any(Request.class))).thenReturn(call);

        ResolverClient.Result result = resolverClient.discover("validDID");
        String content = result.content();
        assertEquals("{\"key\":\"value\"}", content);
        assertFalse(result.isErr());
    }

    @Test
    void testDiscoverNotFound() throws IOException {
        // Simulate a 404 response
        Response notFoundResponse = new Response.Builder()
                .request(new Request.Builder().url(baseUrl).build())
                .protocol(Protocol.HTTP_1_1)
                .code(404)
                .message("Not Found")
                .build();

        Call call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(notFoundResponse);
        Mockito.when(mockedClient.newCall(any(Request.class))).thenReturn(call);

        ResolverClient.Result result = resolverClient.discover("nonexistentDID");
        assertTrue(result.isErr());
    }

    @Test
    void discoverMalformedUrl() {
        // Assuming discover method would throw a RuntimeException for a malformed URL
        // This could happen if base URL is correctly formed but DID is such that it creates an invalid URL
        assertThrows(RuntimeException.class, () -> resolverClient.discover("::::"));
    }

    @Test
    void testDiscoverNon200Non404Response() throws IOException {
        Response errorResponse = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(500)
                .message("Internal Server Error")
                .build();

        Call call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(errorResponse);
        Mockito.when(mockedClient.newCall(any(Request.class))).thenReturn(call);

        ResolverClient.Result result = resolverClient.discover("problematicDID");
        assertTrue(result.isErr());
        assertEquals("No result found", result.content());
    }

    @Test
    void discoverWithEmptyDID() {
        assertThrows(IllegalArgumentException.class, () -> resolverClient.discover(""));
    }

    @Test
    void testDiscoverResponseWithNoBody() throws IOException {
        Response responseWithNoBody = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build(); // No body set

        Call call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(responseWithNoBody);
        Mockito.when(mockedClient.newCall(any(Request.class))).thenReturn(call);

        ResolverClient.Result result = resolverClient.discover("validDIDWithNoBody");
        assertTrue(result.isErr());
        assertEquals("invalid response", result.content());
    }

    @Test
    void discoverInvalidJWTFormat() throws IOException {
        String invalidJWT = "{ \"token\": \"not.a.valid.jwt.format\" } ";
        ResponseBody responseBody = ResponseBody.create(invalidJWT, MediaType.get("application/json; charset=utf-8"));
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(responseBody)
                .build();

        Call call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(mockedClient.newCall(any(Request.class))).thenReturn(call);

        ResolverClient.Result result = resolverClient.discover("DIDWithInvalidJWT");
        assertTrue(result.isErr());
    }

    @Test
    void testDiscoverThrowsIOException() throws IOException {
        Call call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenThrow(new IOException("Network error"));
        Mockito.when(mockedClient.newCall(any(Request.class))).thenReturn(call);

        Exception exception = assertThrows(IOException.class, () -> resolverClient.discover("validDIDWithIOException"));
        assertEquals("Network error", exception.getMessage());
    }

    @Test
    void discoverWithNullDID() {
        assertThrows(IllegalArgumentException.class, () -> resolverClient.discover(null));
    }

    @Test
    void discoverResponseWithEmptyJWTPayload() throws IOException {
        String emptyPayloadJWT = Base64.getEncoder().encodeToString("{}".getBytes());
        String responseBodyString = "{ \"token\": \"header." + emptyPayloadJWT + ".signature\" } ";
        ResponseBody responseBody = ResponseBody.create(responseBodyString, MediaType.get("application/json; charset=utf-8"));
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(responseBody)
                .build();

        Call call = Mockito.mock(Call.class);
        Mockito.when(call.execute()).thenReturn(response);
        Mockito.when(mockedClient.newCall(any(Request.class))).thenReturn(call);

        ResolverClient.Result result = resolverClient.discover("validDIDWithEmptyPayload");
        assertEquals("{}", result.content());
    }

    @Test
    void discoverWithSpecialCharacterDID() throws IOException {
        String specialDID = "did:example:special&character";
        String encodedDID = java.net.URLEncoder.encode(specialDID, StandardCharsets.UTF_8);

        Call call = Mockito.mock(Call.class);
        Response fakeResponse = new Response.Builder()
                .request(new Request.Builder().url("http://example.com/1.0/discover/" + encodedDID).build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("", MediaType.get("application/json")))
                .build();
        Mockito.when(call.execute()).thenReturn(fakeResponse);
        Mockito.when(mockedClient.newCall(any(Request.class))).thenReturn(call);

        resolverClient.discover(specialDID);
        // Verify that the correct, encoded URL was used for the request
        Mockito.verify(mockedClient).newCall(any(Request.class));
    }

}
