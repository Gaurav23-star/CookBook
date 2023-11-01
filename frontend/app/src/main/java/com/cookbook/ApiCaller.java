package com.cookbook;

import com.cookbook.model.ApiResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

final public class ApiCaller {
    private static ApiCaller apiCaller;

    //API URLS
    private static final String LOGIN_URL = "http://172.16.122.20:8080/login";
    private static final String SIGNUP_URL = "http://172.16.122.20:8080/create-account";
    private static final String RECIPE_URL = "http://172.16.122.20:8080/user-defined-recipes";


    private ApiCaller(){

    }
    public static ApiCaller get_caller_instance(){
        if(apiCaller == null){
            apiCaller = new ApiCaller();
        }
        return apiCaller;
    }

    private ApiResponse get_request(String sUrl, String query){
        try {
            final URL url = new URL(sUrl + query);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String responseBody = convertStreamToString(connection.getInputStream());
            connection.disconnect();
            return new ApiResponse(connection.getResponseCode(), responseBody);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ApiResponse post_request(String sUrl, String RequestBody){

        try {
            final URL url = new URL(sUrl);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);


            final OutputStream os = connection.getOutputStream();
            final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);

            osw.write(RequestBody);
            osw.flush();

            int responseCode = connection.getResponseCode();
            String response = "";

            if(responseCode == HttpURLConnection.HTTP_OK){
                response = convertStreamToString(connection.getInputStream());
            }
            System.out.println("POST: API RESPONSE " + response);
            connection.disconnect();
            System.out.println("POST: API RESPONSE CODE " + connection.getResponseCode());
            return new ApiResponse(responseCode, response);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ApiResponse login(String email, String password){
        final String jsonData = "{\"email_id\":\"" + email + "\", \"password\":\"" + password + "\"}";
        return post_request(LOGIN_URL, jsonData);
    }

    public ApiResponse signup(String firstName, String lastName, String email, String password, String username){
        final String jsonData = "{\"first_name\": \"" + firstName + "\"," +
                "\"last_name\": \"" + lastName + "\", " +
                "\"email_id\":\"" + email + "\", " +
                "\"password\":\"" + password + "\", " +
                "\"username\":\"" + username + "\"}";

        return post_request(SIGNUP_URL, jsonData);
    }

    public ApiResponse getAllRecipes(){
        return get_request(RECIPE_URL, "");
    }

    private String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
