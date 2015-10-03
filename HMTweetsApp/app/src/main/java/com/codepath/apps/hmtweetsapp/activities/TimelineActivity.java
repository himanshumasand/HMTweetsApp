package com.codepath.apps.hmtweetsapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.TwitterApplication;
import com.codepath.apps.hmtweetsapp.TwitterClient;
import com.codepath.apps.hmtweetsapp.adapters.TweetsArrayAdapter;
import com.codepath.apps.hmtweetsapp.models.Tweet;
import com.codepath.apps.hmtweetsapp.utils.EndlessScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Activity that shows the stream of tweets
 */
public class TimelineActivity extends AppCompatActivity {

    private static long minTweetId = Long.MAX_VALUE;

    private TwitterClient mClient;
    private ArrayList<Tweet> mTweetsArray;
    private TweetsArrayAdapter mTweetsAdapter;
    private ListView mLvTweets;

    public static long getMinTweetId() {
        return minTweetId;
    }

    public static void setMinTweetId(long id) {
        minTweetId = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setupViewObjects();
        mClient = TwitterApplication.getTwitterClient();
        populateTimeline();
    }

    /**
     * Attaches the adapter to the listview
     */
    private void setupViewObjects() {
        mLvTweets = (ListView) findViewById(R.id.lvTweets);
        mTweetsArray = new ArrayList<>();
        mTweetsAdapter = new TweetsArrayAdapter(this, mTweetsArray);
        mLvTweets.setAdapter(mTweetsAdapter);
        mLvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                populateTimeline();
                return true;
            }
        });
    }

    /**
     * Get the data from the api and populate the listView
     */
    private void populateTimeline() {
        mClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());
                mTweetsAdapter.addAll(Tweet.fromJSONArray(jsonResponse));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        }, minTweetId);
    }
}
