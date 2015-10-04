package com.codepath.apps.hmtweetsapp.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

    private static long tweetsMaxId = Long.MAX_VALUE;
    private static long tweetsSinceId = 1;

    private TwitterClient mClient;
    private ArrayList<Tweet> mTweetsArray;
    private TweetsArrayAdapter mTweetsAdapter;
    private ListView mLvTweets;
    private ArrayList<Tweet> mCurrentUserTweetArray;
    private String mUserProfilePicUrl;

    private SwipeRefreshLayout swipeContainer;
    private ImageView ivNewTweet;

    public static long getTweetsMaxId() {
        return tweetsMaxId;
    }

    public static void setTweetsMaxId(long id) {
        tweetsMaxId = id;
    }

    public static long getTweetsSinceId() {
        return tweetsSinceId;
    }

    public static void setTweetsSinceId(long id) {
        tweetsSinceId = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setupToolbar();
        setupViewObjects();
        mClient = TwitterApplication.getTwitterClient();
        populateTimeline(tweetsSinceId, tweetsMaxId);

        setupSwipeToRefresh();
        getUserTimeline();
    }

    private void setupToolbar() {
        ivNewTweet = (ImageView) findViewById(R.id.ivComposeTweet);
        ivNewTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComposeTweetDialog("");
            }
        });
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
                populateTimeline(1, tweetsMaxId - 1);
                return true;
            }
        });
    }

    private void setupSwipeToRefresh() {

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(tweetsSinceId, tweetsMaxId);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * Get the data from the api and populate the listView
     */
    private void populateTimeline(final long sinceId, long maxId) {
        mClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());
                if (sinceId != 1) {
                    mTweetsArray.addAll(0, Tweet.fromJSONArray(jsonResponse));
                    mTweetsAdapter.notifyDataSetChanged();
                } else {
                    mTweetsAdapter.addAll(Tweet.fromJSONArray(jsonResponse));
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        }, sinceId, maxId);
    }

    public void openComposeTweetDialog(String replyText) {
        FragmentManager fm = getFragmentManager();
        ComposeTweetDialog composeTweetDialog = ComposeTweetDialog.newInstance(mUserProfilePicUrl, replyText);
        composeTweetDialog.show(fm, "fragment_compose_tweet");
    }

    public void onComposeTweetSuccess() {
        populateTimeline(tweetsSinceId, tweetsMaxId);
    }

    private void getUserTimeline() {
        mCurrentUserTweetArray = new ArrayList<>();

        mClient.getUserTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());
                mCurrentUserTweetArray.addAll(Tweet.fromJSONArray(jsonResponse));
                if(mCurrentUserTweetArray != null && mCurrentUserTweetArray.size() > 0) {
                    mUserProfilePicUrl = mCurrentUserTweetArray.get(0).getUser().getProfileImageUrl();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });


    }

}
