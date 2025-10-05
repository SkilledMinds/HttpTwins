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

@Service("remoteERP") // This bean name matches the annotation's destination
public class RemoteERP implements RequestProcessor {

    @Override
    public void process(HttpServletRequest request) {
        // The HttpTwinsAspect now handles the detailed logging.
        // This service can now focus purely on its business logic.

        // For example, you would format the request data and send it to a remote ERP system here.
        System.out.println("-> Executing RemoteERP business logic.");
    }
}
