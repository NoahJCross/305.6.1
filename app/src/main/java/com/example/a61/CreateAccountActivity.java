package com.example.a61;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {

    private TextView createUsername;
    private TextView createEmail;
    private TextView confirmCreateEmail;
    private TextView createPassword;
    private TextView confirmCreatePassword;
    private TextView createPhoneNumber;
    private Button createButton;
    private UserDbHandler userDbHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        // Initialize views
        createUsername = findViewById(R.id.createUsername);
        createEmail = findViewById(R.id.createEmail);
        confirmCreateEmail = findViewById(R.id.confirmCreateEmail);
        createPassword = findViewById(R.id.createPassword);
        confirmCreatePassword = findViewById(R.id.confirmCreatePassword);
        createPhoneNumber = findViewById(R.id.createPhoneNumber);
        createButton = findViewById(R.id.createButton);

        // Create Button Click Listener
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input fields
                if (validateInput()) {
                    // Get input values
                    String username = createUsername.getText().toString();
                    String email = createEmail.getText().toString();
                    String password = createPassword.getText().toString();
                    String phoneNumber = createPhoneNumber.getText().toString();

                    // Create User instance
                    User user = User.getInstance(username, email, password, phoneNumber);

                    // Add user to database
                    userDbHandler = new UserDbHandler(CreateAccountActivity.this);
                    long userId = userDbHandler.addNewUser(user);
                    user.setId(userId);

                    // Show success message
                    Toast.makeText(CreateAccountActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                    // Move to next activity
                    Intent intent = new Intent(CreateAccountActivity.this, YourInterestsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    // Method to validate input fields
    private boolean validateInput() {
        String username = createUsername.getText().toString();
        String email = createEmail.getText().toString();
        String confirmEmail = confirmCreateEmail.getText().toString();
        String password = createPassword.getText().toString();
        String confirmPassword = confirmCreatePassword.getText().toString();
        String phoneNumber = createPhoneNumber.getText().toString();

        if (username.isEmpty()) {
            createUsername.setError("Username is required");
            createUsername.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            createEmail.setError("Email is required");
            createEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            createEmail.setError("Enter a valid email address");
            createEmail.requestFocus();
            return false;
        }

        if (!email.equals(confirmEmail)) {
            confirmCreateEmail.setError("Emails do not match");
            confirmCreateEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            createPassword.setError("Password is required");
            createPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            createPassword.setError("Password must be at least 6 characters");
            createPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmCreatePassword.setError("Passwords do not match");
            confirmCreatePassword.requestFocus();
            return false;
        }

        if (phoneNumber.isEmpty()) {
            createPhoneNumber.setError("Phone number is required");
            createPhoneNumber.requestFocus();
            return false;
        }

        return true;
    }
}
