package com.codepath.apps.hmtweetsapp.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.fragments.ComposeTweetDialog;
import com.codepath.apps.hmtweetsapp.fragments.HomeTimelineFragment;
import com.codepath.apps.hmtweetsapp.fragments.MentionsTimelineFragment;
import com.codepath.apps.hmtweetsapp.fragments.TimelineFragment;

/**
 * Activity that shows the stream of tweets
 */
public class HomeActivity extends AppCompatActivity implements ComposeTweetDialog.ComposeTweetDialogListener {

    //Fragments
    private HomeTimelineFragment homeTimelineFragment;
    private MentionsTimelineFragment mentionsTimelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        homeTimelineFragment = new HomeTimelineFragment();
        mentionsTimelineFragment = new MentionsTimelineFragment();
    }

    private void setupTimeline() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flTimeline, homeTimelineFragment);
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
        homeTimelineFragment.populateTimeline(TimelineFragment.getTweetsSinceId(), TimelineFragment.getTweetsMaxId());
    }

    private String getUserProfileImageUrl() {
        String url;

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_preference_file_key), Context.MODE_PRIVATE);
        url = sharedPref.getString(getString(R.string.user_profile_pic_url), "");

        return url;
    }
}
