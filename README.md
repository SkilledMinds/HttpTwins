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
        localdestinations = {"remoteERP", "reportingAgent"},
        
        // Also send to multiple remote endpoints
        remoteDestinations = {
            "https://http-twins-remote-test.free.beeceptor.com/regionalTests",
            "https://ecom.buyers/items"
        }
        ,active = false
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
@Service("remoteERP")
public class RemoteERP implements RequestProcessor {

    @Override
    public void process(HttpServletRequest request) {
        // Add your logic here to process the mirrored request,
        // e.g., send it to an external service.
        System.out.println("Request mirrored to RemoteERP!");
    }
}
```

---
### 3. Supporting logs

Use HTTP client attached with project to execute REST APIs attached with HTTPTwins

```java
==================== HttpTwins Request for [RemoteERP] ====================

        --- HttpTwins Request for [ReportingAgent] ---
Method: POST
URI: /books
--- Headers ---
Processing POST request for URI: /books
-> Executing ReportingAgent business logic...
        --------------------------------------------

content-length: 83
host: localhost:8080
user-agent: Java-http-client/21.0.8
content-type: application/json
--- Body ---
        {
        "title": "The Hitchhiker's Guide to the Galaxy",
        "author": "Douglas Adams"
        }

        -> Executing RemoteERP business logic...
        =========================================================================
( These are dummy remote endpoints )
HttpTwins ERROR: Failed to mirror request to remote destination 'https://ecom.buyers/items'. Reason: I/O error on POST request for "https://ecom.buyers/items": ecom.buyers
HttpTwins ERROR: Failed to mirror request to remote destination 'https://http-twins-remote-test.free.beeceptor.com/regionalTests'. Reason: 404 Not Found on POST request for "https://http-twins-remote-test.free.beeceptor.com/regionalTests": "Hey ya! Great to see you here. BTW, nothing is configured here. Create a mock server on Beeceptor.com"

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
