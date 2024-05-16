package com.example.app_2100;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_2100.callbacks.PostLoadCallback;
import com.example.app_2100.listeners.OnItemClickListener;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    static final String TAG = "RecyclerViewAdapter";

    /**
     * Constructor for RecyclerViewAdapter.
     * @param posts List of Post objects to be displayed in the RecyclerView.
     */
    public RecyclerViewAdapter(List<Post> posts) {
        this.posts = posts;
        loadPosts();
    }

    /**
     * Loads posts by initialising postLoadCallback for each post.
     */
    private void loadPosts() {
        for (Post post : posts) {
            postLoadCallback(post);
        }
    }

    /**
     * Once the post has finished creating, notify the RecyclerView that a new post has loaded
     * @param post The post to be loaded in the RecyclerView
     */
    private void postLoadCallback(Post post) {
        post.setPostLoadCallback(new PostLoadCallback() {
            @Override
            public void onPostLoaded(Post post) {
                notifyDataSetChanged(); // Notify RecyclerViewAdapter when a post is loaded
            }
        });
    }

    /**
     * Based on the View type we are instantiating the ViewHolder to be shown
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return The ViewHolder to be displayed
     */
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

    /**
     * Check the type of ViewHolder instance and populating the row if neccessary, otherwise displaying loading
      */

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostThumbnailViewHolder) {
            populatePostThumbnails((PostThumbnailViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    /**
     *
     * @return The number of items in the list of posts, or 0 if the list is null
     */
    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    /**
     * Checks element of the list at a position. If the element is NULL we set the view type as 1 else 0
     * @param position position to query
     * @return Number corresponding to whether the item or loading icon should be displayed
     */
    public int getItemViewType(int position) {
        return posts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private OnItemClickListener mListener;

    /**
     * Sets the item click listener for items in the RecyclerView.
     * @param listener The OnItemClickListener to be set.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * ViewHolder for a PostThumbnail in the RecyclerView.
     */
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

    /**
     * Populates the PostThumbnailViewHolder with data for a specific post.
     * @param viewHolder The ViewHolder representing he PostThumbnail to be populated.
     * @param position The position of the post in the list of posts in the recyclerviewer.
     */
    private void populatePostThumbnails(PostThumbnailViewHolder viewHolder, int position) {
        Post currPost = posts.get(position);

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

    /**
     * ViewHolder for the loading indicator in the RecyclerView.
     */
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.post_thumbnail_pb_progress_bar);
        }
    }

    /**
     * Displays the loading view in the RecyclerView.
     * @param viewHolder The ViewHolder for the loading view.
     * @param position The position of the loading view in the list.
     */
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        // Progressbar would be displayed
        viewHolder.progressBar.setVisibility(View.VISIBLE);
    }
}
