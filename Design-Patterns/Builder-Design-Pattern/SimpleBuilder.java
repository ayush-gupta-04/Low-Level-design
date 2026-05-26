import java.util.*;

class HttpRequest {
    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String,String> queryParams;
    private String body;
    private int timeout; // in seconds

    // Private constructor - can only be accessed by the Builder
    HttpRequest() {
        headers = new HashMap<>();
        queryParams = new HashMap<>();
        body = "";
    }

    // Method to execute the HTTP request
    public void execute() {
        System.out.println("Executing " + method + " request to " + url);

        if (!queryParams.isEmpty()) {
            System.out.println("Query Parameters:");
            for (Map.Entry<String, String> param : queryParams.entrySet()) {
                System.out.println("  " + param.getKey() + "=" + param.getValue());
            }
        }

        System.out.println("Headers:");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            System.out.println("  " + header.getKey() + ": " + header.getValue());
        }

        if (body != null && !body.isEmpty()) {
            System.out.println("Body: " + body);
        }

        System.out.println("Timeout: " + timeout + " seconds");
        System.out.println("Request executed successfully!");
    }

    // Builder class as a nested class to access private members
    public static class HttpRequestBuilder {
        private HttpRequest req;

        public HttpRequestBuilder() {
            req = new HttpRequest();
        }

        // Method chaining
        public HttpRequestBuilder withUrl(String u) {
            req.url = u;
            return this;
        }

        public HttpRequestBuilder withMethod(String method) {
            req.method = method;
            return this;
        }

        public HttpRequestBuilder withHeader(String key, String value) {
            req.headers.put(key, value);
            return this;
        }

        public HttpRequestBuilder withQueryParams(String key, String value) {
            req.queryParams.put(key, value);
            return this;
        }

        public HttpRequestBuilder withBody(String body) {
            req.body = body;
            return this;
        }

        public HttpRequestBuilder withTimeout(int timeout) {
            req.timeout = timeout;
            return this;
        }

        // Build method to create the immutable HttpRequest object
        public HttpRequest build() {
            // Validation logic can be added here
            if (req.url == null || req.url.isEmpty()) {
                throw new RuntimeException("URL cannot be empty");
            }
            return req;
        }
    }
}


public class Main {
    public static void main(String[] args) {
        // Using Builder Pattern (nested class)
        HttpRequest request = new HttpRequest.HttpRequestBuilder()
            .withUrl("https://api.example2.com")
            .withMethod("POST")
            .withHeader("Content-Type", "application/json")
            .withHeader("Accept", "application/json")
            .withQueryParams("key", "12345")
            .withBody("{\"name\": \"Aditya\"}")
            .withTimeout(60)
            .build();

        request.execute(); // Guaranteed to be in a consistent state
    }
}
