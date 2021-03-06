package com.codepath.apps.TwitterApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.TwitterApp.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class PostActivity extends AppCompatActivity {

    Button tweetButton;
    EditText postText;
    EditText charCount;
    public static final String requestURL = "https://api.twitter.com/1.1/statuses/update.json";
    public static final String tag = "PostActivity";
    TwitterClient tc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        tweetButton = findViewById(R.id.tweetButton);
        postText = findViewById(R.id.postText);
        //charCount = findViewById(R.id.charCount);
        //charCount.setText("140 characters");
        tc = TwitterApplication.getRestClient(this);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(postText.getText().length() == 0){
                    Toast.makeText(getApplicationContext(), "The text entered is too short", Toast.LENGTH_SHORT).show();
                }
                if(postText.getText().length() > 140){
                    Toast.makeText(getApplicationContext(), "The text entered is too long", Toast.LENGTH_SHORT).show();
                }

                tc.post(postText.getText().toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Toast.makeText(getApplicationContext(), postText.getText().toString(), Toast.LENGTH_SHORT).show();
                        Log.i(tag, "sucessfully posted");
                         try {
                           Tweet tweet = Tweet.getTweet(json.jsonObject);
                            Intent i = new Intent();
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, i);
                            finish();

                        } catch (JSONException e) {
                            Log.e(tag, "oops", e);
                        }
                    };

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("PostActivity", "error creating handler for posting", throwable);
                    }
                });
            }
        });
    }
}