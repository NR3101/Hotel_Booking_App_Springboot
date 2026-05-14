package com.nr3101.hotelbookingapp.advice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@Schema(description = "Error details returned when a request fails")
public class ApiError {

    @Schema(description = "HTTP status code", example = "BAD_REQUEST")
    private HttpStatus status;

    @Schema(description = "Error message", example = "Validation failed")
    private String message;

    @Schema(description = "Detailed sub-errors (e.g. field validation failures)")
    private List<String> subErrors;
}
