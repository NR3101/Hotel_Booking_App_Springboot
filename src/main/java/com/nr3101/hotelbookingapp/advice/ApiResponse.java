package com.nr3101.hotelbookingapp.advice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Response timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Response data payload")
    private T data;

    @Schema(description = "Error details (null on success)")
    private ApiError error;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public ApiResponse(ApiError error) {
        this();
        this.error = error;
    }
}
