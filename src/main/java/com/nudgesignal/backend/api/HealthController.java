package com.nudgesignal.backend.api;

import com.nudgesignal.backend.api.dto.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    private static final String SERVICE_NAME = "nudge-signal-backend";

    @GetMapping("/ping")
    public HealthResponse ping() {
        return HealthResponse.up(SERVICE_NAME);
    }
}
