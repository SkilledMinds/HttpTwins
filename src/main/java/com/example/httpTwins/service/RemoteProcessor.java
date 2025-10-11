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

/**
 * Defines the contract for a class that can process a mirrored HTTP request and send it to a remote destination.
 */
public interface RemoteProcessor {

    /**
     * Processes the incoming HTTP request and sends it to the specified remote URL.
     *
     * @param request The HttpServletRequest to be processed.
     * @param remoteUrl The remote URL to which the request should be sent.
     */
    void process(HttpServletRequest request, String remoteUrl);
}
