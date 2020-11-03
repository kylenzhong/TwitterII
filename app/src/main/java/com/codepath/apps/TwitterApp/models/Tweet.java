package com.codepath.apps.TwitterApp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Tweet {
    String text;
    String createdAt;
    User user;
    Long id;

    public Tweet(){

    }

    public static Tweet getTweet (JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.text = object.getString("text");
        tweet.createdAt = object.getString("created_at");
        tweet.user = User.getUser((object.getJSONObject("user")));
        tweet.id = object.getLong("id");
        return tweet;
    }

    public static ArrayList<Tweet> getTweetsList(JSONArray array) throws JSONException {
        ArrayList<Tweet> list = new ArrayList();
        for(int i = 0; i<array.length(); i++){
            list.add(getTweet(array.getJSONObject(i)));
        }
        return list;
    }

    public String getText() {

        return text;
    }

    public String getCreateAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }
}
