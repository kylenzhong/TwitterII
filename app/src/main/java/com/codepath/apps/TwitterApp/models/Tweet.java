package com.codepath.apps.TwitterApp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet {
    @PrimaryKey
    @ColumnInfo
    Long id;

    @ColumnInfo
    String text;

    @ColumnInfo
    String createdAt;

    @ColumnInfo
    Long userId;

    @Ignore
    User user;

    public Tweet(){

    }

    public static Tweet getTweet (JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();
            tweet.text = object.getString("text");
            tweet.createdAt = object.getString("created_at");
            tweet.user = User.getUser((object.getJSONObject("user")));
            tweet.id = object.getLong("id");
            tweet.userId = tweet.user.id;
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
