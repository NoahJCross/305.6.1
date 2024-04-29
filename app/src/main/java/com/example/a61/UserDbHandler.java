package com.example.a61;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDbHandler extends SQLiteOpenHelper {

    // Database information
    private static final String DB_NAME = "userdb";
    private static final int DB_VERSION = 1;

    // Table information
    private static final String TABLE_NAME = "users";
    private static final String ID_COL = "id";
    private static final String USERNAME_COL = "username";
    private static final String EMAIL_COL = "email";
    private static final String PASSWORD_COL = "password";
    private static final String PHONE_NUMBER_COL = "phonenumber";

    // Constructor
    public UserDbHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL query to create users table
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_COL + " TEXT NOT NULL,"
                + EMAIL_COL + " TEXT NOT NULL,"
                + PASSWORD_COL + " TEXT NOT NULL,"
                + PHONE_NUMBER_COL + " TEXT NOT NULL)";
        // Execute SQL query
        db.execSQL(query);
    }

    // Adding a new user to the database
    public long addNewUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Prepare ContentValues to insert into database
        ContentValues values = new ContentValues();
        values.put(USERNAME_COL, user.getUsername());
        values.put(EMAIL_COL, user.getEmail());
        values.put(PASSWORD_COL, user.getPassword());
        values.put(PHONE_NUMBER_COL, user.getPhoneNumber());

        // Insert row
        long id = db.insert(TABLE_NAME, null, values);

        // Close the database connection
        db.close();

        return id;
    }

    // Finding a user by ID
    public User findUserById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get user by ID
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        User user = null;
        // If cursor has data, set user object
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(USERNAME_COL)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_COL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD_COL)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(PHONE_NUMBER_COL)));
        }

        // Close cursor and database connection
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return user;
    }

    // Validating user credentials
    public long validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Query to validate user credentials
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME_COL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        long userId = -1;

        // If cursor has data and password matches, set userId
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD_COL));
            if (password.equals(storedPassword)) {
                userId = cursor.getLong(cursor.getColumnIndexOrThrow(ID_COL));
            }
        }

        // Close cursor and database connection
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return userId;
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed and create tables again
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
