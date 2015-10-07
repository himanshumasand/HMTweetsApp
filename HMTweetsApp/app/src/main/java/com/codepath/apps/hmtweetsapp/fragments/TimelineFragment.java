package com.codepath.apps.hmtweetsapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
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
 * Created by Himanshu on 10/6/2015.
 */
public class TimelineFragment extends Fragment {

    // Used for the since_id and max_id arguments in the api call to get home timeline
    private static long tweetsMaxId = Long.MAX_VALUE;
    private static long tweetsSinceId = 1;

    //Checks if the call is the first call for the session
    private boolean firstApiCall = true;

    //Data structures used to set up the home timeline list
    private TwitterClient mClient;
    private ArrayList<Tweet> mTweetsArray;
    private TweetsArrayAdapter mTweetsAdapter;
    private ListView mLvTweets;
    private ArrayList<Tweet> mCurrentUserTweetArray;

    //Other UI related items in the activity
    private View fragmentView;
    private SwipeRefreshLayout swipeContainer;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_timeline, container, false);

        setupViewObjects();
        mClient = TwitterApplication.getTwitterClient();
        populateTimeline(tweetsSinceId, tweetsMaxId);

        setupSwipeToRefresh();
        getUserTimeline();

        return fragmentView;
    }

    /**
     * Attaches the adapter to the listview
     */
    private void setupViewObjects() {
        mLvTweets = (ListView) fragmentView.findViewById(R.id.lvTweets);
        mTweetsArray = new ArrayList<>();
        mTweetsAdapter = new TweetsArrayAdapter(getActivity(), mTweetsArray);
        mLvTweets.setAdapter(mTweetsAdapter);
        if(isNetworkAvailable()) {
            mLvTweets.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    if (isNetworkAvailable()) {
                        populateTimeline(1, tweetsMaxId - 1);
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Sets up the listener for pull to refresh
     */
    private void setupSwipeToRefresh() {

        swipeContainer = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable()) {
                    populateTimeline(tweetsSinceId, tweetsMaxId);
                } else {
                    swipeContainer.setRefreshing(false);
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                }
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
    public void populateTimeline(final long sinceId, long maxId) {
        mClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());

                //If the first call is successful, delete everything in the database and re-enter new data
                //Happens only once per session
                if (firstApiCall) {
                    new Delete().from(Tweet.class).execute();
                    firstApiCall = false;
                }

                //Used for pull to refresh. Does not clear the arrayList to reduce processing of redundant items.
                //Also, using since_id gives only the new tweets and reduces redundant download of data
                if (sinceId != 1) {
                    mTweetsArray.addAll(0, Tweet.fromJSONArray(jsonResponse));
                    mTweetsAdapter.notifyDataSetChanged();
                } else {
                    mTweetsAdapter.addAll(Tweet.fromJSONArray(jsonResponse));
                }
                swipeContainer.setRefreshing(false);
                Log.d("DEBUG", String.valueOf(Tweet.allTweets().size()));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //If network is not available, fetch the data from the database
                if (!isNetworkAvailable()) {
                    Log.d("DEBUG", "Size = " + String.valueOf(Tweet.allTweets().size()));
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                    mTweetsAdapter.addAll(Tweet.allTweets());
                } else {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }
        }, sinceId, maxId);
    }

    /**
     * Gets the logged in user's timeline
     */
    private void getUserTimeline() {
        mCurrentUserTweetArray = new ArrayList<>();

        mClient.getUserTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());
                mCurrentUserTweetArray.addAll(Tweet.fromJSONArray(jsonResponse));
                if (mCurrentUserTweetArray != null && mCurrentUserTweetArray.size() > 0) {
                    writeToSharedPreferences(getString(R.string.user_profile_pic_url),
                            mCurrentUserTweetArray.get(0).getUser().getProfileImageUrl());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (isNetworkAvailable()) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }
        });
    }

    /**
     * Adds a key value pair to the shared preferences
     * @param key       String for the key
     * @param value     String for the value
     */
    private void writeToSharedPreferences(String key, String value) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.shared_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Utility function to check if network is available
     * @return      True if network is available
     */
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
