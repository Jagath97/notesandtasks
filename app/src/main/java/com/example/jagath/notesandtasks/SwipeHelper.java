package com.example.jagath.notesandtasks;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by jagath on 18/03/2018.
 */

public class SwipeHelper extends ItemTouchHelper.SimpleCallback {
    Context context;
    public SwipeHelper(NotesRecyclerAdapter adapter, Context context) {
        super(ItemTouchHelper.LEFT,ItemTouchHelper.RIGHT);
        this.context=context;
        this.adapter = adapter;
    }

    NotesRecyclerAdapter adapter;
    public SwipeHelper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.dismmiss(viewHolder.getAdapterPosition(),context);
    }
}
