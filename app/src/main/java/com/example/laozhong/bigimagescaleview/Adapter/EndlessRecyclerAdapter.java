package com.example.laozhong.bigimagescaleview.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public abstract class EndlessRecyclerAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private final RecyclerView.OnScrollListener scrollListener =
            new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    loadNextItemsIfNeeded(recyclerView);
                }
            };

    private boolean isLoading = false;//是否正在加载

    private LoaderCallbacks callbacks;//加载回调
    private int loadingOffset = 0;//偏移量

    final boolean isLoading() {
        return isLoading;//是否正在加载
    }


    public void setCallbacks(LoaderCallbacks callbacks) {
        this.callbacks = callbacks;
        //loadNextItems();
    }

    public void setLoadingOffset(int loadingOffset) {
        this.loadingOffset = loadingOffset;
    }

    /**
     * 加载下一个页
     */
    private void loadNextItems() {
        if (!isLoading && callbacks != null && callbacks.canLoadNextItems()) {
            isLoading = true;
            onLoadingStateChanged();
            callbacks.loadNextItems();
        }
    }


    public void onNextItemsLoaded() {
        if (isLoading) {
            isLoading = false;
            onLoadingStateChanged();
        }
    }



    protected void onLoadingStateChanged() {
        // No-default-op
    }

    /**
     * 判断是否需要加载更多
     *
     * @param recyclerView
     */
    private void loadNextItemsIfNeeded(RecyclerView recyclerView) {
        if (!isLoading) {
            View lastVisibleChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
            int lastVisiblePos = recyclerView.getChildAdapterPosition(lastVisibleChild);//拿到最后的
            int total = getItemCount();

            Log.d("loadNextItemsIfNeeded", "data: lastVisiblePos:" + lastVisiblePos + "  total: " + total);
            if ((lastVisiblePos >= total - loadingOffset - 1) && lastVisiblePos != -1) {
                Log.d("loadNextItemsIfNeeded", "loadNextItem");
                recyclerView.post(this::loadNextItems);
            }

        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(scrollListener);
        loadNextItemsIfNeeded(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.removeOnScrollListener(scrollListener);
    }


    public interface LoaderCallbacks {
        boolean canLoadNextItems();

        void loadNextItems();
    }

}
