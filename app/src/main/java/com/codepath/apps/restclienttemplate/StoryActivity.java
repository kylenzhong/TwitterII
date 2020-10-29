package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.LinearLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.github.scribejava.apis.TwitterApi;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class StoryActivity extends AppCompatActivity {
    TwitterClient client;
    List<Tweet> tweets;
    TweetAdapter adapter;
    RecyclerView rv;
    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        client = TwitterApplication.getRestClient(this);
        swipeContainer= findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                populateStory();
                swipeContainer.setRefreshing(false);
                Log.d("StoryActivity", "refreshed successfully");
            }
        });
        rv = findViewById(R.id.rvStory);
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(tweets, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        populateStory();
    }

    private void populateStory() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("StoryActivity", "successfully got timeline: " + json.jsonArray);

                try {
                    adapter.fetch(Tweet.getTweetsList(json.jsonArray));
                    adapter.notifyDataSetChanged();

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