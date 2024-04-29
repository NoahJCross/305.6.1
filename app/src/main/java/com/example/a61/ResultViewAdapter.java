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
