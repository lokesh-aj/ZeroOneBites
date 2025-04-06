package com.example.grabit.Helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grabit.Adapter.HistoryAdapter;
import com.example.grabit.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private HistoryAdapter mAdapter;
    private Drawable deleteIcon;
    private Drawable shareIcon;
    private final ColorDrawable background;
    private Context context;

    public SwipeToDeleteCallback(HistoryAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        this.context = context;
        background = new ColorDrawable(Color.RED);
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        shareIcon = ContextCompat.getDrawable(context, R.drawable.ic_share);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            mAdapter.deleteItem(position);
        } else if (direction == ItemTouchHelper.RIGHT) {
            mAdapter.shareItem(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                           @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                           int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right (Share)
            background.setColor(Color.parseColor("#5F6EF2"));
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());

            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = iconLeft + shareIcon.getIntrinsicWidth();
            shareIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        } else if (dX < 0) { // Swiping to the left (Delete)
            background.setColor(Color.RED);
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());

            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        } else {
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        if (dX > 0) {
            shareIcon.draw(c);
        } else if (dX < 0) {
            deleteIcon.draw(c);
        }
    }
} 