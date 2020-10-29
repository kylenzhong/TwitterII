package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    String name;
    String handle;
    String imageURL;

    public static User getUser(JSONObject object) throws JSONException {
        User user = new User();
            user.name = object.getString("name");
            user.handle = object.getString("screen_name");
            user.imageURL = object.getString("profile_image_url_https");
        return user;
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
