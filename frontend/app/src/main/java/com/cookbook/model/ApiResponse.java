package com.cookbook.model;

public class ApiResponse {
    private int response_code;
    private String response_body;

    public ApiResponse(int response_code, String response_body) {
        this.response_code = response_code;
        this.response_body = response_body;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public String getResponse_body() {
        return response_body;
    }

    public void setResponse_body(String response_body) {
        this.response_body = response_body;
    }
}
