package com.example.app_2100.data_generators;

import static com.example.app_2100.App.getContext;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app_2100.App;
import com.example.app_2100.FirebaseFirestoreConnection;
import com.example.app_2100.callbacks.InitialisationCallback;
import com.example.app_2100.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostGenerator {
    private final int nPosts;
    private InitialisationCallback callback;
    private List<User> users;
    private int nUsers;
    private final FirebaseFirestore db;

    private final Random rand = new Random();

    private final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String TAG = "PostGenerator";

    private final List<String> places;
    private final List<String> firstNames;
    private final List<String> lastNames;

    private List<String> likerIDs;
    private final List<String> urls;

    private final List<String> PUBLISHERS = Arrays.asList(
            "Penguin Random House", "HarperCollins", "Simon & Schuster", "Hachette Livre",
            "Macmillan Publishers", "Pearson PLC", "Wiley", "Scholastic Corporation",
            "Springer Nature", "Oxford University Press", "Bloomsbury Publishing",
            "John Wiley & Sons", "Cambridge University Press", "Elsevier", "Cengage",
            "McGraw-Hill Education", "Puffin Books", "Random House", "Peachpit Press",
            "Routledge", "Vintage Books", "Houghton Mifflin Harcourt", "Faber & Faber",
            "Grove Press", "National Geographic Society", "Little, Brown and Company",
            "Abrams Books", "Doubleday", "W.W. Norton & Company", "Harvard University Press"
    );

    private final List<String> TITLE_ADJECTIVES = Arrays.asList(
            "Interesting",
            "Exciting",
            "Unexpected",
            "Fascinating",
            "Thrilling",
            "Intriguing",
            "Captivating",
            "Surprising",
            "Stimulating",
            "Engaging",
            "Compelling",
            "Provocative",
            "Enthralling",
            "Riveting",
            "Dramatic",
            "Thought-provoking",
            "Gripping",
            "Challenging",
            "Inspiring",
            "Controversial"
    );

    public PostGenerator(int nPosts, InitialisationCallback callback){
        this.nPosts = nPosts;
        this.callback = callback;
        this.users = new ArrayList<>();

        places = App.readTextFile("places.txt");
        firstNames = App.readTextFile("first_names.txt");
        lastNames = App.readTextFile("last_names.txt");
        urls = App.readTextFile("urls.txt");

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

                            String fName = (String) currData.get("firstName");
                            String lName = (String) currData.get("lastName");
                            User user = new User(
                                    document.getId(),
                                    fName,
                                    lName
                            );
                            users.add(user);
                        }
                        nUsers = users.size();

                        Random rand = new Random();
                        // Generate the required number of posts
                        for (int i = 0; i < nPosts; i++) {
                            // generate post
                            User user = getRandomUser(); // set to random user
                            List<String> likes = createLikerIDs();
                            getAPIOutput(rand.nextInt(4)+1, new ProcessedAPIResCallback(){
                                @Override
                                public void onResProcessed(String title, String body) {
                                    Map<String, Object> post = new HashMap<>();
                                    post.put("title", createTitle(title));
                                    post.put("publisher", createPublisher());
                                    post.put("url", createURL());
                                    post.put("body", body);
                                    post.put("author", user.getUserID());
                                    post.put("timeStamp", createTimestamp());
                                    post.put("likes", likes);
                                    post.put("score", 0.0);

                                    uploadPost(post);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e(TAG, e.toString());
                                }
                            });
                        }

                        if (callback != null) {
                            callback.onInitialised(); // Call the callback when initialisation is complete
                        }
                        //
                    } else {
                        Log.e(TAG, "ERR getting users");
                    }
                }
            });
    }

    /**
     *  Generates a list of random user ID's to simulate users liking the post
     * @return A list of random user ID's
     */
    private List<String> createLikerIDs(){
        List<String> likes = new ArrayList<>();
        int randomNLikes = rand.nextInt(10);

        for (int i=0; i<randomNLikes; i++){
            likes.add(getRandomUser().getUserID());
        }
        return likes;
    }




    public interface ProcessedAPIResCallback {
        void onResProcessed(String title, String body);
        void onError(Exception e);
    }

    /**
     * Get the Article data from this API
     * @param nParagraphs number of paragraphs to generate (random)
     * @param callback
     */
    public void getAPIOutput(int nParagraphs, ProcessedAPIResCallback callback){
        String apiURL = String.format("https://corporatelorem.kovah.de/api/%d?format=text", nParagraphs);
        makeHttpGetRequest(apiURL, new HttpCallback() {
            @Override
            public void onResponse(String response) {
                String[] processedRes = separateTitle(response);
                String title = processedRes[0];
                String body = processedRes[1];

                callback.onResProcessed(title, body);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, e.toString());
                callback.onError(e);
            }
        });
    }

    /***
     * Format the api result to get the title. First line is the title, but not useful to post body.
     * @param input
     * @return Formatted title
     */
    public String[] separateTitle(String input) {
        int indexOfFirstNewline = input.indexOf('\n');
        if (indexOfFirstNewline != -1) {
            String title = input.substring(0, indexOfFirstNewline);
            String body = input.substring(indexOfFirstNewline + 1);
            return new String[]{title, removePTags(body)};
        }
        return new String[]{"", ""}; // If there is no newline, return an empty string
    }

    /***
     * Get rid of the html tags from the API output. Looks clunky to keep it in
     * @param htmlIn
     * @return Title without any html <p></p> tags
     */
    public String removePTags(String htmlIn) {
        return htmlIn.replaceAll("<p>|</p>", "");
    }

    /***
     *
     * @return A random user that exists in the database
     */
    public User getRandomUser(){
        return this.users.get(rand.nextInt(users.size()));
    }

    /***
     * Add more details to the title (random adjective, random location)
     * @param keyword The keyword from the API which says what the article is about
     * @return
     */
    private String createTitle(String keyword){
        int randomPlacesIndex = rand.nextInt(places.size());
        int randomAdjIndex = rand.nextInt(TITLE_ADJECTIVES.size());
        return String.format("%s %s Article in %s", TITLE_ADJECTIVES.get(randomAdjIndex), keyword, places.get(randomPlacesIndex)); // e.g. "{Interesting} {Sports} in {Pyongyang}"
    }

    /***
     * Generate random timestamp between now and 4 months ago - Generate posts within this age
     * @return random timestamp
     */
    private Timestamp createTimestamp(){
        long now = System.currentTimeMillis();
        long fourMonthsAgo = now - (4L * 30 * 24 * 60 * 60 * 1000);
        long randomTimestamp = fourMonthsAgo + (long) (Math.random() * (now - fourMonthsAgo));

        return new Timestamp(new Date(randomTimestamp));
    }
    public String createPublisher(){
        int randomIndex = rand.nextInt(PUBLISHERS.size());
        return PUBLISHERS.get(randomIndex);
    }
    public String createURL(){
        int randomIndex = rand.nextInt(urls.size());
        return urls.get(randomIndex);
    }

    /**
     * Callback used for performing API request
     */
    public interface HttpCallback {
        void onResponse(String response);
        void onError(Exception e);
    }
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public void makeHttpGetRequest(String urlString, HttpCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    System.out.println("Response Code: " + responseCode);

                    // get response from server
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line+"\n");
                    }
                    reader.close();
                    connection.disconnect();

                    // Pass the response back to the caller
                    callback.onResponse(response.toString());

                } catch (IOException e) {
                    // Pass the exception back to the caller
                    callback.onError(e);
                }
            }
        });
    }

    public void uploadPost(Map<String, Object> post){
        CollectionReference postsCollection = db.collection("posts");
        postsCollection.add(post)
                .addOnSuccessListener(documentReference -> {
                    // success
                    Log.d("PostGenerator", "Success, Post ID: "+documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // boohoo
                    Log.d("PostGenerator","ERR uploading post: "+ post);
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
