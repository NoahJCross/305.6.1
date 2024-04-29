package com.example.a61;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class QuizAPICaller {

    // Interface to handle quiz response callbacks
    public interface QuizResponseListener {
        void onQuizReceived(List<Question> questions);
        void onQuizError(Exception e);
    }

    // Method to fetch quiz questions from the API
    public static void getQuiz(String topic, QuizResponseListener listener, Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.3:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        QuizRequest request = retrofit.create(QuizRequest.class);

        Call<QuizResponse> call = request.getQuiz(topic);
        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Quiz quiz = response.body().getQuiz();
                        List<Question> questions = parseQuizResponse(quiz, context);
                        listener.onQuizReceived(questions);
                    } catch (JSONException e) {
                        listener.onQuizError(e);
                    }
                } else {
                    listener.onQuizError(new Exception("Failed to get quiz. Response code: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {
                listener.onQuizError(new Exception("Failed to get quiz. Error: " + t.getMessage()));
            }
        });
    }

    // Method to parse the quiz response and convert it into Question objects
    private static List<Question> parseQuizResponse(Quiz quiz, Context context) throws JSONException {
        List<Question> questions = new ArrayList<>();
        TaskDbHandler taskDbHandler;
        QuestionDbHandler questionDbHandler;

        String title = quiz.getTitle();
        String description = quiz.getDescription();
        Task task = new Task(User.getInstance().getId(), title, description);
        taskDbHandler = new TaskDbHandler(context);
        long taskId = taskDbHandler.addTask(task);
        List<QuestionData> questionsData = quiz.getQuestions();

        for (QuestionData questionData : questionsData) {
            String questionText = questionData.getQuestion();
            List<String> options = questionData.getOptions();
            String correctAnswer = questionData.getCorrectAnswer();
            Question question = new Question(taskId, questionText, options.get(0), options.get(1), options.get(2), Integer.parseInt(correctAnswer));
            questionDbHandler = new QuestionDbHandler(context);
            questionDbHandler.addQuestion(question);
            questions.add(question);
        }
        return questions;
    }

    // Retrofit interface for making API requests
    interface QuizRequest {
        @GET("getQuiz")
        Call<QuizResponse> getQuiz(@Query("topic") String topic);
    }

    // Response class for the quiz API
    public static class QuizResponse {
        private Quiz quiz;

        public Quiz getQuiz() {
            return quiz;
        }
    }

    // Model class representing a quiz
    public static class Quiz {
        private String title;
        private String description;
        private List<QuestionData> questions;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public List<QuestionData> getQuestions() {
            return questions;
        }
    }

    // Model class representing question data
    public static class QuestionData {
        private String question;
        private List<String> options;
        private String correct_answer;

        public String getQuestion() {
            return question;
        }

        public List<String> getOptions() {
            return options;
        }

        public String getCorrectAnswer() {
            return correct_answer;
        }
    }
}
