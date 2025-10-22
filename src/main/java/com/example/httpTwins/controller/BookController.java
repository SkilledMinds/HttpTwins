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
package com.example.httpTwins.controller;

import com.example.httpTwins.annotation.HttpTwins;
import com.example.httpTwins.model.Book;
import com.example.httpTwins.repository.BookRepository;
import com.example.httpTwins.service.RemoteERP;
import com.example.httpTwins.service.ReportingAgent;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    @HttpTwins(localdestinations = ReportingAgent.class, active = "${http-twins.get-books.enabled}") // Fan-out to a local reporting agent
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @PostMapping
    @HttpTwins(
        localdestinations = {RemoteERP.class, ReportingAgent.class},
        remoteDestinations = {
            "https://http-twins-remote-test.free.beeceptor.com/regionalTests", // A test endpoint to receive mirrored requests
            "https://ecom.buyers/items" // A second remote destination
        }
    )
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }
}
