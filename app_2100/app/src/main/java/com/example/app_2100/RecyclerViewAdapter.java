package com.example.app_2100;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> mPosts;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    static final String TAG = "RecyclerViewAdapter";

    public RecyclerViewAdapter(List<Post> posts) {

        mPosts = posts;
        loadPosts();
    }

    private void loadPosts() {
        for (Post post : mPosts) {
            postLoadCallback(post);
        }
    }

    private void postLoadCallback(Post post) {
        post.setPostLoadCallback(new PostLoadCallback() {
            @Override
            public void onPostLoaded(Post post) {
                notifyDataSetChanged(); // Notify RecyclerViewAdapter when a post is loaded
            }
        });
    }

    // Based on the View type we are instantiating the ViewHolder in the onCreateViewHolder() method
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View postThumbnail = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home_feed_post_thumbnail, parent, false);
            return new PostThumbnailViewHolder(postThumbnail);
        } else {
            View loading = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_thumbnail_loading, parent, false);
            return new LoadingViewHolder(loading);
        }
    }

    // checking the type of ViewHolder instance and populating the row accordingly
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostThumbnailViewHolder) {
            populatePostThumbnails((PostThumbnailViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    // returns the size of the list
    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : mPosts.size();
    }

    // check each element of the list. If the element is NULL we set the view type as 1 else 0
    public int getItemViewType(int position) {
        return mPosts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    /////////////////// SERIOUS BUSINESS HERE
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private class PostThumbnailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTv;
        TextView authorTv;
        TextView dateTimeTv;
        TextView bodyTv;

        Button likeBt;
        Button shareBt;

        public PostThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.activity_home_feed_post_thumbnail_tv_title);
            authorTv = itemView.findViewById(R.id.activity_home_feed_post_thumbnail_tv_author);
            dateTimeTv = itemView.findViewById(R.id.activity_home_feed_post_thumbnail_tv_date);
            bodyTv = itemView.findViewById(R.id.activity_home_feed_post_thumbnail_tv_summary);

            likeBt = itemView.findViewById(R.id.activity_home_feed_post_thumbnail_bt_like);
//            shareBt = itemView.findViewById(R.id.activity_home_feed_post_thumbnail_bt_share);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

    }

    private void populatePostThumbnails(PostThumbnailViewHolder viewHolder, int position) {
        Post currPost = mPosts.get(position);

        viewHolder.titleTv.setText(currPost.getTitle());
        viewHolder.authorTv.setText(currPost.getAuthorName());
        viewHolder.dateTimeTv.setText(currPost.getFormattedDateTime());
        viewHolder.bodyTv.setText(currPost.getBody());

        // initialise the clicked state
        if (currPost.getLikedByCurrUser()) // user has liked the post
        {
            viewHolder.likeBt.setBackgroundResource(R.drawable.home_feed_post_thumbnail_like_clickable);
        } else { // user hasnt liked the post yet
            viewHolder.likeBt.setBackground(null);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });

        viewHolder.likeBt.setOnClickListener(v -> {
            Log.d(TAG, String.valueOf(currPost.getLikedByCurrUser()));
            if (currPost.getLikedByCurrUser()) // user has liked the post, now wants to unlike
            {
                viewHolder.likeBt.setBackground(null);
            } else { // user hasnt liked the post yet, and wants to like
                viewHolder.likeBt.setBackgroundResource(R.drawable.home_feed_post_thumbnail_like_clickable);
            }
            currPost.toggleLike(CurrentUser.getCurrent().getUserID());
        });

//        viewHolder.shareBt.setOnClickListener(v -> {
//            currPost.addShare(CurrentUser.getCurrent().getUserID());
//        });
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.post_thumbnail_pb_progress_bar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        // Progressbar would be displayed
    }
}
