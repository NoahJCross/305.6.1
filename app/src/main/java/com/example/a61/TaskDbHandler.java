package com.example.a61;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDbHandler extends SQLiteOpenHelper {
    // Database version
    private static final int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME = "taskdb";
    // Table name
    private static final String TABLE_NAME = "tasks";
    // Column names
    private static final String ID_COL = "id";
    private static final String USER_ID_COL = "userId";
    private static final String TITLE_COL = "title";
    private static final String DESCRIPTION_COL = "description";

    // Constructor
    public TaskDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create tasks table
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USER_ID_COL + " INTEGER NOT NULL,"
                + TITLE_COL + " TEXT NOT NULL,"
                + DESCRIPTION_COL + " TEXT NOT NULL,"
                + " FOREIGN KEY (" + USER_ID_COL + ") REFERENCES users(" + ID_COL + ")"
                + ")";
        // Execute SQL statement
        db.execSQL(CREATE_TASKS_TABLE);
    }

    // Adding new task
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Prepare ContentValues to insert into database
        ContentValues values = new ContentValues();
        values.put(USER_ID_COL, task.getUserId());
        values.put(TITLE_COL, task.getTitle());
        values.put(DESCRIPTION_COL, task.getDescription());

        // Insert row
        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return newRowId;
    }

    // Getting all tasks by user ID
    public List<Task> getTasksByUserId(long userId) {
        List<Task> taskList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        // Query to get tasks by user ID
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USER_ID_COL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        // Loop through cursor and add tasks to list
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL)));
                task.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(USER_ID_COL)));
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE_COL)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_COL)));
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return taskList;
    }

    // Getting task by ID
    public Task getTaskById(long taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Task task = null;

        // Query to get task by ID
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(taskId)});

        // If cursor has data, set task object
        if (cursor.moveToFirst()) {
            task = new Task();
            task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL)));
            task.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(USER_ID_COL)));
            task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE_COL)));
            task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_COL)));
        }

        cursor.close();
        db.close();
        return task;
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed and create tables again
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
