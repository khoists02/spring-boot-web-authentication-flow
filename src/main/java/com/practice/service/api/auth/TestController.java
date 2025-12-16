package com.practice.service.api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("hello");
    }
}
