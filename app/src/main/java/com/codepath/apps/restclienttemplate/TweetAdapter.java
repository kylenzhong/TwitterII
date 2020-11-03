package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.TimeFormatter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    List<Tweet> tweets;
    Context context;

    public TweetAdapter(List<Tweet> tweets, Context context){
        this.tweets = tweets;
        this.context = context;
    };
    @NonNull
    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetAdapter.ViewHolder holder, int position) {
        holder.bind(tweets.get(position));
    }

    public void clear(){
        tweets.clear();
        this.notifyDataSetChanged();
    }

    public void fetch(List<Tweet> tweetlist){
        tweets.addAll(tweetlist);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView nameText;
        TextView tweetText;
        TextView timeText;
        TextView handleText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            nameText = itemView.findViewById(R.id.nameText);
            tweetText = itemView.findViewById(R.id.tweetText);
            timeText = itemView.findViewById(R.id.timeText);
            handleText = itemView.findViewById(R.id.handleText);
        }

        public void bind(Tweet tweet) {
            tweetText.setText(tweet.getText());
            nameText.setText(tweet.getUser().getName());
            handleText.setText("@" + tweet.getUser().getHandle());

            try {
                timeText.setText(TimeFormatter.getTimeDifference(tweet.getCreateAt()));
            } catch (ParseException e) {
                Log.e("TweetAdapter", "failed to convert time");
            }
            Glide.with(context).load(tweet.getUser().getImageURL()).into(profileImage);

        }


    }
}
