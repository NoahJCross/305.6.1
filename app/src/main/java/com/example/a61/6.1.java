// MAIN ACTIVITY
package com.example.a61;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView loginUsername;
    private TextView loginPassword;
    private Button loginButton;
    private TextView createAccountLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        createAccountLink = findViewById(R.id.createAccountLink);

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get entered username and password
                String enteredUsername = loginUsername.getText().toString().trim();
                String enteredPassword = loginPassword.getText().toString().trim();

                // Check if username or password is empty
                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate user credentials
                UserDbHandler dbHandler = new UserDbHandler(MainActivity.this);
                long userId = dbHandler.validateUser(enteredUsername, enteredPassword);
                User user = dbHandler.findUserById(userId);
                // Set user ID and username in User singleton
                User.getInstance().setId(userId);
                User.getInstance().setUsername(user.getUsername());

                // If user is validated, start DueTasksActivity
                if (userId != -1) {
                    Intent intent = new Intent(MainActivity.this, DueTasksActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Create account link click listener
        createAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start CreateAccountActivity
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}


// CREATE ACCOUNT ACTIVITY
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


// CUSTOM LAYOUT MANAGER
package com.example.a61;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CustomLayoutManager extends RecyclerView.LayoutManager {
    private Context context;

    public CustomLayoutManager(Context context) {
        this.context = context;
    }

    // Allow vertical scrolling
    @Override
    public boolean canScrollVertically() {
        return true;
    }

    // Generate default layout parameters
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    // Layout children views
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);
        int previousWidth = 0;
        int margin = Math.round(10 * context.getResources().getDisplayMetrics().density);
        for (int i = 0; i < getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);

            measureChildWithMargins(view, 0, 0);

            int parentWidth = getWidth();
            int width;
            // Alternating between two widths
            if (i % 4 == 3 || i % 4 == 0) {
                width = (int) (parentWidth * 0.40);
            } else {
                width = (int) (parentWidth * 0.60);
            }
            width = width - margin / 2;
            int height = getDecoratedMeasuredHeight(view);

            int left = (i % 2 == 0) ? 0 : previousWidth + margin;
            int top = i / 2 * height + margin;
            int right = left + width;
            int bottom = top + height;
            layoutDecorated(view, left, top, right, bottom);
            previousWidth = width;
        }

    }

    // Scroll vertically
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0 || dy == 0) {
            return 0;
        }

        int scrolled = dy;
        int paddingTop = getPaddingTop();
        int paddingBottom = getHeight() - getPaddingBottom();
        int topOffset = getDecoratedTop(getChildAt(0));
        int bottomOffset = getDecoratedBottom(getChildAt(getChildCount() - 1));

        // Scroll up
        if (dy < 0) {
            if (topOffset - dy > paddingTop) {
                scrolled = topOffset - paddingTop;
            }
        }
        // Scroll down
        else {
            if (bottomOffset - dy < paddingBottom) {
                scrolled = bottomOffset - paddingBottom;
            }
        }

        if (scrolled != dy) {
            return scrolled;
        }

        // Offset children vertically
        offsetChildrenVertical(-scrolled);

        return scrolled;
    }

}

// DUE TASKS ACTIVITY
package com.example.a61;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DueTasksActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView taskAmountTextView;
    private RecyclerView dueTaskRecyclerView;
    private TaskDbHandler taskDbHandler;
    private UserDbHandler userDbHandler;
    private List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_tasks);
        long userId = User.getInstance().getId(); // Get current user ID

        // Initialize views
        usernameTextView = findViewById(R.id.usernameTextView);
        taskAmountTextView = findViewById(R.id.taskAmountTextView);
        dueTaskRecyclerView = findViewById(R.id.dueTaskRecyclerView);

        // Initialize database handlers
        taskDbHandler = new TaskDbHandler(DueTasksActivity.this);
        userDbHandler = new UserDbHandler(DueTasksActivity.this);

        // Retrieve tasks for the current user
        tasks = taskDbHandler.getTasksByUserId(userId);

        // Set username text
        String username = User.getInstance().getUsername();
        if (username != null && !username.isEmpty()) {
            username = username.substring(0, 1).toUpperCase() + username.substring(1);
        }
        usernameTextView.setText(username);

        // Set task amount text
        taskAmountTextView.setText("You have " + tasks.size() + (tasks.size() != 1 ? " tasks" : " task") + " due");

        // Set up RecyclerView with DueTaskViewAdapter
        DueTaskViewAdapter adapter = new DueTaskViewAdapter(tasks, this);
        dueTaskRecyclerView.setAdapter(adapter);
        dueTaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}


