package com.codepath.apps.TwitterApp.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {

    @Query("SELECT Tweet.text AS tweet_text, Tweet.createdAt AS tweet_createdAt, " +
            " Tweet.id AS tweet_id, User.* FROM Tweet INNER JOIN User ON Tweet.userId = User.id ORDER BY createdAt DESC LIMIT 5")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User... users);
}
