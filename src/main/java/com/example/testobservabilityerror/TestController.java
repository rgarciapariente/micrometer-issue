package com.example.testobservabilityerror;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
@RestController
public class TestController {

    @GetMapping(value = "/test-traceid")
    public Mono<ResponseEntity<String>> testTraceId() {
        return Mono.just("testTraceId")
                .handle((s, synchronousSink) -> {
                    System.out.println("("+Thread.currentThread().getName().toString()+")" + MDC.getCopyOfContextMap());
                    synchronousSink.next(MDC.get("traceId").toString());
                })
                .map(obj -> ResponseEntity.ok(obj.toString()));
    }


}
