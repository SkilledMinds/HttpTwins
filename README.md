# HttpTwins
HttpTwins: A Design Pattern for Asynchronous HTTP Request Duplication

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
