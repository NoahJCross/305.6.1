package com.example.a61;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class QuestionFragment extends Fragment {

    private static final String ARG_QUESTIONS = "questions";
    private static final String ARG_POSITION = "position";

    private List<Question> questions;
    private int currentPosition;
    private TextView questionTitleTextView;
    private TextView questionTextView;
    private CheckBox answer1CheckBox;
    private CheckBox answer2CheckBox;
    private CheckBox answer3CheckBox;
    private Button nextButton;
    private Button prevButton;

    public QuestionFragment() {
    }

    // Method to create a new instance of QuestionFragment
    public static QuestionFragment newInstance(List<Question> questions, int position) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_QUESTIONS, new ArrayList<>(questions));
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questions = getArguments().getParcelableArrayList(ARG_QUESTIONS);
            currentPosition = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_fragment, container, false);
        if (questions != null) {
            // Initialize UI elements
            questionTitleTextView = view.findViewById(R.id.questionTitleTextView);
            questionTextView = view.findViewById(R.id.questionTextView);
            answer1CheckBox = view.findViewById(R.id.answer1CheckBox);
            answer2CheckBox = view.findViewById(R.id.answer2CheckBox);
            answer3CheckBox = view.findViewById(R.id.answer3CheckBox);
            nextButton = view.findViewById(R.id.nextButton);
            prevButton = view.findViewById(R.id.prevButton);

            // Set question and answer options
            Question currentQuestion = questions.get(currentPosition);
            questionTitleTextView.setText("Question " + (currentPosition + 1) + ".");
            questionTextView.setText(currentQuestion.getQuestion());
            answer1CheckBox.setText(currentQuestion.getAnswer1());
            answer2CheckBox.setText(currentQuestion.getAnswer2());
            answer3CheckBox.setText(currentQuestion.getAnswer3());

            // Button click listeners
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TaskActivity) requireActivity()).NextQuestion();
                }
            });

            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TaskActivity) requireActivity()).PrevQuestion();
                }
            });

            // Update answer selection when checkboxes are checked/unchecked
            answer1CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateSelection());
            answer2CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateSelection());
            answer3CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateSelection());
        }
        return view;
    }

    // Method to update answer selection
    private void updateSelection() {
        if (answer1CheckBox.isChecked()) {
            ((TaskActivity) requireActivity()).UpdateAnswer(1);
        } else if (answer2CheckBox.isChecked()) {
            ((TaskActivity) requireActivity()).UpdateAnswer(2);
        } else if (answer3CheckBox.isChecked()) {
            ((TaskActivity) requireActivity()).UpdateAnswer(3);
        }
        // Show submit button if it's the last question
        if (currentPosition == questions.size() - 1) {
            View rootView = requireActivity().findViewById(android.R.id.content);
            Button submitButton = rootView.findViewById(R.id.submitButton);
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    // Method to update visibility of next and previous buttons
    private void updateButtonVisibility() {
        if (currentPosition == 0) {
            prevButton.setVisibility(View.GONE);
        } else {
            prevButton.setVisibility(View.VISIBLE);
        }

        if (currentPosition == questions.size() - 1) {
            nextButton.setVisibility(View.GONE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
        }
    }
}
