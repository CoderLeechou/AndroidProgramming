package com.lzwap.android.photogallery;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private ViewTreeObserver mObserver;
    private PhotoAdapter mPhotoAdapter;
    private List<GalleryItem> mItems = new ArrayList<>();
    private FetchItemsTask mFetchItemsTask;
    private int mNextPage = 1;
    private int mLastPosition;

    private final int MAX_PAGES = 10;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //new FetchItemsTask().execute();
        mFetchItemsTask = new FetchItemsTask();
        mFetchItemsTask.execute(1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        //mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        mPhotoRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int columns = mPhotoRecyclerView.getWidth() / 350;
                        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columns));
                        mPhotoRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (!(mFetchItemsTask.getStatus() == AsyncTask.Status.FINISHED)) {
                            return;
                        }
                        mPhotoRecyclerView.setAdapter(mPhotoAdapter);
                        mPhotoRecyclerView.addOnScrollListener(onButtomListener);
                        mPhotoRecyclerView.getLayoutManager().scrollToPosition(mLastPosition);
                    }
                });

        setupAdapter();

        return v;
    }

    private void setupAdapter() {
        if (isAdded()) {
            //mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
            if (mPhotoAdapter == null) {
                mPhotoAdapter = new PhotoAdapter(mItems);
                mPhotoRecyclerView.setAdapter(mPhotoAdapter);
                mPhotoRecyclerView.addOnScrollListener(onButtomListener);
            } else {
                mPhotoAdapter.addData(mItems);
            }
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            mTitleTextView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

        public void addData(List<GalleryItem> newItems) {
            mGalleryItems.addAll(newItems);
            notifyDataSetChanged();
        }

    }

    private class FetchItemsTask extends AsyncTask<Integer, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Integer... voids) {
//            try {
//                String result = new FlickrFetchr()
//                        .getUrlString("https://www.bignerdranch.com");
//                Log.i(TAG, "Fetched contents of URL: " + result);
//            } catch (IOException ioe) {
//                Log.e(TAG, "Failed to fetch URL: ", ioe);
//            }
            return new FlickrFetchr().fetchItems(voids[0]);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }

    private RecyclerView.OnScrollListener onButtomListener =
            new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    //int lastPosition = layoutManager.findLastVisibleItemPosition();
                    mLastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (mPhotoAdapter == null) {
                        return;
                    }
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                            && mLastPosition >= mPhotoAdapter.getItemCount() - 1) {
                        if (mFetchItemsTask.getStatus() == AsyncTask.Status.FINISHED) {
                            mNextPage++;
                            if (mNextPage <= MAX_PAGES) {
                                Toast.makeText(getActivity(), "waiting to load ……", Toast.LENGTH_SHORT).show();
                                mFetchItemsTask = new FetchItemsTask();
                                mFetchItemsTask.execute(mNextPage);
                            } else {
                                Toast.makeText(getActivity(), "This is the end!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            };
}
