package com.example.a61;

import java.util.Arrays;
import java.util.List;

// Class representing a list of interests
public class InterestsArray {
    // List of interests
    private List<String> interests = Arrays.asList(
            "Java", "Math", "Biology", "Learning", "Python", "Physics", "Chemistry", "History",
            "Music", "Programming", "Geography", "Art", "Reading", "Cooking", "Networking", "Sports",
            "Cycling", "JavaScript", "Physics", "Robotics", "History", "Gaming", "Drawing", "Writing",
            "Basketball", "Soccer", "Economics", "Swimming", "Algebra", "Machine", "Cyber", "English",
            "Science", "Physics", "Artificial", "Computers", "Engineering", "Medicine", "Business", "French",
            "Spanish", "Fitness", "Training", "Finance", "Running", "Marketing", "Android", "Design",
            "Fashion", "Guitar"
    );

    // Getter method for the list of interests
    public List<String> getInterests() {
        return interests;
    }
}