package com.practice.service.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    @PreAuthorize("hasPermission('viewUser')")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("hello");
    }
}
