package com.example.laozhong.bigimagescaleview;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.transition.GestureTransitions;
import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator;
import com.alexvasilkov.gestures.transition.tracker.SimpleTracker;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.laozhong.bigimagescaleview.Adapter.EndlessRecyclerAdapter;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PhotoListAdapter.OnPhotoListener {
    private String url = "http://gank.io/api/data/福利/10/";
    private int mCurrentPage = 1;
    private Gson gson = new Gson();
    private List<Image> mImageList = new ArrayList<>();
    private PhotoListAdapter photoListAdapter;//瀑布流recyclerview的Adapter
    private ActivityViewHolder mActivityView;
    private ViewsTransitionAnimator<Integer> listAnimator;//动画类
    private PhotoPagerAdapter pagerAdapter;//viewPager的Adapter
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivityView = new ActivityViewHolder(this);
        loadData();
        initView();
        initPagerAnimator();

    }

    private void initView() {
        mActivityView.recyclerView = findViewById(R.id.photo_list);
        mActivityView.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        mActivityView.recyclerView.addItemDecoration(decoration);

        photoListAdapter = new PhotoListAdapter(mImageList, this);
        photoListAdapter.setmItemListener(this);
        photoListAdapter.setCallbacks(new EndlessRecyclerAdapter.LoaderCallbacks() {
            @Override
            public boolean canLoadNextItems() {
                return true;
            }

            @Override
            public void loadNextItems() {
                Log.d(TAG, "load next page");
                mCurrentPage++;
                loadData();

            }
        });
        mActivityView.recyclerView.setAdapter(photoListAdapter);
    }

    /**
     * Initializing grid-to-pager animation.
     */
    private void initPagerAnimator() {

        final SimpleTracker gridTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int pos) {
                //拿到对应的位置
                RecyclerView.ViewHolder holder = mActivityView.recyclerView.findViewHolderForLayoutPosition(pos);
                if (holder == null) {
                    Log.d(TAG, "grid holder " + "null");
                }
                return holder == null ? null : PhotoListAdapter.getImage(holder);
            }
        };

        pagerAdapter = new PhotoPagerAdapter(this, mActivityView.viewPager, mImageList);
        final SimpleTracker pagerTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int pos) {
                RecyclePagerAdapter.ViewHolder holder = pagerAdapter.getViewHolder(pos);
                if (holder == null) {

                    Log.d(TAG, "viewpager holdzer " + "null");
                }
                return holder == null ? null : PhotoPagerAdapter.getImage(holder);
            }
        };
        mActivityView.viewPager.setAdapter(pagerAdapter);
        listAnimator = GestureTransitions.from(mActivityView.recyclerView, gridTracker)
                .into(mActivityView.viewPager, pagerTracker);

        // Setting up and animating image transition

        listAnimator.addPositionUpdateListener(new ViewPositionAnimator.PositionUpdateListener() {
            @Override
            public void onPositionUpdate(float position, boolean isLeaving) {
                Log.d(TAG, "position：" + position + "isLeaving:" + isLeaving);
                mActivityView.fullBackGround.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                mActivityView.fullBackGround.setAlpha(position);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (!listAnimator.isLeaving()) {
            listAnimator.exit(true);
        } else {
            super.onBackPressed();
        }
    }


    private void loadData() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + mCurrentPage, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "response" + response.toString());
                Data data = gson.fromJson(response.toString(), Data.class);

                for (Image i : data.getResults()) {
                    i.setHeight((int) (400 + 300 * Math.random()));
                }
                mImageList.addAll(new ArrayList<Image>(Arrays.asList(data.getResults())));//
                Log.d(TAG, "size " + mImageList.size());
                photoListAdapter.notifyDataSetChanged();
                pagerAdapter.notifyDataSetChanged();
                photoListAdapter.onNextItemsLoaded();//加载完成
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "error: " + error.getMessage());
            }
        });
        Volley.newRequestQueue(this).add(request);

    }

    @Override
    public void onPhotoClick(int position) {
        //pagerAdapter.setA(true);
        Log.d(TAG, "onCLick");
        listAnimator.enter(position, true);
    }

    /**
     * 当前Activity的视图
     */
    class ActivityViewHolder {
        RecyclerView recyclerView;
        ViewPager viewPager;
        View fullBackGround;

        public ActivityViewHolder(Activity activity) {
            recyclerView = activity.findViewById(R.id.photo_list);
            viewPager = activity.findViewById(R.id.image_viewpager);
            fullBackGround = activity.findViewById(R.id.recycler_full_background);
        }
    }


    /**
     * item间距
     */
    class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }
}
