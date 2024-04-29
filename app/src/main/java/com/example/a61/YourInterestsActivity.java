package com.example.a61;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class YourInterestsActivity extends AppCompatActivity implements InterestsViewAdapter.clickListener {
    private RecyclerView interestsRecyclerView;
    private Button interestsNextButton;
    private FrameLayout interestsLoaderFrame;
    private List<String> selectedInterests = new ArrayList<>();
    private InterestsArray interestsArray = new InterestsArray();
    private int successfulFetchCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_interests);

        List<String> interests = interestsArray.getInterests();
        interestsLoaderFrame = findViewById(R.id.interestsLoaderFrame);

        interestsRecyclerView = findViewById(R.id.interestsRecyclerView);
        InterestsViewAdapter adapter = new InterestsViewAdapter(interests, this, this);
        interestsRecyclerView.setAdapter(adapter);
        interestsRecyclerView.setLayoutManager(new CustomLayoutManager(this));

        interestsNextButton = findViewById(R.id.interestsNextButton);
        interestsNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if at least one interest is selected
                if (selectedInterests.isEmpty()) {
                    Toast.makeText(YourInterestsActivity.this, "Please select at least one interest", Toast.LENGTH_SHORT).show();
                    return;
                }
                interestsLoaderFrame.setVisibility(View.VISIBLE);
                // Fetch quiz for each selected interest
                for (String interest : selectedInterests) {
                    fetchQuizForInterest(interest);
                }
            }
        });
    }

    // Handle click events on interests
    @Override
    public void onClick(int position, boolean isSelected) {
        String interest = interestsArray.getInterests().get(position);
        if (isSelected) {
            selectedInterests.add(interest);
        } else {
            selectedInterests.remove(interest);
        }
    }

    // Fetch quiz questions for a particular interest
    private void fetchQuizForInterest(String interest) {
        QuizAPICaller.getQuiz(interest, new QuizAPICaller.QuizResponseListener() {
            @Override
            public void onQuizReceived(List<Question> questions) {
                try {
                    successfulFetchCount++;
                    // If all selected interests have been fetched, start the next activity
                    if (successfulFetchCount == selectedInterests.size()) {
                        startNextActivity();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onQuizError(Exception e) {
                // If there's an error fetching quiz, try again
                Log.d("API", e.getMessage());
                fetchQuizForInterest(interest);
            }
        }, this);
    }

    // Start the next activity after fetching quiz questions for all selected interests
    private void startNextActivity() {
        interestsLoaderFrame.setVisibility(View.GONE);
        Intent intent = new Intent(YourInterestsActivity.this, DueTasksActivity.class);
        startActivity(intent);
        finish();
    }
}
