package com.codepath.apps.hmtweetsapp;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "F6UhO4jZOlA261MQQ0SrdLOg5";
	public static final String REST_CONSUMER_SECRET = "AnhwKObBDro0zybDaw2HRgNulQgrVtLJfEp77VOfS2EpX8EpMP";
	public static final String REST_CALLBACK_URL = "oauth://hmtweetsapp";	//Also change it in the manifest

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/**
	 * Gets the home timeline by making a GET call
	 * @param handler   callback
     * @param sinceId
	 * @param maxId
	 */
	public void getHomeTimeline(AsyncHttpResponseHandler handler, long sinceId, long maxId) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", sinceId);
		if(maxId != Long.MAX_VALUE && sinceId == 1) {
			params.put("max_id", maxId);
		}
        getClient().get(apiUrl, params, handler);
	}

	/**
	 * Gets the home timeline by making a GET call
	 * @param handler   callback
     * @param sinceId
	 * @param maxId
	 */
	public void getMentionsTimeline(AsyncHttpResponseHandler handler, long sinceId, long maxId) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", sinceId);
		if(maxId != Long.MAX_VALUE && sinceId == 1) {
			params.put("max_id", maxId);
		}
        getClient().get(apiUrl, params, handler);
	}

	/**
	 * Gets the home timeline by making a GET call
	 * @param handler       callback
     * @param screenName    the screenname of the user whose timeline is to be retrieved
	 * @param sinceId
	 * @param maxId
	 */
	public void getUserTimeline(AsyncHttpResponseHandler handler, String screenName, long sinceId, long maxId) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		params.put("count", 25);
		params.put("since_id", sinceId);
		if(maxId != Long.MAX_VALUE && sinceId == 1) {
			params.put("max_id", maxId);
		}
		getClient().get(apiUrl, params, handler);
	}

    /**
     * Updates the authenticating user’s current status, also known as Tweeting.
     * @param handler   callback
     * @param status    Text to be posted in the tweet
     */
    public void postTweet(AsyncHttpResponseHandler handler, String status) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
        params.put("status", status);
        getClient().post(apiUrl, params, handler);
	}


}