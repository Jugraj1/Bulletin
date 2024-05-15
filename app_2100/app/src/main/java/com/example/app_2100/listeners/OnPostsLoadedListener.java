package com.example.app_2100.listeners;

import com.example.app_2100.Post;

import java.util.List;


public interface OnPostsLoadedListener {
    // interface for the post loaded listener
    void onPostsLoaded(List<Post> posts);
}
