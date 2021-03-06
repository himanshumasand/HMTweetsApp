package com.codepath.apps.hmtweetsapp.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.adapters.TimelineFragmentPagerAdapter;
import com.codepath.apps.hmtweetsapp.adapters.TweetsArrayAdapter;
import com.codepath.apps.hmtweetsapp.fragments.ComposeTweetDialog;
import com.codepath.apps.hmtweetsapp.fragments.HomeTimelineFragment;
import com.codepath.apps.hmtweetsapp.fragments.MentionsTimelineFragment;

/**
 * Activity that shows the stream of tweets
 */
public class HomeActivity extends AppCompatActivity implements ComposeTweetDialog.ComposeTweetDialogListener, TweetsArrayAdapter.TweetsArrayAdapterListener {

    //Fragments
    private HomeTimelineFragment homeTimelineFragment;
    private MentionsTimelineFragment mentionsTimelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupToolbar();
        setupFragments();
        setupViewPager();
    }

    /**
     * Sets up the toolbar on top of the activity
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView ivNewTweet = (ImageView) findViewById(R.id.ivComposeTweet);
        ivNewTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComposeTweetDialog("");
            }
        });
        ImageView ivProfile = (ImageView) findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class).putExtra("screenName", "MasandHimanshu");
                startActivity(profileIntent);
            }
        });
    }

    private void setupFragments() {
        homeTimelineFragment = new HomeTimelineFragment();
        mentionsTimelineFragment = new MentionsTimelineFragment();
    }

    private void setupViewPager() {
        ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);
        vPager.setAdapter( new TimelineFragmentPagerAdapter(getSupportFragmentManager(), this));
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vPager);
    }

    /**
     * Opens up the fragment to compose a new tweet
     * @param replyText     The screenname to be added while replying
     */
    public void openComposeTweetDialog(String replyText) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialog composeTweetDialog = ComposeTweetDialog.newInstance(getUserProfileImageUrl(), replyText);
        composeTweetDialog.show(fm, "fragment_compose_tweet");
    }

    /**
     * Listener after a tweet has been successfully posted and compose tweet dialog is closed
     */
    public void onComposeTweetSuccess() {
        recreate();
    }

    private String getUserProfileImageUrl() {
        String url;

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.shared_preference_file_key), Context.MODE_PRIVATE);
        url = sharedPref.getString(getString(R.string.user_profile_pic_url), "");

        return url;
    }

    @Override
    public void onReplyToTweet(String screenName) {
        openComposeTweetDialog(screenName);
    }
}
