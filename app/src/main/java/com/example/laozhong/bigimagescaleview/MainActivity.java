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

import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.commons.circle.CircleGestureImageView;
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
    private static String TAG = MainActivity.class.getName();
    private String url = "http://gank.io/api/data/福利/10/";
    private int mCurrentPage = 1;
    private Gson gson = new Gson();
    private List<Image> mImageList = new ArrayList<>();
    private PhotoListAdapter photoListAdapter;
    private ActivityViewHolder mActivityView;
    private ViewsTransitionAnimator<Integer> listAnimator;
    private PhotoPagerAdapter pagerAdapter;

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

        pagerAdapter = new PhotoPagerAdapter(this, mActivityView.viewPager, mImageList);
        final SimpleTracker gridTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int pos) {
                //拿到对应的位置
                RecyclerView.ViewHolder holder = mActivityView.recyclerView.findViewHolderForLayoutPosition(pos);
                return holder == null ? null : PhotoListAdapter.getImage(holder);
            }
        };

        final SimpleTracker pagerTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int pos) {
                RecyclePagerAdapter.ViewHolder holder = pagerAdapter.getViewHolder(pos);
                return holder == null ? null : PhotoPagerAdapter.getImage(holder);
            }
        };

        listAnimator = GestureTransitions.from(mActivityView.recyclerView, gridTracker)
                .into(mActivityView.viewPager, pagerTracker);
        if (listAnimator == null) {
            Log.d("Tag", "null");
        }
        // Setting up and animating image transition
/*
        listAnimator.addPositionUpdateListener(this::applyFullPagerState);
*/
    }


    /*public void applyFullPagerState(){

    }*/
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
                photoListAdapter.onNextItemsLoaded();//加载完成
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error");
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


    class ActivityViewHolder {
        RecyclerView recyclerView;
        ViewPager viewPager;
        CircleGestureImageView circleGestureImageView;

        public ActivityViewHolder(Activity activity) {
            recyclerView = activity.findViewById(R.id.photo_list);
            viewPager = activity.findViewById(R.id.image_viewpager);
        }
    }


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
