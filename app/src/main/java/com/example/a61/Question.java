package com.example.a61;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Question implements Parcelable {
    private long id;
    private long taskId;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private int correctAnswer;
    private int usersAnswer;

    private String feedback;

    // Default constructor
    public Question() {
    }

    // Constructor with parameters
    public Question(long taskId, String question, String answer1, String answer2, String answer3, int correctAnswer) {
        this.taskId = taskId;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.correctAnswer = correctAnswer;
        this.feedback = "";
    }

    // Parcelable constructor
    protected Question(Parcel in) {
        id = in.readLong();
        taskId = in.readLong();
        question = in.readString();
        answer1 = in.readString();
        answer2 = in.readString();
        answer3 = in.readString();
        correctAnswer = in.readInt();
        if (in.dataAvail() > 0) {
            usersAnswer = in.readInt();
        }
    }

    // Parcelable CREATOR
    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    // Write object's data to the parcel
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(taskId);
        dest.writeString(question);
        dest.writeString(answer1);
        dest.writeString(answer2);
        dest.writeString(answer3);
        dest.writeInt(correctAnswer);
        if (usersAnswer != -1) {
            dest.writeInt(usersAnswer);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getUsersAnswer() {
        return usersAnswer;
    }

    public void setUsersAnswer(int usersAnswer) {
        this.usersAnswer = usersAnswer;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
