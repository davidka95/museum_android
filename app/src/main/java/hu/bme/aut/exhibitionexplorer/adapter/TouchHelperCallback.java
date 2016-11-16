package hu.bme.aut.exhibitionexplorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import hu.bme.aut.exhibitionexplorer.interfaces.FavoriteTouchHelperAdapter;

/**
 * Created by Zay on 2016.11.14..
 */

public class TouchHelperCallback extends ItemTouchHelper.Callback {
    private final FavoriteTouchHelperAdapter mAdapter;
    private final RecyclerView recyclerView;

    public TouchHelperCallback(FavoriteTouchHelperAdapter mAdapter, RecyclerView recyclerView) {
        this.mAdapter = mAdapter;
        this.recyclerView = recyclerView;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        int swipeFlags = ItemTouchHelper.START;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition(), recyclerView);
    }
}
