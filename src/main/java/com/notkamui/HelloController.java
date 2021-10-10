package com.notkamui;

import com.notkamui.domain.HelloDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import java.net.URI;

@ExecuteOn(TaskExecutors.IO)
@Controller("/hello")
public class HelloController {

    private final HelloRepository repository;

    public HelloController(HelloRepository repository) {
        this.repository = repository;
    }

    @Get(produces = MediaType.TEXT_PLAIN)
    public String index() {
        return "Hello World";
    }

    @Post
    public HttpResponse<HelloDTO> save() {
        var hello = repository.save();
        return HttpResponse
            .created(hello)
            .headers(headers -> headers.location(URI.create("/hello/" + hello.getId())));
    }
}
