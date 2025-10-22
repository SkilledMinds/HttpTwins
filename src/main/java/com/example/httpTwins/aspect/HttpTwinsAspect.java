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
import com.example.httpTwins.service.RemoteProcessor;
import com.example.httpTwins.service.RequestProcessor;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Aspect
@Component
public class HttpTwinsAspect {

    private static final Logger logger = LoggerFactory.getLogger(HttpTwinsAspect.class);

    private final ApplicationContext applicationContext;
    private final Environment environment;
    private final RequestProcessor defaultRequestProcessor;

    public HttpTwinsAspect(ApplicationContext applicationContext, Environment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;
        this.defaultRequestProcessor = createDefaultProcessor();
    }

    @Before("@annotation(httpTwins)")
    public void fanoutHttpRequest(JoinPoint joinPoint, HttpTwins httpTwins) {
        if (!isActive(httpTwins.active())) {
            return;
        }

        Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> {
                    // Handle local destinations
                    for (Class<? extends RequestProcessor> destinationClass : httpTwins.localdestinations()) {
                        new Thread(() -> processLocalDestination(request, destinationClass)).start();
                    }

                    // Handle remote destinations
                    Class<? extends RemoteProcessor> remoteProcessorClass = httpTwins.remoteProcessor();
                    RemoteProcessor remoteProcessor = applicationContext.getBean(remoteProcessorClass);
                    for (String remoteUrl : httpTwins.remoteDestinations()) {
                        new Thread(() -> remoteProcessor.process(request, remoteUrl)).start();
                    }

                    // Handle default case if no destinations are specified
                    if (httpTwins.localdestinations().length == 0 && httpTwins.remoteDestinations().length == 0) {
                        new Thread(() -> defaultRequestProcessor.process(request)).start();
                    }
                });
    }

    private boolean isActive(String activeValue) {
        if (activeValue == null || activeValue.trim().isEmpty()) {
            return false;
        }
        String resolvedValue = environment.resolvePlaceholders(activeValue);
        return Boolean.parseBoolean(resolvedValue);
    }

    private void processLocalDestination(HttpServletRequest request, Class<? extends RequestProcessor> destinationClass) {
        try {
            RequestProcessor processor = applicationContext.getBean(destinationClass);
            processor.process(request);
        } catch (NoSuchBeanDefinitionException e) {
            logger.warn("HttpTwins WARNING: No RequestProcessor bean found of type '{}'.", destinationClass.getName());
        }
    }

    private RequestProcessor createDefaultProcessor() {
        return request -> {
            logger.info("\n--- HttpTwins Default Logger ---");
            logger.info("Method: {}", request.getMethod());
            logger.info("URI: {}", request.getRequestURI());
            logger.info("------------------------------\n");
        };
    }
}
