package com.example.restapi.domain.opts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.restapi.generated.api.HealthApi;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class HealthController implements HealthApi {

    @Override
    public ResponseEntity<Void> healthGet() {
        return ResponseEntity.ok().build();
    }
}
