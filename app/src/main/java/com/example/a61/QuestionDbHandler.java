package com.example.a61;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class QuestionDbHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "questiondb";
    private static final String TABLE_NAME = "questions";
    private static final String ID_COL = "id";
    private static final String TASK_ID_COL = "taskId";
    private static final String QUESTION_COL = "question";
    private static final String ANSWER_1_COL = "answer1";
    private static final String ANSWER_2_COL = "answer2";
    private static final String ANSWER_3_COL = "answer3";
    private static final String CORRECT_ANSWER_COL = "correctAnswer";
    private static final String USERS_ANSWER_COL = "usersAnswer";
    private static final String FEEDBACK_COL = "feedback";

    // Constructor
    public QuestionDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the database table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TASK_ID_COL + " INTEGER NOT NULL,"
                + QUESTION_COL + " TEXT NOT NULL,"
                + ANSWER_1_COL + " TEXT NOT NULL,"
                + ANSWER_2_COL + " TEXT NOT NULL,"
                + ANSWER_3_COL + " TEXT NOT NULL,"
                + FEEDBACK_COL + " TEXT NOT NULL,"
                + CORRECT_ANSWER_COL + " INTEGER NOT NULL,"
                + USERS_ANSWER_COL + " INTEGER DEFAULT NULL,"
                + " FOREIGN KEY (" + TASK_ID_COL + ") REFERENCES tasks(" + ID_COL + ")"
                + ")";
        db.execSQL(CREATE_QUESTIONS_TABLE);
    }

    // Add a question to the database
    public void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_ID_COL, question.getTaskId());
        values.put(QUESTION_COL, question.getQuestion());
        values.put(ANSWER_1_COL, question.getAnswer1());
        values.put(ANSWER_2_COL, question.getAnswer2());
        values.put(ANSWER_3_COL, question.getAnswer3());
        values.put(CORRECT_ANSWER_COL, question.getCorrectAnswer());
        values.put(FEEDBACK_COL, question.getFeedback());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Get questions by task ID from the database
    public List<Question> getQuestionsByTaskId(long taskId) {
        List<Question> questionList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + TASK_ID_COL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(taskId)});

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL)));
                question.setTaskId(cursor.getLong(cursor.getColumnIndexOrThrow(TASK_ID_COL)));
                question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow(QUESTION_COL)));
                question.setAnswer1(cursor.getString(cursor.getColumnIndexOrThrow(ANSWER_1_COL)));
                question.setAnswer2(cursor.getString(cursor.getColumnIndexOrThrow(ANSWER_2_COL)));
                question.setAnswer3(cursor.getString(cursor.getColumnIndexOrThrow(ANSWER_3_COL)));
                question.setFeedback(cursor.getString(cursor.getColumnIndexOrThrow(FEEDBACK_COL)));
                question.setCorrectAnswer(cursor.getInt(cursor.getColumnIndexOrThrow(CORRECT_ANSWER_COL)));
                int usersAnswerIndex = cursor.getColumnIndexOrThrow(USERS_ANSWER_COL);
                if (!cursor.isNull(usersAnswerIndex)) {
                    question.setUsersAnswer(cursor.getInt(usersAnswerIndex));
                }
                questionList.add(question);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return questionList;
    }

    // Update user's answer for a question
    public void updateUserAnswer(long questionId, int userAnswer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USERS_ANSWER_COL, userAnswer);

        db.update(TABLE_NAME, values, ID_COL + " = ?", new String[]{String.valueOf(questionId)});
        db.close();
    }

    // Update feedback for a question
    public void updateQuestionFeedback(long questionId, String feedback) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FEEDBACK_COL, feedback);

        db.update(TABLE_NAME, values, ID_COL + " = ?", new String[]{String.valueOf(questionId)});
        db.close();
    }

    // Drop and recreate the table when upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
