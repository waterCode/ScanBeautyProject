package com.example.laozhong.bigimagescaleview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by lao.zhong on 2018/3/9.
 */

public class PhotoPagerAdapter extends RecyclePagerAdapter<PhotoPagerAdapter.ViewHolder> {
    private List<Image> mPhotoList;
    private Context mContext;
    private ViewPager mViewPager;

    public PhotoPagerAdapter(Context context, ViewPager viewPager,List<Image> photoList) {
        this.mPhotoList = photoList;
        mContext = context;
        mViewPager = viewPager;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup container) {
        ViewHolder viewHolder = new ViewHolder(container);
        viewHolder.gestureImageView.getController().enableScrollInViewPager(mViewPager);//设置viewPager
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mPhotoList !=null){
            //不为null的话
            Glide.with(mContext).load(mPhotoList.get(position)).into(holder.gestureImageView);

        }
    }

    @Override
    public int getCount() {
        return (mPhotoList == null) ? 0 : mPhotoList.size();
    }

    public static View getImage(RecyclePagerAdapter.ViewHolder holder) {
        if(holder instanceof  PhotoPagerAdapter.ViewHolder){
            return ((ViewHolder) holder).gestureImageView;
        }else {
            return null;
        }
    }

    class ViewHolder extends RecyclePagerAdapter.ViewHolder {
        private GestureImageView gestureImageView;
        private View progressDialog;

        ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.full_image_view, parent, false));

            gestureImageView = itemView.findViewById(R.id.gesture_ImageView);
            //progressDialog = itemView.findViewById(R.id.loading_progress);

        }
    }


}
