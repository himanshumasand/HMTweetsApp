package com.codepath.apps.hmtweetsapp.fragments;

import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.codepath.apps.hmtweetsapp.models.Tweet;
import com.codepath.apps.hmtweetsapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeTimelineFragment extends TimelineFragment {

    @Override
    public void populateTimeline(final long sinceId, long maxId) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());

                //If the first call is successful, delete everything in the database and re-enter new data
                //Happens only once per session
                if (firstApiCall) {
                    new Delete().from(Tweet.class).execute();
                    new Delete().from(User.class).execute();
                    firstApiCall = false;
                }

                //Used for pull to refresh. Does not clear the arrayList to reduce processing of redundant items.
                //Also, using since_id gives only the new tweets and reduces redundant download of data
                if (sinceId != 1) {
                    mTweetsArray.addAll(0, Tweet.fromJSONArray(jsonResponse));
                    mTweetsAdapter.notifyDataSetChanged();
                } else {
                    mTweetsArray.addAll(Tweet.fromJSONArray(jsonResponse));
                    mTweetsAdapter.notifyDataSetChanged();
                }

                setMinMaxTweetIds();
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
}
