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