// FEEDBACK API CALLER
package com.example.a61;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class FeedbackAPICaller {

    // Interface to listen for feedback response or errors
    public interface FeedbackResponseListener {
        void onFeedbackReceived(List<Result> results);
        void onFeedbackError(Exception e);
    }

    // Method to get feedback from the API
    public static void getFeedback(String quizResultsJson, FeedbackResponseListener listener) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Create OkHttpClient with custom timeout settings
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(300, TimeUnit.SECONDS)
                        .readTimeout(300, TimeUnit.SECONDS)
                        .build();

                // Create Retrofit instance
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.0.3:5000/") // Base URL of the API
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();

                // Create Retrofit service interface for making requests
                FeedbackRequest request = retrofit.create(FeedbackRequest.class);

                // Create request body with quiz results in JSON format
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), quizResultsJson);

                // Make API call to get feedback
                Call<FeedbackResponse> call = request.getFeedback(requestBody);
                call.enqueue(new Callback<FeedbackResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FeedbackResponse> call, @NonNull Response<FeedbackResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Parse feedback response
                            List<Result> feedbackResults = parseFeedbackResponse(response.body());
                            // Notify listener with feedback results
                            listener.onFeedbackReceived(feedbackResults);
                        } else {
                            // Notify listener of error when response is not successful
                            listener.onFeedbackError(new Exception("Failed to get feedback. Response code: " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FeedbackResponse> call, @NonNull Throwable t) {
                        // Notify listener of error when API call fails
                        listener.onFeedbackError(new Exception("Failed to get feedback. Error: " + t.getMessage()));
                    }
                });
            } catch (Exception e) {
                // Log error and notify listener of error on the main thread
                Log.e("API", "Error: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> listener.onFeedbackError(e));
            }
        });
    }

    // Method to parse feedback response
    private static List<Result> parseFeedbackResponse(FeedbackResponse feedbackResponse) {
        List<Result> feedbackResults = new ArrayList<>();
        try {
            List<Result> feedbackList = feedbackResponse.getFeedback();

            for (Result feedback : feedbackList) {
                // Extract question text and feedback text from each feedback item
                String questionText = feedback.getTitle();
                String feedbackText = feedback.getFeedback();
                // Create a Result object with question text and feedback text, and add it to the list
                Result feedbackResult = new Result(questionText, feedbackText);
                feedbackResults.add(feedbackResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feedbackResults;
    }

    // Retrofit service interface for making feedback request
    public interface FeedbackRequest {
        @POST("getFeedback") // Endpoint for getting feedback
        Call<FeedbackResponse> getFeedback(@Body RequestBody quizResultsJson); // POST method with request body
    }
}

// FEEDBACK RESPONSE
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


// INTERESTS ARRAY
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


// INTERESTS VIEW ADAPTER
package com.example.a61;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InterestsViewAdapter extends RecyclerView.Adapter<InterestsViewAdapter.ViewHolder> {

    private List<String> interests;
    private Context context;
    private clickListener listener;
    private boolean[] selectedItems;

    // Constructor
    public InterestsViewAdapter(List<String> interests, Context context, clickListener listener){
        this.interests = interests;
        this.context = context;
        this.listener = listener;
        this.selectedItems = new boolean[interests.size()]; // Initialize selectedItems array
    }

    // Interface for click events
    public interface clickListener{
        void onClick(int position, boolean isSelected);
    }

    // Create new views
    @NonNull
    @Override
    public InterestsViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        View itemView = LayoutInflater.from(context).inflate(R.layout.brick_design, parent, false);
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(@NonNull InterestsViewAdapter.ViewHolder holder, int position) {
        // Set interest text
        holder.interestTextView.setText(interests.get(position));
        // Calculate item width based on position
        int recyclerViewWidth = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels;
        int itemWidth;
        int mod = position / 2 % 2 + 2;
        if (position % 4 == 3 || position % 4 == 0) {
            itemWidth = (int) (recyclerViewWidth * 0.32);
        } else {
            itemWidth = (int) (recyclerViewWidth * 0.485);
        }
        // Set item width
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = itemWidth;
        holder.itemView.setLayoutParams(layoutParams);
    }

    // Return the size of your dataset
    @Override
    public int getItemCount() { return interests.size(); }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView interestTextView;
        private clickListener clickListener;
        int currentBackgroundResource = R.drawable.dark_pink_background; // Resource ID for background

        // Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            interestTextView = itemView.findViewById(R.id.interestTextView);
            this.clickListener = listener;
            itemView.setOnClickListener(this);
        }

        // Handle click events
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // Get position of the clicked item
            selectedItems[position] = !selectedItems[position]; // Toggle selected state
            v.setSelected(selectedItems[position]); // Set selected state of the view
            listener.onClick(position, selectedItems[position]); // Notify listener of click event
            // Toggle background resource
            if (currentBackgroundResource == R.drawable.dark_pink_background) {
                currentBackgroundResource = R.drawable.gray_background;
            } else {
                currentBackgroundResource = R.drawable.dark_pink_background;
            }
            interestTextView.setBackgroundResource(currentBackgroundResource); // Set background resource
        }
    }
}


