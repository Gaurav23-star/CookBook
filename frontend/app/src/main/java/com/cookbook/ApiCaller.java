package com.cookbook;

import android.net.Uri;
import android.widget.ImageView;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.Comment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

final public class ApiCaller {
    private static ApiCaller apiCaller;


    //API URLS
    private static final String production_host = "http://172.16.122.20:8080";
    private static final String development_host = "http://10.0.2.2:8080";
    public static final String host = production_host;
    private static final String USER_SEARCH_URL = host + "/user-search?text=";
    private static final String USER_IS_FOLLOWING_URL = host + "/user-is-following?user_id=";
    private static final String USER_FOLLOW_URL = host + "/user-follow";
    private static final String USER_UNFOLLOW_URL = host + "/user-unfollow";

    private static final String LOGIN_URL = host + "/login";
    private static final String SIGNUP_URL = host + "/create-account";
    private static final String RECIPE_URL = host + "/user-defined-recipes";
    private static final String FAVORITES_URL = host + "/favorites";
    private static final String USER_CREATED_RECIPES_URL = host + "/user-defined-recipes?user_id=";
    private static final String USER_FOLLOWER_FOLLOWING_COUNT_URL = host + "/user-followers-following-count?user_id=";
    private static final String USERS_NETWORK_LIST_URL = host + "/users-network-list?user_id=";
    public static final String GET_RECIPE_IMAGE_URL = host + "/user-defined-recipes/download_image/";
    private static final String COMMENTS_URL = RECIPE_URL + "/comments";
    //private static final String COMMENTS_URL = "http://10.66.7.132:8080/user-defined-recipes/comments";
    private static final String POST_NEW_RECIPE_URL = host + "/user-defined-recipes";

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


    private ApiResponse delete_request(String sUrl, String RequestBody){
        try {
            final URL url = new URL(sUrl);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
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
            System.out.println("DELETE: API RESPONSE " + response);
            connection.disconnect();
            System.out.println("DELETE: API RESPONSE CODE " + connection.getResponseCode());
            return new ApiResponse(responseCode, response);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ApiResponse login(String email, String password) {
        final String jsonData = "{\"email_id\":\"" + email + "\", \"password\":\"" + password + "\"}";
        ApiResponse apiResponse =  post_request(LOGIN_URL, jsonData);
        if(apiResponse != null && apiResponse.getResponse_code() == HttpURLConnection.HTTP_OK){
            String response = apiResponse.getResponse_body();
            try {
                JSONObject jsonObject = new JSONObject(response);
                apiResponse.setResponse_body(jsonObject.getString("user"));
            }
            catch (JSONException ignored){

            }
        }
        return apiResponse;
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

    public ApiResponse getFavoriteRecipes(final String user_id) {
        return get_request(FAVORITES_URL, user_id);
    }

    public void getRecipeImage(){

    }

    public ApiResponse getAllUserCreatedRecipes(String user_id){
        System.out.println("GET ALL USERS " + user_id);
        return get_request(USER_CREATED_RECIPES_URL,user_id);
    }

    public ApiResponse getUsersFollowersAndFollowingCount(String user_id){
        return get_request(USER_FOLLOWER_FOLLOWING_COUNT_URL,user_id);
    }

    public ApiResponse getUsersNetworkList(String user_id, String networkType){
        return get_request(USERS_NETWORK_LIST_URL, user_id + "&"+"network_type="+networkType);
    }

    public ApiResponse getUserSearch(String text){
        return get_request(USER_SEARCH_URL, text);
    }

    public ApiResponse getUserIsFollowingVisitingUser(String userId, String visitorId){
        return get_request(USER_IS_FOLLOWING_URL, userId + "&"+"visitor_id="+visitorId);
    }

    public ApiResponse UserFollowVisitingUser(String userId, String visitorId){
    final String jsonData = "{\"user_id\": \"" + userId + "\"," +
            "\"visitor_id\":\"" + visitorId + "\"}";

        return post_request(USER_FOLLOW_URL, jsonData );
    }

    public ApiResponse UserUnfollowVisitingUser(String loggedInUserId, String currentUserId) {
        final String jsonData = "{\"user_id\":\"" + loggedInUserId + "\", \"visitor_id\":\"" + currentUserId + "\"}";
        return delete_request(USER_UNFOLLOW_URL, jsonData);
    }


    public ApiResponse getAllComments(int recipe_id){
        String queryParams = "?recipe_id=" + recipe_id;
        return get_request(COMMENTS_URL, queryParams);
    }

    public ApiResponse postComment(Comment comment){
        String json = "{\"recipe_id\": \"" + comment.getRecipe_id() + "\"," +
                      "\"comment\": \"" + comment.getComment() + "\"," +
                      "\"user_id\": \"" + comment.getUser_id() + "\"}";
        return post_request(COMMENTS_URL, json);
    }

    private String convertStreamToString(InputStream is) {
        final Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public ApiResponse uploadRecipeImage(File file) {
        final ApiResponse[] apiResponse = {null};
        String baseUrl = host + "/user-defined-recipes/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        RequestBody recipeId = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

        uploadImageService upload = retrofit.create(uploadImageService.class);
        Call<ApiResponse> responseCall = upload.uploadRecipeImage(body, recipeId);


        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                System.out.println("SUCCESS");
                System.out.println("response code " + response.body().getResponse_code());
                System.out.println("response body " + response.body().getResponse_body());
                apiResponse[0] = new ApiResponse(response.body().getResponse_code(), response.body().getResponse_body());
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                System.out.println("FAILURE");
            }

        });

        System.out.println("RESPONSE SENT");
        return apiResponse[0];

    }

    public ApiResponse postNewRecipe(String recipeName, String desc, String servings, String prepareTime, String ingredients, String instructions, int userId){

        String json = "{\"recipe_name\": \"" + recipeName + "\"," +
                "\"servings\" : \"" + servings + "\"," +
                "\"preparation_time_minutes\" : \"" + prepareTime + "\"," +
                "\"ingredients\" : \"" + ingredients + "\"," +
                "\"description\" : \"" + desc + "\"," +
                "\"instructions\" : \"" + instructions + "\"," +
                "\"user_id\" : \"" + userId + "\"}";


        System.out.println("JSON POST IS " + json);
        return post_request(POST_NEW_RECIPE_URL, json);
    }


    interface uploadImageService {
        @Multipart
        @POST("upload_image")
        Call<ApiResponse> uploadRecipeImage(@Part MultipartBody.Part image, @Part("image_path") RequestBody recipeId);
    }
}
