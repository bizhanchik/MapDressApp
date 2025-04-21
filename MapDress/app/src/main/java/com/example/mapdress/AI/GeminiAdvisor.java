// GeminiAdvisor.java
package com.example.mapdress.AI;

import com.example.mapdress.BuildConfig;
import com.google.gson.annotations.SerializedName;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class GeminiAdvisor {
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/";
    private static GeminiApi geminiApi;

    public static void initialize() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        geminiApi = retrofit.create(GeminiApi.class);
    }

    public static GeminiApi getApi() {
        if (geminiApi == null) initialize();
        return geminiApi;
    }

    public interface GeminiApi {
        @Headers("Content-Type: application/json")
        @POST("models/gemini-pro:generateContent")
        Call<GenerateContentResponse> generateContent(
                @Body GenerateContentRequest request,
                @Query("key") String apiKey
        );
    }

    public static class GenerateContentRequest {
        @SerializedName("contents")
        public Message[] contents;
        public GenerateContentRequest(Message[] contents) { this.contents = contents; }
    }

    public static class Message {
        @SerializedName("parts")
        public Part[] parts;
        public Message(Part[] parts) { this.parts = parts; }
    }

    public static class Part {
        @SerializedName("text")
        public String text;
        public Part(String text) { this.text = text; }
    }

    public static class GenerateContentResponse {
        @SerializedName("candidates")
        public Candidate[] candidates;
    }

    public static class Candidate {
        @SerializedName("content")
        public Content content;
    }

    public static class Content {
        @SerializedName("parts")
        public Part[] parts;
    }
}
