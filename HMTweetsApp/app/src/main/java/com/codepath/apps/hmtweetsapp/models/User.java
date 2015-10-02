package com.codepath.apps.hmtweetsapp.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model that holds all information of a user of a single tweet
 */
public class User {

    long id;
    String name;
    String screenName;
    String profileImageUrl;

    public long getId() {
        return id;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getName() {
        return name;
    }

    /**
     * Deserialize the JSON object and create a User object
     * @param jsonObject    User part of the JSON object returned by the api for a single tweet
     * @return User         object used by the Tweet object
     */
    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();

        try {
            user.id = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
