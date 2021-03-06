package com.codepath.apps.TwitterApp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codepath.apps.TwitterApp.models.EndlessRecyclerViewScrollListener;
import com.codepath.apps.TwitterApp.models.Tweet;
import com.codepath.apps.TwitterApp.models.TweetDao;
import com.codepath.apps.TwitterApp.models.TweetWithUser;
import com.codepath.apps.TwitterApp.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class StoryActivity extends AppCompatActivity {
    TwitterClient client;
    List<Tweet> tweets;
    TweetAdapter adapter;
    RecyclerView rv;
    SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    public static final int  postRequestCode = 100;
    TweetDao tweetDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        client = TwitterApplication.getRestClient(this);
        tweetDao = ((TwitterApplication) getApplicationContext()).getMyDatabase().tweetDao();
        swipeContainer= findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                populateStory();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("StoryActivity", "showing data from the database");
                        List<TweetWithUser> data = tweetDao.recentItems();
                        tweets.clear();
                        tweets.addAll(TweetWithUser.getTweetList(data));
                    }
                });
                swipeContainer.setRefreshing(false);
                Log.d("StoryActivity", "refreshed successfully");

            }
        });
        rv = findViewById(R.id.rvStory);
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(tweets, this);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i("StoryActivity", "succesfully loaded more");
                loadMoreData();
            }
        };
        rv.addOnScrollListener(scrollListener);
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i("StoryActivity", "showing data from the database");
                List<TweetWithUser> data = tweetDao.recentItems();
                tweets.clear();
                tweets.addAll(TweetWithUser.getTweetList(data));
            }
        });
        populateStory();

        View decorView = this.getWindow().getDecorView();
// Calling setSystemUiVisibility() with a value of 0 clears
// all flags.
        decorView.setSystemUiVisibility(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == postRequestCode && resultCode == RESULT_OK){
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            adapter.notifyDataSetChanged();
            rv.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toast.makeText(this, "this is the id: " + item.getItemId(), Toast.LENGTH_SHORT).show();
        if(item.getItemId() == R.id.composeButton){
            Intent i = new Intent(this, PostActivity.class);
            startActivityForResult(i,  postRequestCode);
        }
        return true;
    }

    private void loadMoreData() {
        client.scrollMore(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    adapter.fetch(Tweet.getTweetsList(json.jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("StoryActivity", "oops", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i("Story Activity", "trouble recieving data when scrolled");
            }
        }, tweets.get(tweets.size() -1).getId());
    }

    private void populateStory() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("StoryActivity", "successfully got timeline: " + json.jsonArray);

                try {
                    final List<Tweet> tweetsFromNetwork = Tweet.getTweetsList(json.jsonArray);
                    adapter.fetch(tweetsFromNetwork);
                    adapter.notifyDataSetChanged();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("StoryActivity", "Saving data into the database");
                            List<User> userList = User.getUserList(tweetsFromNetwork);
                            tweetDao.insertUser(userList.toArray(new User[0]));
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                        }
                    });

                    Log.i("StoryActivity", "successfully parsed jsonArray");
                } catch (JSONException e) {
                    Log.e("StoryActivity", "problem pasring json array into tweet list" , e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("StoryActivity", "having trouble getting hometimeline to client");
            }
        });
    }
}