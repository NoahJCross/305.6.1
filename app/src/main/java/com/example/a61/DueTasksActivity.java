package com.example.a61;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DueTasksActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView taskAmountTextView;
    private RecyclerView dueTaskRecyclerView;
    private TaskDbHandler taskDbHandler;
    private UserDbHandler userDbHandler;
    private List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_tasks);
        long userId = User.getInstance().getId(); // Get current user ID

        // Initialize views
        usernameTextView = findViewById(R.id.usernameTextView);
        taskAmountTextView = findViewById(R.id.taskAmountTextView);
        dueTaskRecyclerView = findViewById(R.id.dueTaskRecyclerView);

        // Initialize database handlers
        taskDbHandler = new TaskDbHandler(DueTasksActivity.this);
        userDbHandler = new UserDbHandler(DueTasksActivity.this);

        // Retrieve tasks for the current user
        tasks = taskDbHandler.getTasksByUserId(userId);

        // Set username text
        String username = User.getInstance().getUsername();
        if (username != null && !username.isEmpty()) {
            username = username.substring(0, 1).toUpperCase() + username.substring(1);
        }
        usernameTextView.setText(username);

        // Set task amount text
        taskAmountTextView.setText("You have " + tasks.size() + (tasks.size() != 1 ? " tasks" : " task") + " due");

        // Set up RecyclerView with DueTaskViewAdapter
        DueTaskViewAdapter adapter = new DueTaskViewAdapter(tasks, this);
        dueTaskRecyclerView.setAdapter(adapter);
        dueTaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
