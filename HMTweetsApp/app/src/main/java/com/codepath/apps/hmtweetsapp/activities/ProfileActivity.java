package com.codepath.apps.hmtweetsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.adapters.TweetsArrayAdapter;
import com.codepath.apps.hmtweetsapp.fragments.ComposeTweetDialog;
import com.codepath.apps.hmtweetsapp.fragments.UserTimelineFragment;
import com.codepath.apps.hmtweetsapp.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;

public class ProfileActivity extends AppCompatActivity implements UserTimelineFragment.UserTimelineFragmentListener, ComposeTweetDialog.ComposeTweetDialogListener, TweetsArrayAdapter.TweetsArrayAdapterListener {

    private User mUser;
    private UserTimelineFragment userTimelineFragment;
    private ImageView profilePic;
    private ImageView profileBackgroundPic;
    private TextView name;
    private TextView screenName;
    private TextView numFollowers;
    private TextView numFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupToolbar();
        setupUserTimeline();
    }

    /**
     * Sets up the toolbar on top of the activity
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_white));
        }
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

    private void setupUserTimeline() {
        String screenName = getIntent().getStringExtra("screenName");
        Log.d("DEBUG", "username = " + screenName);
        if(screenName != null && screenName != "") {
            userTimelineFragment = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flUserTimeline, userTimelineFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void setupUserInfo() {
        //Profile pic
        profilePic = (ImageView) findViewById(R.id.ivProfilePageProfileImage);
        profilePic.setImageResource(0);
        Picasso.with(this).load(mUser.getProfileImageUrl()).into(profilePic);
        Picasso.with(this).load(mUser.getProfileImageUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                dr.setCornerRadius(2.0f);
                profilePic.setImageDrawable(dr);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        profileBackgroundPic = (ImageView) findViewById(R.id.ivProfileBackground);
        if(mUser.getProfileBackgroundImageUrl() != null) {
            Picasso.with(this).load(mUser.getProfileBackgroundImageUrl()).fit().into(profileBackgroundPic);
        }

        name = (TextView) findViewById(R.id.tvProfilePageName);
        name.setText(mUser.getName());

        screenName = (TextView) findViewById(R.id.tvProfilePageScreenName);
        screenName.setText("@" + mUser.getScreenName());

        DecimalFormat formatter = new DecimalFormat("#,###,###");

        numFollowers = (TextView) findViewById(R.id.tvNumFollowers);
        numFollowers.setText(formatter.format(mUser.getNumFollowers()));

        numFollowing = (TextView) findViewById(R.id.tvNumFollowing);
        numFollowing.setText(formatter.format(mUser.getNumFollowing()));

    }

    @Override
    public void onUserInfoAvailable() {
        mUser = userTimelineFragment.getUser();
        setupUserInfo();
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
        // do nothing
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