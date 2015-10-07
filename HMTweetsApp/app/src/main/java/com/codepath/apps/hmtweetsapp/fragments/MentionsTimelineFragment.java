package com.codepath.apps.hmtweetsapp.fragments;

import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.hmtweetsapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class MentionsTimelineFragment extends TimelineFragment {

    @Override
    public void populateTimeline(final long sinceId, long maxId) {
        client.getMentionsTimeline(new JsonHttpResponseHandler() {
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
        }, sinceId, maxId);
    }
}
