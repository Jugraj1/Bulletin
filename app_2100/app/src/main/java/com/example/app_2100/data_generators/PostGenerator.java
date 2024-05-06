package com.example.app_2100.data_generators;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app_2100.CreatePost;
import com.example.app_2100.CurrentUser;
import com.example.app_2100.FirebaseFirestoreConnection;
import com.example.app_2100.Post;
import com.example.app_2100.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PostGenerator {
    private final int nPosts;
    private List<User> users;
    private int nUsers;
    private FirebaseFirestore db;

    private final Random rand = new Random();

    public PostGenerator(int nPosts){
        this.nPosts = nPosts;
        db = FirebaseFirestoreConnection.getDb();

        db.collection("users")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> currData;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            currData = document.getData();
                            users.add(new User(
                                    document.getId(),
                                    (String) currData.get("firstName"),
                                    (String) currData.get("lastName")
                            ));
                        }
                        nUsers = users.size();


                    } else {
                        System.out.println("ERR getting users");
                    }
                }
            });
    }

    public User getRandomUser(){
        return this.users.get(rand.nextInt(users.size()));
    }

    public static void main(String[] args) {
        PostGenerator gen = new PostGenerator(20);
        FirebaseFirestore db = FirebaseFirestoreConnection.getDb();
        Random rand = new Random();
        for (int i=0; i<gen.getNPosts(); i++) {
            // generate post
            User user = gen.getRandomUser(); // set to random user

            Map<String, Object> post = new HashMap<>();
            post.put("title", title);
            post.put("publisher", publisher);
            post.put("url", url);
            post.put("body", content);
            post.put("author", user.getUserID());
            post.put("timeStamp", new Timestamp(new Date()));

            uploadPost(post, db);
        }
    }

    private static void uploadPost(Map<String, Object> post, FirebaseFirestore db){
        CollectionReference postsCollection = db.collection("posts");
        postsCollection.add(post)
                .addOnSuccessListener(documentReference -> {
                    // success
                })
                .addOnFailureListener(e -> {
                    // boohoo
                    System.out.println("ERR uploading post: "+ post);
                });
    }

    public int getNPosts() {
        return nPosts;
    }

    public List<User> getUsers() {
        return users;
    }

    public int getNUsers() {
        return nUsers;
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}