// QUESTION CLASS
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


// QUESTION DB HANDLER
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

// QUESTION FRAGMENT
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


// QUIZ API CALLER
package com.example.a61;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class QuizAPICaller {

    // Interface to handle quiz response callbacks
    public interface QuizResponseListener {
        void onQuizReceived(List<Question> questions);
        void onQuizError(Exception e);
    }

    // Method to fetch quiz questions from the API
    public static void getQuiz(String topic, QuizResponseListener listener, Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.3:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        QuizRequest request = retrofit.create(QuizRequest.class);

        Call<QuizResponse> call = request.getQuiz(topic);
        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuizResponse> call, @NonNull Response<QuizResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Quiz quiz = response.body().getQuiz();
                        List<Question> questions = parseQuizResponse(quiz, context);
                        listener.onQuizReceived(questions);
                    } catch (JSONException e) {
                        listener.onQuizError(e);
                    }
                } else {
                    listener.onQuizError(new Exception("Failed to get quiz. Response code: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuizResponse> call, @NonNull Throwable t) {
                listener.onQuizError(new Exception("Failed to get quiz. Error: " + t.getMessage()));
            }
        });
    }

    // Method to parse the quiz response and convert it into Question objects
    private static List<Question> parseQuizResponse(Quiz quiz, Context context) throws JSONException {
        List<Question> questions = new ArrayList<>();
        TaskDbHandler taskDbHandler;
        QuestionDbHandler questionDbHandler;

        String title = quiz.getTitle();
        String description = quiz.getDescription();
        Task task = new Task(User.getInstance().getId(), title, description);
        taskDbHandler = new TaskDbHandler(context);
        long taskId = taskDbHandler.addTask(task);
        List<QuestionData> questionsData = quiz.getQuestions();

        for (QuestionData questionData : questionsData) {
            String questionText = questionData.getQuestion();
            List<String> options = questionData.getOptions();
            String correctAnswer = questionData.getCorrectAnswer();
            Question question = new Question(taskId, questionText, options.get(0), options.get(1), options.get(2), Integer.parseInt(correctAnswer));
            questionDbHandler = new QuestionDbHandler(context);
            questionDbHandler.addQuestion(question);
            questions.add(question);
        }
        return questions;
    }

    // Retrofit interface for making API requests
    interface QuizRequest {
        @GET("getQuiz")
        Call<QuizResponse> getQuiz(@Query("topic") String topic);
    }

    // Response class for the quiz API
    public static class QuizResponse {
        private Quiz quiz;

        public Quiz getQuiz() {
            return quiz;
        }
    }

    // Model class representing a quiz
    public static class Quiz {
        private String title;
        private String description;
        private List<QuestionData> questions;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public List<QuestionData> getQuestions() {
            return questions;
        }
    }

    // Model class representing question data
    public static class QuestionData {
        private String question;
        private List<String> options;
        private String correct_answer;

        public String getQuestion() {
            return question;
        }

        public List<String> getOptions() {
            return options;
        }

        public String getCorrectAnswer() {
            return correct_answer;
        }
    }
}

