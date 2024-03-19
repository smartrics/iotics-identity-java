package smartrics.iotics.identity.go;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class StringResultTest {

    @Test
    void noArgConstructor() {
        StringResult result = new StringResult();
        assertNull(result.value, "Value should be null");
        assertNull(result.err, "Err should be null");
    }

    @Test
    void argConstructor() {
        String expectedValue = "Test Value";
        String expectedErr = "Test Error";
        StringResult result = new StringResult(expectedValue, expectedErr);

        assertEquals(expectedValue, result.value, "Value does not match");
        assertEquals(expectedErr, result.err, "Err does not match");
    }

    @Test
    void testToString() {
        String expectedValue = "Test Value";
        String expectedErr = "Test Error";
        StringResult result = new StringResult(expectedValue, expectedErr);

        String expectedToString = "StringResult{" +
                "value='" + expectedValue + '\'' +
                ", r1='" + expectedErr + '\'' +
                '}';
        assertEquals(expectedToString, result.toString(), "toString does not match");
    }

    @Test
    void getFieldOrder() {
        StringResult result = new StringResult();
        assertEquals(Arrays.asList("value", "err"), result.getFieldOrder(), "Field order is incorrect");
    }
}
