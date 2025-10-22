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

@Service
public class ReportingAgent implements RequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ReportingAgent.class);

    @Override
    public void process(HttpServletRequest request) {
        logger.info("\n--- HttpTwins Request for [ReportingAgent] ---");
        logger.info("Processing {} request for URI: {}", request.getMethod(), request.getRequestURI());
        logger.info("-> Executing ReportingAgent business logic...");
        logger.info("--------------------------------------------\n");
    }
}
