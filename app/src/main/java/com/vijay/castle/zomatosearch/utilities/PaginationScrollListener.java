package com.vijay.castle.zomatosearch.utilities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by vijay on 6/9/17.
 */

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    public abstract void loadMoreItems();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

    protected abstract boolean isFavouritePage();

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int firstVisibleItemPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        if (!isLoading() && !isLastPage() && !isFavouritePage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4
                    && firstVisibleItemPosition >= 0) {
                loadMoreItems();
            }
        }
    }
}
