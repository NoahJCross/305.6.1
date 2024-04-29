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
