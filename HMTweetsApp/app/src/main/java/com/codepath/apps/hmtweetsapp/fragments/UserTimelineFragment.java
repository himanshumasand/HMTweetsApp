package com.codepath.apps.hmtweetsapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.models.Tweet;
import com.codepath.apps.hmtweetsapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserTimelineFragment extends TimelineFragment {

    private User mUser;

    public UserTimelineFragment() {}

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        fragment.setArguments(args);
        return fragment;
    }

    public interface UserTimelineFragmentListener {
        void onUserInfoAvailable();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
        final UserTimelineFragmentListener listener = (UserTimelineFragmentListener) getActivity();
        listener.onUserInfoAvailable();
    }

    @Override
    public void populateTimeline(final long sinceId, long maxId) {
        final String screenName = getArguments().getString("screenName");
        client.getUserTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());

                //Used for pull to refresh. Does not clear the arrayList to reduce processing of redundant items.
                //Also, using since_id gives only the new tweets and reduces redundant download of data
                if (sinceId != 1) {
                    mTweetsArray.addAll(0, Tweet.fromJSONArray(jsonResponse));
                    mTweetsAdapter.notifyDataSetChanged();
                } else {
                    mTweetsArray.addAll(Tweet.fromJSONArray(jsonResponse));
                    mTweetsAdapter.notifyDataSetChanged();
                }

                setUser(mTweetsArray.get(0).getUser());

                if (mTweetsArray != null && mTweetsArray.size() > 0 && mUser.getScreenName().equals("MasandHimanshu")) {
                    writeToSharedPreferences(getString(R.string.user_profile_pic_url),
                            mUser.getProfileImageUrl());
                }

                setMinMaxTweetIds();
                swipeContainer.setRefreshing(false);
                Log.d("DEBUG", String.valueOf(Tweet.allTweets().size()));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //If network is not available, fetch the data from the database
                if (!isNetworkAvailable()) {
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }
        }, screenName, sinceId, maxId);
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
}
