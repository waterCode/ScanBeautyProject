package com.example.laozhong.bigimagescaleview.Adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.laozhong.bigimagescaleview.R;
import com.example.laozhong.bigimagescaleview.common.Views;

/**
 * Created by lao.zhong on 2018/3/12.
 */

public abstract class StaggeredEndlessRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends EndlessRecyclerAdapter<RecyclerView.ViewHolder> {


    private static final int LOADING_VIEW_TYPE = Integer.MAX_VALUE;


    @Override
    public int getItemViewType(int position) {
        if (position == getCount()) {
            return LOADING_VIEW_TYPE;//footView
        }else {
            return getItemType();
        }
    }
    /**
     *
     * @return 需要重写的itemType
     */
    public int getItemType() {
        return 0;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOADING_VIEW_TYPE) {
            return new LoadingFootViewHolder(parent);
        } else {
            return onCreateHolder(parent,viewType);
        }
    }

    protected abstract VH onCreateHolder(ViewGroup parent, int viewType);


    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LoadingFootViewHolder){
            //应该设置占据一整格
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams){
                Log.d(StaggeredEndlessRecyclerAdapter.class.getName(),"设置占据一整格");
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            }
        }else {
            onBindHolder(((VH)holder),position);
        }
    }

    /**
     * 代替系统的onBindViewHolder
     * @param holder
     * @param position
     */
    protected abstract void onBindHolder(VH holder, int position);

    @Override
    public final int getItemCount() {
        return getCount() + (isLoading() ? 1 : 0);
    }


    /**
     * 实际的数据量
     *
     * @return 数据量大小
     */
    public abstract int getCount();


    @Override
    protected void onLoadingStateChanged() {
        super.onLoadingStateChanged();
        //状态改变
        //如果当前状态为正在加载
        if(isLoading()){
            //添加loadingView
            notifyItemInserted(getCount());
        }else {
            notifyItemRemoved(getCount());//删除loadingView
        }
    }

    static class LoadingFootViewHolder extends RecyclerView.ViewHolder {
        TextView textView;//

        public LoadingFootViewHolder(ViewGroup parent) {
            super(Views.inflate(parent, R.layout.load_more_view));
            textView = itemView.findViewById(R.id.loading_text);
        }
    }
}
