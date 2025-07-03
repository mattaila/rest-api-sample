package com.example.restapi.domain.opts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.restapi.generated.api.HealthApi;

@RestController
public class HealthController implements HealthApi {

    @Override
    public ResponseEntity<Void> healthGet() {
        return ResponseEntity.ok().build();
    }
}
