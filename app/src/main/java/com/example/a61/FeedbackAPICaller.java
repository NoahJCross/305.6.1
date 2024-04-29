package com.example.a61;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class FeedbackAPICaller {

    // Interface to listen for feedback response or errors
    public interface FeedbackResponseListener {
        void onFeedbackReceived(List<Result> results);
        void onFeedbackError(Exception e);
    }

    // Method to get feedback from the API
    public static void getFeedback(String quizResultsJson, FeedbackResponseListener listener) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Create OkHttpClient with custom timeout settings
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(300, TimeUnit.SECONDS)
                        .readTimeout(300, TimeUnit.SECONDS)
                        .build();

                // Create Retrofit instance
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.0.3:5000/") // Base URL of the API
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();

                // Create Retrofit service interface for making requests
                FeedbackRequest request = retrofit.create(FeedbackRequest.class);

                // Create request body with quiz results in JSON format
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), quizResultsJson);

                // Make API call to get feedback
                Call<FeedbackResponse> call = request.getFeedback(requestBody);
                call.enqueue(new Callback<FeedbackResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FeedbackResponse> call, @NonNull Response<FeedbackResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Parse feedback response
                            List<Result> feedbackResults = parseFeedbackResponse(response.body());
                            // Notify listener with feedback results
                            listener.onFeedbackReceived(feedbackResults);
                        } else {
                            // Notify listener of error when response is not successful
                            listener.onFeedbackError(new Exception("Failed to get feedback. Response code: " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FeedbackResponse> call, @NonNull Throwable t) {
                        // Notify listener of error when API call fails
                        listener.onFeedbackError(new Exception("Failed to get feedback. Error: " + t.getMessage()));
                    }
                });
            } catch (Exception e) {
                // Log error and notify listener of error on the main thread
                Log.e("API", "Error: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> listener.onFeedbackError(e));
            }
        });
    }

    // Method to parse feedback response
    private static List<Result> parseFeedbackResponse(FeedbackResponse feedbackResponse) {
        List<Result> feedbackResults = new ArrayList<>();
        try {
            List<Result> feedbackList = feedbackResponse.getFeedback();

            for (Result feedback : feedbackList) {
                // Extract question text and feedback text from each feedback item
                String questionText = feedback.getTitle();
                String feedbackText = feedback.getFeedback();
                // Create a Result object with question text and feedback text, and add it to the list
                Result feedbackResult = new Result(questionText, feedbackText);
                feedbackResults.add(feedbackResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feedbackResults;
    }

    // Retrofit service interface for making feedback request
    public interface FeedbackRequest {
        @POST("getFeedback") // Endpoint for getting feedback
        Call<FeedbackResponse> getFeedback(@Body RequestBody quizResultsJson); // POST method with request body
    }
}