// QUIZ RESPONSE
package com.example.a61;

import java.util.List;

public class QuizResponse {
    private String title;
    private String description;
    private List<Question> questions;
}

// RESULT CLASS
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


// RESULTS ACTIVITY
package com.example.a61;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private RecyclerView resultRecyclerView;
    private Button continueButton;
    private QuestionDbHandler questionDbHandler;
    private List<Result> results = new ArrayList<Result>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        // Get task ID from intent
        long taskId = getIntent().getLongExtra("task_id", 0);

        // Initialize continue button and set click listener to navigate to DueTasksActivity
        continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultsActivity.this, DueTasksActivity.class);
                startActivity(intent);
            }
        });

        // Initialize database handler for questions
        questionDbHandler = new QuestionDbHandler(ResultsActivity.this);

        // Retrieve questions related to the task from the database
        List<Question> questions = questionDbHandler.getQuestionsByTaskId(taskId);

        // Populate results list with questions and feedback
        for (Question question : questions) {
            Result result = new Result(question.getQuestion(), question.getFeedback());
            results.add(result);
        }

        // Initialize RecyclerView and set adapter for displaying results
        resultRecyclerView = findViewById(R.id.resultsRecyclerView);
        ResultViewAdapter adapter = new ResultViewAdapter(results, this);
        resultRecyclerView.setAdapter(adapter);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}


// RESULT VIEW ADAPTER
package com.example.a61;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResultViewAdapter extends RecyclerView.Adapter<ResultViewAdapter.ViewHolder> {
    private List<Result> results;
    private Context context;

    // Constructor to initialize the adapter with data and context
    public ResultViewAdapter(List<Result> results, Context context) {
        this.results = results;
        this.context = context;
    }

    // Create new views
    @NonNull
    @Override
    public ResultViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the item
        View itemView = LayoutInflater.from(context).inflate(R.layout.result_card, parent, false);
        return new ResultViewAdapter.ViewHolder(itemView);
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(@NonNull ResultViewAdapter.ViewHolder holder, int position) {
        // Get element from your dataset at this position
        // Replace the contents of the view with that element
        holder.resultTitleTextView.setText(results.get(position).getTitle());
        holder.resultFeedbackTextView.setText(results.get(position).getFeedback());
    }

    // Return the size of your dataset
    @Override
    public int getItemCount() {
        return results.size();
    }

    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView resultTitleTextView;
        public TextView resultFeedbackTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get references to the views
            resultTitleTextView = itemView.findViewById(R.id.resultTitleTextView);
            resultFeedbackTextView = itemView.findViewById(R.id.resultFeedbackTextView);
        }
    }
}


// TASK CLASS
package com.example.a61;

public class Task {
    private long id;
    private long userId;
    private String title;
    private String description;

    // Default constructor
    public Task() {
    }

    // Constructor with parameters to initialize the task
    public Task(long userId, String title, String description) {
        this.userId = userId;
        this.title = title;
        this.description = description;
    }

    // Getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

// TASK ACTIVITY
package com.example.a61;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity implements FeedbackAPICaller.FeedbackResponseListener {
    private TextView taskTitleTextView;
    private TextView taskDescriptionTextView;
    private Button submitButton;
    private FrameLayout taskLoaderFrame;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int currentQuestionAnswer = -1;
    private QuestionDbHandler questionDbHandler;
    private TaskDbHandler taskDbHandler;
    private long taskId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);

        // Retrieve task ID from intent
        Intent intent = getIntent();
        taskId = intent.getLongExtra("task_id", 0);

        // Initialize database handlers
        taskDbHandler = new TaskDbHandler(TaskActivity.this);
        taskTitleTextView = findViewById(R.id.taskTitleTextView);
        taskDescriptionTextView = findViewById(R.id.taskDescriptionTextView);
        taskLoaderFrame = findViewById(R.id.taskLoaderFrame);

