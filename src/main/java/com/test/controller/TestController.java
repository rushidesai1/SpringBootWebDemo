package com.test.controller;

import com.test.domain.frontend.dto.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class TestController {

    private static final String template = "Hello, %s!";
    //    private static DynamicLongProperty timeToWait = DynamicPropertyFactory
//            .getInstance().getLongProperty("hystrixdemo.sleep", 100);
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        System.out.println("name : " + name);
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
}
