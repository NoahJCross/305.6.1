package com.example.a61;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Result implements Parcelable {
    private String question; // Stores the question
    private String feedback; // Stores the feedback

    // Empty constructor
    public Result() {
    }

    // Constructor with parameters
    public Result(String question, String feedback) {
        this.question = question;
        this.feedback = feedback;
    }

    // Parcelable constructor
    protected Result(Parcel in) {
        question = in.readString(); // Read question from parcel
        feedback = in.readString(); // Read feedback from parcel
    }

    // Creator for Parcelable
    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    // Write object values to parcel
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(question); // Write question to parcel
        dest.writeString(feedback); // Write feedback to parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // getters and setters
    public String getTitle() {
        return question;
    }

    public void setTitle(String question) {
        this.question = question;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
