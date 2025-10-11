/*
 * Copyright 2024 Gyanendra Ojha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law of or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.httpTwins.annotation;

import com.example.httpTwins.service.RemoteDestinationProcessor;
import com.example.httpTwins.service.RemoteProcessor;
import com.example.httpTwins.service.RequestProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpTwins {

    /**
     * An array of local destination classes that will process the mirrored request.
     * These classes must implement the {@link RequestProcessor} interface.
     */
    Class<? extends RequestProcessor>[] localdestinations() default {};

    /**
     * The class responsible for processing remote destinations.
     * This class must implement the {@link RemoteProcessor} interface.
     * Defaults to {@link RemoteDestinationProcessor}.
     */
    Class<? extends RemoteProcessor> remoteProcessor() default RemoteDestinationProcessor.class;

    /**
     * An array of remote URLs that will receive a copy of the HTTP request.
     * The {@link #remoteProcessor()} will be invoked for each URL.
     */
    String[] remoteDestinations() default {};

    /**
     * A boolean flag to enable or disable the annotation's functionality.
     * Defaults to true.
     */
    boolean active() default true;
}
