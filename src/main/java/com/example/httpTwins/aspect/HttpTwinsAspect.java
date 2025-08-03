/*
 * Copyright 2024 Gyanendra Ojha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.httpTwins.aspect;

import com.example.httpTwins.annotation.HttpTwins;
import com.example.httpTwins.service.RequestProcessor;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Collections;
import java.util.Optional;

@Aspect
@Component
public class HttpTwinsAspect {

    private final ApplicationContext applicationContext;
    private final RequestProcessor defaultRequestProcessor;
    private final RestTemplate restTemplate;

    public HttpTwinsAspect(ApplicationContext applicationContext, RestTemplate restTemplate) {
        this.applicationContext = applicationContext;
        this.restTemplate = restTemplate;
        this.defaultRequestProcessor = createDefaultProcessor();
    }

    @Before("@annotation(httpTwins)")
    public void fanoutHttpRequest(JoinPoint joinPoint, HttpTwins httpTwins) {
        if (!httpTwins.active()) {
            return;
        }

        Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> {
                    // Handle local destinations
                    for (String destination : httpTwins.localdestinations()) {
                        new Thread(() -> processLocalDestination(request, destination)).start();
                    }

                    // Handle remote destinations
                    for (String remoteUrl : httpTwins.remoteDestinations()) {
                        new Thread(() -> processRemoteDestination(request, remoteUrl)).start();
                    }

                    // Handle default case if no destinations are specified
                    if (httpTwins.localdestinations().length == 0 && httpTwins.remoteDestinations().length == 0) {
                        new Thread(() -> defaultRequestProcessor.process(request)).start();
                    }
                });
    }

    private void processLocalDestination(HttpServletRequest request, String destination) {
        try {
            RequestProcessor processor = applicationContext.getBean(destination, RequestProcessor.class);
            processor.process(request);
        } catch (NoSuchBeanDefinitionException e) {
            System.err.println("HttpTwins WARNING: No RequestProcessor bean found with name '" + destination + "'.");
        }
    }

    private void processRemoteDestination(HttpServletRequest request, String remoteUrl) {
        try {
            byte[] body = ((ContentCachingRequestWrapper) request).getContentAsByteArray();

            HttpHeaders headers = new HttpHeaders();
            Collections.list(request.getHeaderNames())
                .forEach(name -> headers.add(name, request.getHeader(name)));

            HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);
            HttpMethod method = HttpMethod.valueOf(request.getMethod());

            restTemplate.exchange(remoteUrl, method, entity, String.class);
            System.out.println("HttpTwins: Successfully mirrored request to remote destination: " + remoteUrl);

        } catch (Exception e) {
            System.err.println("HttpTwins ERROR: Failed to mirror request to remote destination '" + remoteUrl + "'. Reason: " + e.getMessage());
        }
    }

    private RequestProcessor createDefaultProcessor() {
        return request -> {
            System.out.println("--- HttpTwins Default Logger ---");
            System.out.println("Method: " + request.getMethod());
            System.out.println("URI: " + request.getRequestURI());
            System.out.println("------------------------------");
        };
    }
}
