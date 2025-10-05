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
package com.example.httpTwins.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service("remoteERP") // This bean name matches the annotation's destination
public class RemoteERP implements RequestProcessor {

    @Override
    public void process(HttpServletRequest request) {
        // This processor now decides exactly what to log.
        System.out.println("\n==================== HttpTwins Request for [RemoteERP] ====================");
        System.out.println("Method: " + request.getMethod());
        System.out.println("URI: " + request.getRequestURI());

        System.out.println("--- Headers ---");
        Collections.list(request.getHeaderNames())
            .forEach(name -> System.out.println(name + ": " + request.getHeader(name)));

        System.out.println("--- Body ---");
        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
        byte[] body = wrapper.getContentAsByteArray();
        if (body.length > 0) {
            System.out.println(new String(body, StandardCharsets.UTF_8));
        } else {
            System.out.println("[No Body]");
        }

        // After logging, it can execute its specific business logic.
        System.out.println("-> Executing RemoteERP business logic...");
        System.out.println("=========================================================================\n");
    }
}
