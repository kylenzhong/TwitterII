package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.EndlessRecyclerViewScrollListener;
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
    private EndlessRecyclerViewScrollListener scrollListener;
    public static final int requestCode = 100;

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
        populateStory();

        View decorView = this.getWindow().getDecorView();
// Calling setSystemUiVisibility() with a value of 0 clears
// all flags.
        decorView.setSystemUiVisibility(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
            startActivityForResult(i, requestCode);
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