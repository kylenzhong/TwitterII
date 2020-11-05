package com.codepath.apps.TwitterApp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {
    @PrimaryKey
    @ColumnInfo
    Long id;

    @ColumnInfo
    String name;

    @ColumnInfo
    String handle;

    @ColumnInfo
    String imageURL;

    public User(){
    }

    public static User getUser(JSONObject object) throws JSONException {
        User user = new User();
            user.name = object.getString("name");
            user.id = object.getLong("id");
            user.handle = object.getString("screen_name");
            user.imageURL = object.getString("profile_image_url_https");
        return user;
    }

    public static List<User> getUserList(List<Tweet> tweetsFromNetwork) {
        List<User> result = new ArrayList<>();
        for(int i = 0; i<tweetsFromNetwork.size(); i++){
            result.add(tweetsFromNetwork.get(i).user);
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public String getHandle() {
        return handle;
    }

    public String getImageURL() {
        return imageURL;
    }
}
