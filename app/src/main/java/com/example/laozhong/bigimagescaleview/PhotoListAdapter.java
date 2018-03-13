package com.example.laozhong.bigimagescaleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.laozhong.bigimagescaleview.Adapter.StaggeredEndlessRecyclerAdapter;

import java.util.List;

/**
 * Created by lao.zhong on 2018/3/8.
 */

public class PhotoListAdapter extends StaggeredEndlessRecyclerAdapter<PhotoListAdapter.PhotoViewHolder> {
    private List<Image> mImageList;
    private Context mContext;
    private OnPhotoListener mItemListener ;


    public PhotoListAdapter(List<Image> mImageList, Context context) {
        this.mImageList = mImageList;
        mContext = context;
    }



    @Override
    protected PhotoViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_image_item, parent, false);
        return new PhotoViewHolder(view);
    }



    @Override
    protected void onBindHolder(PhotoViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onPhotoClick(position);
            }
        });
        ImageView imageView = ((PhotoViewHolder) holder).imageView;
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.height = mImageList.get(position).getHeight();
        Glide.with(mContext).load(mImageList.get(position).getUrl()).into(imageView);
    }




    @Override
    public int getCount() {
        return mImageList.size();
    }

    public static ImageView getImage(RecyclerView.ViewHolder holder) {
        if (holder instanceof PhotoViewHolder) {
            return ((PhotoViewHolder) holder).imageView;
        } else {
            return null;
        }

    }


    public void setmItemListener(OnPhotoListener mItemListener) {
        this.mItemListener = mItemListener;
    }



    class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }


    }

    public interface OnPhotoListener {
        void onPhotoClick(int position);
    }
}
