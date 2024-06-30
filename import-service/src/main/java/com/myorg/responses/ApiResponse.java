package com.myorg.responses;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class ApiResponse {

    private final int statusCode;

    private final Map<String, String> headers;

    private final String body;
}
