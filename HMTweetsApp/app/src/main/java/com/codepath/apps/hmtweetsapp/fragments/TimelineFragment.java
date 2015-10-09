package com.codepath.apps.hmtweetsapp.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.TwitterApplication;
import com.codepath.apps.hmtweetsapp.TwitterClient;
import com.codepath.apps.hmtweetsapp.adapters.TweetsArrayAdapter;
import com.codepath.apps.hmtweetsapp.models.Tweet;
import com.codepath.apps.hmtweetsapp.utils.EndlessScrollListener;
import java.util.ArrayList;

public abstract class TimelineFragment extends Fragment {

    // Used for the since_id and max_id arguments in the api call to get home timeline
    private long tweetsMaxId = Long.MAX_VALUE;
    private long tweetsSinceId = 1;

    //Checks if the call is the first call for the session
    protected boolean firstApiCall = true;

    //Data structures used to set up the home timeline list
    protected TwitterClient client;
    protected ArrayList<Tweet> mTweetsArray;
    protected TweetsArrayAdapter mTweetsAdapter;
    protected ListView mLvTweets;

    //Other UI related items in the activity
    private View fragmentView;
    protected SwipeRefreshLayout swipeContainer;

    public long getTweetsMaxId() { return tweetsMaxId; }

    public long getTweetsSinceId() { return tweetsSinceId; }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_timeline, container, false);

        setupViewObjects();
        client = TwitterApplication.getTwitterClient();
        populateTimeline(tweetsSinceId, tweetsMaxId);
        setupSwipeToRefresh();

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
    public abstract void populateTimeline(long sinceId, long maxId);

    protected void setMinMaxTweetIds() {
        for (int i = 0; i < mTweetsArray.size(); i++) {
            long id = mTweetsArray.get(i).getTweetId();
            if(id < tweetsMaxId) {
                tweetsMaxId = id;
            }
            if(id > tweetsSinceId) {
                tweetsSinceId = id;
            }
        }
    }


    /**
     * Utility function to check if network is available
     * @return      True if network is available
     */
    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
