package com.example.laozhong.bigimagescaleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by lao.zhong on 2018/3/8.
 */

public class PhotoListAdapter extends RecyclerView.Adapter {
    private List<Image> mImageList;
    private Context mContext;
    private OnPhotoListener mItemListener ;


    public PhotoListAdapter(List<Image> mImageList, Context context) {
        this.mImageList = mImageList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onPhotoClick(position);
            }
        });
        ImageView imageView = ((ViewHolder) holder).imageView;
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = mImageList.get(position).getHeight();
        Glide.with(mContext).load(mImageList.get(position).getUrl()).into(imageView);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public static ImageView getImage(RecyclerView.ViewHolder holder) {
        if (holder instanceof ViewHolder) {
            return ((ViewHolder) holder).imageView;
        } else {
            return null;
        }

    }


    public void setmItemListener(OnPhotoListener mItemListener) {
        this.mItemListener = mItemListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }


    }

    public interface OnPhotoListener {
        void onPhotoClick(int position);
    }
}
