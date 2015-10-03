package com.codepath.apps.hmtweetsapp.models;

import com.codepath.apps.hmtweetsapp.activities.TimelineActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Model that holds all information of a single tweet
 */
public class Tweet {

    long id;
    String body;
    String timestamp;
    int retweetCount;
    int favoriteCount;
    User user;

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public User getUser() {
        return user;
    }

    /**
     * Deserialize the JSON object and create a Tweet object
     * @param jsonObject    JSON object returned by the api for a single tweet
     * @return              Tweet object used by the view
     */
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.id = jsonObject.getLong("id");
            tweet.body = jsonObject.getString("text");
            tweet.timestamp = jsonObject.getString("created_at");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    /**
     * Converts the JSON array to an array list of tweet objects
     * @param jsonArray     JSON array returned by the api for the home timeline
     * @return              A list of tweets
     */
    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = fromJSON(tweetJson);
                if(tweet != null) {
                    tweets.add(tweet);
                    if(tweet.id < TimelineActivity.getMinTweetId()) {
                        TimelineActivity.setMinTweetId(tweet.id);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }
}
