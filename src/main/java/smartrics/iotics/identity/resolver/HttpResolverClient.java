package smartrics.iotics.identity.resolver;

import okhttp3.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;

/**
 * Resolver client over HTTP
 */
public class HttpResolverClient implements ResolverClient {
    private final URL base;
    private final OkHttpClient client;

    public HttpResolverClient(URL base) {
        this(base, new OkHttpClient());
    }

    public HttpResolverClient(URL base, OkHttpClient client) {
        this.base = base;
        this.client = client;
    }


    protected OkHttpClient getClient() {
        return this.client;
    }

    public Result discover(String did) throws IOException {
        if(did == null || did.isBlank()) {
            throw new IllegalArgumentException("invalid input string");
        }
        URL url;
        try {
            url = new URL(base, "/1.0/discover/" + URI.create(did));
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid input did");
        }
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = null;
        try {
            Call call = getClient().newCall(request);
            if(call == null) {
                return new Result("Unable to create the http request", "application/text", true);
            }
            response = call.execute();
            if (response.code() > 299) {
                if (response.code() == 404) {
                    return new Result("DID not found", "application/text", true);
                }
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        return new Result(body.string(), "application/xml", true);
                    } else {
                        return new Result("No result found", "application/text", true);
                    }
                }
            }
            try (ResponseBody body = response.body()) {
                if (body == null) {
                    return new Result("invalid response", "application/text", true);
                }
                try {
                    String bodyString = body.string();
                    String[] parts = bodyString.split("\"");
                    String token = parts[3];
                    Base64.Decoder decoder = Base64.getDecoder();
                    String payload = new String(decoder.decode(token.split("\\.")[1]));
                    return new Result(payload, "application/json", false);
                } catch (Exception e) {
                    return new Result("parsing error: " + e.getMessage(), "application/text", true);
                }
            }
        } finally {
            try {
                if(response != null) {
                    response.close(); // Ensure the response is closed if not done automatically
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public static void main(String[] args) throws Exception {
        HttpResolverClient c = new HttpResolverClient(URI.create(args[0]).toURL());
        Result agent = c.discover(args[1]);
        Result user = c.discover(args[2]);

        System.out.println("AGENT ------");
        System.out.println(agent);
        System.out.println("USER ------");
        System.out.println(user);
    }
}
