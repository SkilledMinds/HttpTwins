# 🧩 HttpTwins: A New Design Pattern for Asynchronous HTTP Duplication

**HttpTwins** (or **HttpFanout**) introduces a fresh, annotation-driven design pattern that extends the classic *Fan-out* concept directly into the **HTTP request layer**.  
It enables developers to **asynchronously duplicate an incoming HTTP request** to one or more downstream processors — without blocking the main execution thread.

---

## 🚀 Overview

The **Spring Boot implementation** of `@HttpTwins` is a **robust and extensible custom annotation** for REST controllers.  
It enables **non-blocking, asynchronous handling** of HTTP requests for advanced use cases such as:

- Request mirroring and live request duplication  
- Real-time auditing and compliance logging  
- Integration with remote systems (ERP, analytics, monitoring)  
- Non-intrusive debugging and observability  

The annotation is highly configurable — supporting **activation toggling** and **dynamic delegation** to target classes or handlers.

---

## 🧠 What Problem It Solves

In typical web applications, developers often combine multiple tools to achieve asynchronous or inspection behavior:

- `@Async` — for parallel execution  
- `HandlerInterceptor` or `Filter` — for request interception  
- Custom logging or monitoring utilities  

While these serve specific purposes, **there has been no clean, unified way** to *fan out* full HTTP requests asynchronously — until now.

**HttpTwins fills that gap.**  
It provides a **simple, declarative annotation** that triggers background duplication of a live HTTP request, allowing secondary operations (like mirroring, logging, or forwarding) to run in parallel — **without impacting response time**.

---

## ⚙️ How It Works

By annotating a controller or service method with `@HttpTwins` (or `@HttpFanout`), developers can automatically:

1. **Capture** the complete HTTP request — including method, URI, headers, and body.  
2. **Asynchronously dispatch** or **log** the request copy to downstream systems or handlers.  
3. **Maintain** the main thread flow for serving the primary client response.  

---

## 💡 Why It Matters

- Promotes **clean architecture** and **minimal boilerplate**  
- Enhances **observability** without intrusive code changes  
- Improves **developer productivity** and **system scalability**  
- Enables **multi-system synchronization**, **A/B testing**, or **event-driven pipelines**

---

## Key Features

- **Decoupled & Asynchronous**: Keeps controller logic clean and performs all mirroring operations on a separate thread, ensuring zero performance impact.
- **Highly Configurable**: Toggle functionality with `active = true/false` or route to different processors using `destination = "beanName"`.
- **Extensible**: Simply implement the `RequestProcessor` interface to create new destinations for your mirrored requests.
- **Easy to Use**: A single annotation is all you need to enable powerful request mirroring on any controller method.

## Use Cases

- **Live Auditing**: Send a copy of every critical request to a persistent audit log.
- **Real-Time Analytics**: Forward request metadata to an analytics engine to track API usage.
- **Debugging & Inspection**: Easily inspect the full payload of problematic requests without a debugger.
- **External System Integration**: Mirror requests to a remote ERP, a message queue (Kafka, RabbitMQ), or a backup service.

---

## Quick Start

Getting started with `HttpTwins` is simple. Just add the annotation to your controller methods and create a processor to handle the mirrored data.

### 1. Annotate Your Controller Method

Apply `@HttpTwins` to any Spring MVC endpoint. You can specify a destination bean and toggle it on or off.

```java
@RestController
@RequestMapping("/books")
public class BookController {

    // ... constructor ...

    @PostMapping
    @HttpTwins(
            // Fan-out to multiple local Spring beans
            localdestinations = {RemoteERP.class, ReportingAgent.class},

            // Also send to multiple remote endpoints
            remoteDestinations = {
                    // A test endpoint to receive mirrored requests
                    "https://http-twins-remote-test.free.beeceptor.com/regionalTests",
                    // A second remote destination
                    "https://ecom.buyers/items" 
            }
            , active = true
    )
    public Book createBook(@RequestBody Book book) {
        // Your primary controller logic remains clean
        return bookRepository.save(book);
    }
}
```

### 2. Create a Request Processor

Create a Spring bean that implements the `RequestProcessor` interface. The bean name must match the `destination` in the annotation.

