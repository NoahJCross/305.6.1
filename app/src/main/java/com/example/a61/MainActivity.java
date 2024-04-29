package com.example.a61;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView loginUsername;
    private TextView loginPassword;
    private Button loginButton;
    private TextView createAccountLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        createAccountLink = findViewById(R.id.createAccountLink);

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get entered username and password
                String enteredUsername = loginUsername.getText().toString().trim();
                String enteredPassword = loginPassword.getText().toString().trim();

                // Check if username or password is empty
                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate user credentials
                UserDbHandler dbHandler = new UserDbHandler(MainActivity.this);
                long userId = dbHandler.validateUser(enteredUsername, enteredPassword);
                User user = dbHandler.findUserById(userId);
                // Set user ID and username in User singleton
                User.getInstance().setId(userId);
                User.getInstance().setUsername(user.getUsername());

                // If user is validated, start DueTasksActivity
                if (userId != -1) {
                    Intent intent = new Intent(MainActivity.this, DueTasksActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Create account link click listener
        createAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CreateAccountActivity
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}
