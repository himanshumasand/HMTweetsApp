package com.codepath.apps.hmtweetsapp.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.codepath.apps.hmtweetsapp.activities.TimelineActivity;
import com.codepath.apps.hmtweetsapp.fragments.TimelineFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Model that holds all information of a single tweet
 */
@Table(name = "Tweets")
public class Tweet extends Model{

    @Column(name = "TweetId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long tweetId;
    @Column(name = "Body")
    String body;
    @Column(name = "ImageUrl")
    String imageUrl;
    @Column(name = "Timestamp")
    String timestamp;
    @Column(name = "RetweetCount")
    int retweetCount;
    @Column(name = "FavoriteCount")
    int favoriteCount;
    @Column(name = "User", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    User user;

    public long getTweetId() {
        return tweetId;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public Tweet() {}
    /**
     * Deserialize the JSON object and create a Tweet object
     * @param jsonObject    JSON object returned by the api for a single tweet
     * @return              Tweet object used by the view
     */
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.tweetId = jsonObject.getLong("id");
            tweet.body = jsonObject.getString("text");
            tweet.timestamp = jsonObject.getString("created_at");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            if(jsonObject.has("extended_entities") && jsonObject.getJSONObject("extended_entities") != null
                    && jsonObject.getJSONObject("extended_entities").getJSONArray("media") != null
                    && jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0) != null
                    && jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).getString("media_url") != null) {
                tweet.imageUrl = jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).getString("media_url");
            }
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.save();
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
                    if(tweet.tweetId < TimelineFragment.getTweetsMaxId()) {
                        TimelineFragment.setTweetsMaxId(tweet.tweetId);
                    }
                    if(tweet.tweetId > TimelineFragment.getTweetsSinceId()) {
                        TimelineFragment.setTweetsSinceId(tweet.tweetId);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    /**
     * Utility function to get all tweets
     * @return      All the tweets stored in the table
     */
    public static List<Tweet> allTweets() {
        return new Select().from(Tweet.class).execute();
    }
}
