package com.example.demo.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController {

    @GetMapping
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("hello");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getId(@PathVariable String id){
        return ResponseEntity.ok("Hello "+id);
    }
}
