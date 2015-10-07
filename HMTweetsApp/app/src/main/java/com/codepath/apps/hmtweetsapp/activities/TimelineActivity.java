package com.codepath.apps.hmtweetsapp.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.TwitterApplication;
import com.codepath.apps.hmtweetsapp.TwitterClient;
import com.codepath.apps.hmtweetsapp.adapters.TweetsArrayAdapter;
import com.codepath.apps.hmtweetsapp.fragments.ComposeTweetDialog;
import com.codepath.apps.hmtweetsapp.fragments.TimelineFragment;
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

    //Fragments
    private TimelineFragment timelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setupToolbar();
        setupFragments();
        setupTimeline();
    }

    /**
     * Sets up the toolbar on top of the activity
     */
    private void setupToolbar() {
        ImageView ivNewTweet = (ImageView) findViewById(R.id.ivComposeTweet);
        ivNewTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComposeTweetDialog("");
            }
        });
    }

    private void setupFragments() {
        timelineFragment = new TimelineFragment();
    }

    private void setupTimeline() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flTimeline, timelineFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Opens up the fragment to compose a new tweet
     * @param replyText     The screenname to be added while replying
     */
    public void openComposeTweetDialog(String replyText) {
        FragmentManager fm = getFragmentManager();
        ComposeTweetDialog composeTweetDialog = ComposeTweetDialog.newInstance(getUserProfileImageUrl(), replyText);
        composeTweetDialog.show(fm, "fragment_compose_tweet");
    }

    /**
     * Listener after a tweet has been successfully posted and compose tweet dialog is closed
     */
    public void onComposeTweetSuccess() {
        //tell the fragment to perform this operation
        timelineFragment.populateTimeline(TimelineFragment.getTweetsSinceId(), TimelineFragment.getTweetsMaxId());
    }

    private String getUserProfileImageUrl() {
        String url;

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_preference_file_key), Context.MODE_PRIVATE);
        url = sharedPref.getString(getString(R.string.user_profile_pic_url), "");

        return url;
    }
}
