package com.nudgesignal.backend.api.dto;

public record HealthResponse(String status, String service) {

    public static HealthResponse up(String service) {
        return new HealthResponse("ok", service);
    }
}
