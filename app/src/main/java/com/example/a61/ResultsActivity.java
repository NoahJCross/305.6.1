package com.example.a61;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private RecyclerView resultRecyclerView;
    private Button continueButton;
    private QuestionDbHandler questionDbHandler;
    private List<Result> results = new ArrayList<Result>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        // Get task ID from intent
        long taskId = getIntent().getLongExtra("task_id", 0);

        // Initialize continue button and set click listener to navigate to DueTasksActivity
        continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultsActivity.this, DueTasksActivity.class);
                startActivity(intent);
            }
        });

        // Initialize database handler for questions
        questionDbHandler = new QuestionDbHandler(ResultsActivity.this);

        // Retrieve questions related to the task from the database
        List<Question> questions = questionDbHandler.getQuestionsByTaskId(taskId);

        // Populate results list with questions and feedback
        for (Question question : questions) {
            Result result = new Result(question.getQuestion(), question.getFeedback());
            results.add(result);
        }

        // Initialize RecyclerView and set adapter for displaying results
        resultRecyclerView = findViewById(R.id.resultsRecyclerView);
        ResultViewAdapter adapter = new ResultViewAdapter(results, this);
        resultRecyclerView.setAdapter(adapter);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