        // Set task title and description
        Task task = taskDbHandler.getTaskById(taskId);
        taskTitleTextView.setText(task.getTitle());
        taskDescriptionTextView.setText(task.getDescription());

        // Initialize submit button and set click listener
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestionIndex == questions.size() - 1) {
                    // Prepare JSON data and call feedback API
                    JSONObject quizResultsJson = prepareQuizResultsJson();
                    taskLoaderFrame.setVisibility(View.VISIBLE);
                    FeedbackAPICaller.getFeedback(quizResultsJson.toString(), TaskActivity.this);
                }
            }
        });

        // Initialize database handler for questions
        questionDbHandler = new QuestionDbHandler(TaskActivity.this);
        questions = questionDbHandler.getQuestionsByTaskId(taskId);

        // Display the first question
        displayQuestion(currentQuestionIndex);
    }

    // Prepare JSON object with quiz results
    private JSONObject prepareQuizResultsJson() {
        JSONObject quizResultsJson = new JSONObject();
        try {
            JSONArray resultsArray = new JSONArray();
            for (Question question : questions) {
                JSONObject questionResult = new JSONObject();
                questionResult.put("question", question.getQuestion());
                questionResult.put("correct_answer", question.getCorrectAnswer());
                questionResult.put("users_answer", question.getUsersAnswer());
                resultsArray.put(questionResult);
            }
            quizResultsJson.put("results", resultsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return quizResultsJson;
    }

    // Move to the next question
    public void NextQuestion() {
        if (currentQuestionAnswer != -1) {
            Question question = questions.get(currentQuestionIndex);
            question.setUsersAnswer(currentQuestionAnswer);
            questionDbHandler.updateUserAnswer(question.getId(), currentQuestionAnswer);
            currentQuestionIndex++;
            currentQuestionAnswer = -1;
            displayQuestion(currentQuestionIndex);
        } else {
            Toast.makeText(this, "Please answer the current question.", Toast.LENGTH_SHORT).show();
        }
    }

    // Move to the previous question
    public void PrevQuestion() {
        if (currentQuestionIndex == questions.size() - 1) {
            submitButton.setVisibility(View.GONE);
        }
        currentQuestionIndex--;
        currentQuestionAnswer = -1;
        displayQuestion(currentQuestionIndex);
    }

    // Display a question fragment
    private void displayQuestion(int index) {
        QuestionFragment fragment = QuestionFragment.newInstance(questions, currentQuestionIndex);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment).commit();
    }

    // Update the current question's answer
    public void UpdateAnswer(int answer) {
        currentQuestionAnswer = answer;
    }

    // Callback method invoked when feedback is received
    @Override
    public void onFeedbackReceived(List<Result> results) {
        // Update question feedback in database
        for (int i = 0; i < results.size(); i++) {
            for (Question question : questions) {
                if (question.getQuestion().equals(results.get(i).getTitle())) {
                    question.setFeedback(results.get(i).getFeedback());
                    questionDbHandler.updateQuestionFeedback(question.getId(), results.get(i).getFeedback());
                    break;
                }
            }
        }
        // Start results activity
        Intent intent = new Intent(TaskActivity.this, ResultsActivity.class);
        intent.putExtra("task_id", taskId);
        startActivity(intent);
    }

    // Callback method invoked when feedback API call encounters an error
    @Override
    public void onFeedbackError(Exception e) {
        Toast.makeText(TaskActivity.this, "Connection Error, trying again.", Toast.LENGTH_SHORT).show();
        Log.d("API", e.getMessage());
        // Retry feedback API call
        JSONObject quizResultsJson = prepareQuizResultsJson();
        FeedbackAPICaller.getFeedback(quizResultsJson.toString(), this);
    }
}


// TASK DB HANDLER
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


// USER CLASS
package com.example.a61;

public class User {
    private static User instance;
    private long id;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;

    // Singleton pattern: static method to get instance
    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    // Singleton pattern: static method to get instance with parameters
    public static User getInstance(String username, String email, String password, String phoneNumber) {
        if (instance == null) {
            instance = new User();
            instance.username = username;
            instance.email = email;
            instance.password = password;
            instance.phoneNumber = phoneNumber;
        }
        return instance;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}



// USER DB HANDLER
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


// YOUR INTERESTS ACTIVITY
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
