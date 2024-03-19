package smartrics.iotics.identity.resolver;

import java.io.IOException;

public interface ResolverClient {
    record Result(String content, String contentType, boolean isErr) { }

    Result discover(String did) throws IOException;
}
