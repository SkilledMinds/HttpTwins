# HttpTwins
HttpTwins: A Design Pattern for Asynchronous HTTP Request Duplication

Here's a short note and demo code for my HttpTwins design pattern — a custom annotation for creating asynchronous duplicates of incoming HTTP requests.

In the Spring Boot demo, it captures and logs the complete HTTP request (method, URI, headers, body) to the console asynchronously, enabling non-blocking debugging and monitoring.

This pattern is useful in scenarios such as:

Distributing the same payload to multiple remote systems.

Powering AI agents that need access to live HTTP input.

Inspecting incoming requests for debugging, compliance, or analytics — particularly in development or staging environments.

# HttpTwins

**Purpose**  
`HttpTwins` is a custom Spring Boot annotation for controller methods. It asynchronously logs the full HTTP request \(method, URI, headers, body\) to the console, aiding debugging and monitoring without blocking the main thread.

**Use Case**  
Apply `@HttpTwins` to REST API endpoints where you need to inspect incoming HTTP requests for troubleshooting, compliance, or analytics, especially in development or staging environments.

**Example Usage**
```java
@HttpTwins
@GetMapping("/books")
public List<Book> getBooks() {
    // Your logic here
}
```

**Features**
- Non-blocking request logging \(runs in a separate thread\)
- Captures method, URI, headers, and body
- Simple integration with Spring Boot controllers

**Note**  
For full request body logging, use a request wrapper or filter to cache the body, as servlet request streams can only be read once.
