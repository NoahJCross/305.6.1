package com.example.a61;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity implements FeedbackAPICaller.FeedbackResponseListener {
    private TextView taskTitleTextView;
    private TextView taskDescriptionTextView;
    private Button submitButton;
    private FrameLayout taskLoaderFrame;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int currentQuestionAnswer = -1;
    private QuestionDbHandler questionDbHandler;
    private TaskDbHandler taskDbHandler;
    private long taskId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);

        // Retrieve task ID from intent
        Intent intent = getIntent();
        taskId = intent.getLongExtra("task_id", 0);

        // Initialize database handlers
        taskDbHandler = new TaskDbHandler(TaskActivity.this);
        taskTitleTextView = findViewById(R.id.taskTitleTextView);
        taskDescriptionTextView = findViewById(R.id.taskDescriptionTextView);
        taskLoaderFrame = findViewById(R.id.taskLoaderFrame);

        // Set task title and description
        Task task = taskDbHandler.getTaskById(taskId);
        taskTitleTextView.setText(task.getTitle());
        taskDescriptionTextView.setText(task.getDescription());

        // Initialize submit button and set click listener
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex == questions.size() - 1) {
                    // Prepare JSON data and call feedback API
                    JSONObject quizResultsJson = prepareQuizResultsJson();
                    taskLoaderFrame.setVisibility(View.VISIBLE);
                    FeedbackAPICaller.getFeedback(quizResultsJson.toString(), TaskActivity.this);
                }
            }
        });

        // Initialize database handler for questions
        questionDbHandler = new QuestionDbHandler(TaskActivity.this);
        questions = questionDbHandler.getQuestionsByTaskId(taskId);

        // Display the first question
        displayQuestion(currentQuestionIndex);
    }

    // Prepare JSON object with quiz results
    private JSONObject prepareQuizResultsJson() {
        JSONObject quizResultsJson = new JSONObject();
        try {
            JSONArray resultsArray = new JSONArray();
            for (Question question : questions) {
                JSONObject questionResult = new JSONObject();
                questionResult.put("question", question.getQuestion());
                questionResult.put("correct_answer", question.getCorrectAnswer());
                questionResult.put("users_answer", question.getUsersAnswer());
                resultsArray.put(questionResult);
            }
            quizResultsJson.put("results", resultsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return quizResultsJson;
    }

    // Move to the next question
    public void NextQuestion() {
        if (currentQuestionAnswer != -1) {
            Question question = questions.get(currentQuestionIndex);
            question.setUsersAnswer(currentQuestionAnswer);
            questionDbHandler.updateUserAnswer(question.getId(), currentQuestionAnswer);
            currentQuestionIndex++;
            currentQuestionAnswer = -1;
            displayQuestion(currentQuestionIndex);
        } else {
            Toast.makeText(this, "Please answer the current question.", Toast.LENGTH_SHORT).show();
        }
    }

    // Move to the previous question
    public void PrevQuestion() {
        if (currentQuestionIndex == questions.size() - 1) {
            submitButton.setVisibility(View.GONE);
        }
        currentQuestionIndex--;
        currentQuestionAnswer = -1;
        displayQuestion(currentQuestionIndex);
    }

    // Display a question fragment
    private void displayQuestion(int index) {
        QuestionFragment fragment = QuestionFragment.newInstance(questions, currentQuestionIndex);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment).commit();
    }

    // Update the current question's answer
    public void UpdateAnswer(int answer) {
        currentQuestionAnswer = answer;
    }

    // Callback method invoked when feedback is received
    @Override
    public void onFeedbackReceived(List<Result> results) {
        // Update question feedback in database
        for (int i = 0; i < results.size(); i++) {
            for (Question question : questions) {
                if (question.getQuestion().equals(results.get(i).getTitle())) {
                    question.setFeedback(results.get(i).getFeedback());
                    questionDbHandler.updateQuestionFeedback(question.getId(), results.get(i).getFeedback());
                    break;
                }
            }
        }
        // Start results activity
        Intent intent = new Intent(TaskActivity.this, ResultsActivity.class);
        intent.putExtra("task_id", taskId);
        startActivity(intent);
    }

    // Callback method invoked when feedback API call encounters an error
    @Override
    public void onFeedbackError(Exception e) {
        Toast.makeText(TaskActivity.this, "Connection Error, trying again.", Toast.LENGTH_SHORT).show();
        Log.d("API", e.getMessage());
        // Retry feedback API call
        JSONObject quizResultsJson = prepareQuizResultsJson();
        FeedbackAPICaller.getFeedback(quizResultsJson.toString(), this);
    }
}
