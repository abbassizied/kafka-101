package io.github.abbassizied.order_service.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeResource {

    @GetMapping("/")
    public String index() {
        return "<h1>Order Service Index Page</h1>";
    }

}
