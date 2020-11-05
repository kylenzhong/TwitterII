package com.codepath.apps.TwitterApp.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {

    @Embedded
    User user;

    @Embedded(prefix = "tweet_")
    Tweet tweet;

    public static List<Tweet> getTweetList(List<TweetWithUser> data) {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i<data.size(); i++){
            Tweet temp = data.get(i).tweet;
            temp.user = data.get(i).user;
            tweets.add(temp);
        }
        return tweets;
    }
}
