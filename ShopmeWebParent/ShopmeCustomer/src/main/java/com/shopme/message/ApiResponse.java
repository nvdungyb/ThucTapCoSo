package com.shopme.message;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ApiResponse<T> {
    private String timestamp;
    private int status;
    private String message;
    private T data;
    private Map<String, String> errors;
    private String path;
}
