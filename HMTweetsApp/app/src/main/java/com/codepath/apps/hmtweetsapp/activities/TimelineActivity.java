package com.codepath.apps.hmtweetsapp.activities;

import android.app.FragmentManager;
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
import com.codepath.apps.hmtweetsapp.fragments.ComposeTweetDialog;
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
public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialog.ComposeTweetDialogListener {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tweet) {
            openComposeTweetDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openComposeTweetDialog() {
        FragmentManager fm = getFragmentManager();
        ComposeTweetDialog composeTweetDialog = ComposeTweetDialog.newInstance();
        composeTweetDialog.show(fm, "fragment_compose_tweet");
    }

    public void onComposeTweetSuccess() {
        minTweetId = Long.MAX_VALUE;
        mTweetsArray.clear();
        populateTimeline();
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
