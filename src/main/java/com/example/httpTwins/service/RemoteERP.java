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

import com.example.httpTwins.service.RequestProcessor;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

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
