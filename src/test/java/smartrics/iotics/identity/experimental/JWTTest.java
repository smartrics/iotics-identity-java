package smartrics.iotics.identity.experimental;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class JWTTest {

    @Test
    void validJwtTokenParsing() {
        // Example JWT token (You should replace this with a valid encoded example)
        String encodedToken = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDAwMDAwMDAsImlhdCI6MTUwMDAwMDAwMH0.signaturePart";
        JWT jwt = new JWT(encodedToken);

        assertAll(
                () -> assertNotNull(jwt),
                () -> assertEquals("{\"alg\":\"HS256\"}", jwt.header()),
                () -> assertTrue(jwt.payload().contains("\"exp\":1600000000")),
                () -> assertTrue(jwt.payload().contains("\"iat\":1500000000")),
                () -> assertEquals("signaturePart", jwt.signature())
        );
    }

    @Test
    void invalidJwtTokenHandling() {
        String badToken = "bad.token";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new JWT(badToken));
        assertEquals("Invalid JWT token", exception.getMessage());
    }

    @Test
    void toNiceStringOutputsCorrectly() {
        // Example JWT token (You should replace this with a valid encoded example)
        String encodedToken = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDAwMDAwMDAsImlhdCI6MTUwMDAwMDAwMH0.signaturePart";
        JWT jwt = new JWT(encodedToken);

        String niceString = jwt.toNiceString();
        assertNotNull(niceString);

        JsonObject jsonObject = JsonParser.parseString(niceString).getAsJsonObject();
        JsonObject header = jsonObject.getAsJsonObject("header");
        JsonObject payload = jsonObject.getAsJsonObject("payload");
        String signature = jsonObject.get("signature").getAsString();

        assertAll(
                () -> assertNotNull(header),
                () -> assertNotNull(payload),
                () -> assertEquals("signaturePart", signature),
                () -> assertTrue(payload.has("exp")),
                () -> assertTrue(payload.has("iat"))
        );
    }

    @Test
    void toStringMethod() {
        // Example JWT token (You should replace this with a valid encoded example)
        String encodedToken = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDAwMDAwMDAsImlhdCI6MTUwMDAwMDAwMH0.signaturePart";
        JWT jwt = new JWT(encodedToken);

        String expectedToString = "JWT{header='{\"alg\":\"HS256\"}', payload='{\"exp\":1600000000,\"iat\":1500000000}', signature='signaturePart'}";
        assertEquals(expectedToString, jwt.toString());
    }
}
