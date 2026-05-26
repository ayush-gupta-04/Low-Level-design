// Step-Builder : 
// - used to create an object step-by-step.
// - we instantiate instance variables step-by-step in order.
// - we have required fields , optional fields.
// - we can also have validations here.

// Simple Builder : 
// - All the chaining methods returned HttpStepBuilder object ... so we were able to call methods in any order.


import java.util.*;

class HttpRequest {
    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;
    private int timeout;

    // Private constructor - can only be accessed by the Builder
    private HttpRequest() {
        headers = new HashMap<>();
        queryParams = new HashMap<>();
        body = "";
    }

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

        if (!body.isEmpty()) {
            System.out.println("Body: " + body);
        }

        System.out.println("Timeout: " + timeout + " seconds");
        System.out.println("Request executed successfully!");
    }



    // Nested Step interfaces
    // - We made interfaces of the methods.
    // - Even if we chain on withUrl() .. we will only be able to call methods of MethodStep.
    interface UrlStep {
        MethodStep withUrl(String url);
    }

    interface MethodStep {
        HeaderStep withMethod(String method);
    }

    interface HeaderStep {
        OptionalStep withHeader(String key, String value);
    }

    // Here every method returns the same type of object.
    // They return OptionalStep Type .. so we can call these methods in any order.
    interface OptionalStep {
        OptionalStep withBody(String body);
        OptionalStep withTimeout(int timeout);
        HttpRequest build();
    }



    // ------------ Step builder class ---------------
    // static class.
    // have reference of HttpRequest.
    // constructor will be private.
    // implements all the imterfaces.
    static class HttpRequestStepBuilder implements UrlStep, MethodStep, HeaderStep, OptionalStep {
        private HttpRequest req;


        // Constructor will be private : 
        // If public then i would be able to start the chain with any method.
        // Since i want to start the chain with the Method in UrlStep .. i must have a reference of type UrlStep.
        private HttpRequestStepBuilder() {
            req = new HttpRequest();
        }

        // UrlStep implementation
        // - HttpRequestStepBuilder object will be returned but the reference type of the object returned will be MethodStep.
        // - On chaining withUrl() .. we will only be able to call methods defined by MethodStep.
        public MethodStep withUrl(String url) {
            req.url = url;
            return this;
        }

        // MethodStep implementation
        public HeaderStep withMethod(String method) {
            req.method = method;
            return this;
        }

        // HeaderStep implementation
        public OptionalStep withHeader(String key, String value) {
            req.headers.put(key, value);
            return this;
        }

        // OptionalStep implementation
        public OptionalStep withBody(String body) {
            req.body = body;
            return this;
        }

        public OptionalStep withTimeout(int timeout) {
            req.timeout = timeout;
            return this;
        }


        // It stops the chaining.
        // Returns HttpRequest object.
        // We can have final validations here.
        public HttpRequest build() {
            if (req.url == null || req.url.isEmpty()) {
                throw new RuntimeException("URL cannot be empty");
            }
            return req;
        }

        // method to start the building process.
        // must be static.
        // Returns HttpRequestStepBuilder object ... but the reference type of the object will be UrlStep.
        // Since the reference type will be UrlStep .. we will only be able to call methods in UrlStep.
        public static UrlStep getBuilder() {
            return new HttpRequestStepBuilder();
        }
    }
}




public class Main {
    public static void main(String[] args) {
        HttpRequest stepRequest = HttpRequest.HttpRequestStepBuilder.getBuilder()
            .withUrl("https://api.example.com/products")
            .withMethod("POST")
            .withHeader("Content-Type", "application/json")
            .withBody("{\"product\": \"Laptop\", \"price\": 49999}")
            .withTimeout(45)
            .build();

        stepRequest.execute();
    }
}
