package com.example.a61;

import java.util.List;

// Class representing the response from the feedback API
public class FeedbackResponse {
    private List<Result> feedback; // List of feedback results

    // Getter method for the feedback results
    public List<Result> getFeedback(){
        return feedback;
    }
}