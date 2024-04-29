package com.example.a61;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DueTaskViewAdapter extends RecyclerView.Adapter<DueTaskViewAdapter.ViewHolder> {

    private List<Task> tasks;
    private Context context;

    // Constructor
    public DueTaskViewAdapter(List<Task> tasks, Context context){
        this.tasks = tasks;
        this.context = context;
    }

    // Create new views
    @NonNull
    @Override
    public DueTaskViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        View itemView = LayoutInflater.from(context).inflate(R.layout.task_card, parent, false);
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(@NonNull DueTaskViewAdapter.ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        holder.taskTitle.setText(tasks.get(position).getTitle());
        holder.taskDescription.setText(tasks.get(position).getDescription());
    }

    // Return the size of your dataset
    @Override
    public int getItemCount() { return tasks.size(); }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView taskTitle;
        private TextView taskDescription;
        private Button startButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            startButton = itemView.findViewById(R.id.startButton);

            // Set click listener for startButton
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the position of the item clicked
                    int position = getAdapterPosition();
                    // Get the task associated with the clicked item
                    Task clickedTask = tasks.get(position);
                    // Create an intent to start TaskActivity and pass the task ID as extra data
                    Intent intent = new Intent(context, TaskActivity.class);
                    intent.putExtra("task_id", clickedTask.getId());
                    // Start TaskActivity
                    context.startActivity(intent);
                }
            });
        }
    }
}
