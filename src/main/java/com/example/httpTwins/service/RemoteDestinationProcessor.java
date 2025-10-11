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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Collections;

@Component
public class RemoteDestinationProcessor implements RemoteProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RemoteDestinationProcessor.class);
    private final RestTemplate restTemplate;

    public RemoteDestinationProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void process(HttpServletRequest request, String remoteUrl) {
        try {
            byte[] body = ((ContentCachingRequestWrapper) request).getContentAsByteArray();

            HttpHeaders headers = new HttpHeaders();
            Collections.list(request.getHeaderNames())
                .forEach(name -> headers.add(name, request.getHeader(name)));

            HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);
            HttpMethod method = HttpMethod.valueOf(request.getMethod());

            restTemplate.exchange(remoteUrl, method, entity, String.class);
            logger.info("HttpTwins: Successfully mirrored request to remote destination: {}", remoteUrl);

        } catch (Exception e) {
            logger.error("HttpTwins ERROR: Failed to mirror request to remote destination '{}'. Reason: {}", remoteUrl, e.getMessage());
        }
    }
}
