package com.aprendiz.demo.exception;

import java.time.Instant;

public record ApiError(int status, String mensaje, Instant fecha) {

    public static ApiError de(int status, String mensaje) {
        return new ApiError(status, mensaje, Instant.now());
    }
}
