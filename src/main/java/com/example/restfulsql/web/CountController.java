package com.example.restfulsql.web;

import com.example.restfulsql.api.CountRequest;
import com.example.restfulsql.api.CountResponse;
import com.example.restfulsql.service.CountService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/count", produces = MediaType.APPLICATION_JSON_VALUE)
public class CountController {

    private final CountService countService;

    public CountController(CountService countService) {
        this.countService = countService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CountResponse count(@Valid @RequestBody CountRequest request) {
        long count = countService.countRows(request);
        return new CountResponse(count);
    }
}
