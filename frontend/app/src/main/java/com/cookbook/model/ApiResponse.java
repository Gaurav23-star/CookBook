package com.cookbook.model;

public class ApiResponse {
    // HTTP response code indicating the status of the API call
    private int response_code;

    // Response body containing data or error messages returned by the API
    private String response_body;

    // Constructor for creating an ApiResponse object with response code and body
    public ApiResponse(int response_code, String response_body) {
        this.response_code = response_code;
        this.response_body = response_body;
    }

    // Getter method to retrieve the HTTP response code
    public int getResponse_code() {
        return response_code;
    }

    // Setter method to set the HTTP response code
    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    // Getter method to retrieve the response body
    public String getResponse_body() {
        return response_body;
    }

    // Setter method to set the response body
    public void setResponse_body(String response_body) {
        this.response_body = response_body;
    }

    // Override toString method to provide a string representation of the ApiResponse object
    @Override
    public String toString() {
        return "ApiResponse{" +
                "response_code=" + response_code +
                ", response_body='" + response_body + '\'' +
                '}';
    }
}
