package smartrics.iotics.identity;

public record DocumentResult(String rawDocument, String fetchErrorMessage) {
    public boolean hasError() {
        return fetchErrorMessage != null;
    }
}