```java
@Service
public class RemoteERP implements RequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RemoteERP.class);

    @Override
    public void process(HttpServletRequest request) {
        logger.info("\n==================== HttpTwins Request for [RemoteERP] ====================");
        logger.info("Method: {}", request.getMethod());
        logger.info("URI: {}", request.getRequestURI());

        logger.info("--- Headers ---");
        Collections.list(request.getHeaderNames())
                .forEach(name -> logger.info("{}: {}", name, request.getHeader(name)));

        logger.info("--- Body ---");
        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
        byte[] body = wrapper.getContentAsByteArray();
        if (body.length > 0) {
            logger.info(new String(body, StandardCharsets.UTF_8));
        } else {
            logger.info("[No Body]");
        }

        logger.info("-> Executing RemoteERP business logic...");
        logger.info("=========================================================================\n");
    }
}
```

---
### 3. Supporting logs

Use HTTP client attached with project to execute REST APIs attached with HTTPTwins

```java
2025-10-11T19:15:05.282+05:30  INFO 26586 --- [httpTwins] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
        2025-10-11T19:15:05.283+05:30  INFO 26586 --- [httpTwins] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
        2025-10-11T19:15:05.289+05:30  INFO 26586 --- [httpTwins] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 6 ms
2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-2] c.e.httpTwins.service.ReportingAgent     :
        --- HttpTwins Request for [ReportingAgent] ---
        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    :
        ==================== HttpTwins Request for [RemoteERP] ====================
        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-2] c.e.httpTwins.service.ReportingAgent     : Processing POST request for URI: /books
2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : Method: POST
2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-2] c.e.httpTwins.service.ReportingAgent     : -> Executing ReportingAgent business logic...
        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : URI: /books
2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-2] c.e.httpTwins.service.ReportingAgent     : --------------------------------------------

        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : --- Headers ---
        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : content-length: 83
        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : host: localhost:8080
        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : user-agent: Java-http-client/21.0.8
        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : content-type: application/json
2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : --- Body ---
        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : {
        "title": "The Hitchhiker's Guide to the Galaxy",
        "author": "Douglas Adams"
        }

        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : -> Executing RemoteERP business logic...
        2025-10-11T19:15:05.365+05:30  INFO 26586 --- [httpTwins] [       Thread-1] c.example.httpTwins.service.RemoteERP    : =========================================================================

        2025-10-11T19:15:05.457+05:30 ERROR 26586 --- [httpTwins] [       Thread-4] c.e.h.s.RemoteDestinationProcessor       : HttpTwins ERROR: Failed to mirror request to remote destination 'https://ecom.buyers/items'. Reason: I/O error on POST request for "https://ecom.buyers/items": ecom.buyers
2025-10-11T19:15:06.894+05:30 ERROR 26586 --- [httpTwins] [       Thread-3] c.e.h.s.RemoteDestinationProcessor       : HttpTwins ERROR: Failed to mirror request to remote destination 'https://http-twins-remote-test.free.beeceptor.com/regionalTests'. Reason: 404 Not Found on POST request for "https://http-twins-remote-test.free.beeceptor.com/regionalTests": "Hey ya! Great to see you here. BTW, nothing is configured here. Create a mock server on Beeceptor.com"

```

## Dive Deeper and Explore the Code!

The real magic happens in the `HttpTwinsAspect`, which seamlessly intercepts the annotation, finds the correct processor, and manages the asynchronous execution.

## 🧱 Future Extensions

- Support for multiple destination handlers  
- Integration with Kafka, RabbitMQ, or Cloud Pub/Sub  
- Dynamic fan-out strategies (e.g., rules-based or event-driven routing)  

---

## ⚖️ License

**HttpTwins** is licensed under the **Apache License, Version 2.0**.  
You may use, modify, and distribute this project freely under the terms of the license.
http://www.apache.org/licenses/LICENSE-2.0

**Author:** Gyanendra Ojha  
**Version:** 1.0  
**Framework:** Spring Boot  
**Category:** Asynchronous Design Pattern for HTTP Fan-out  
**License:** Apache 2.0
