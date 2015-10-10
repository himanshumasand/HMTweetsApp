package com.codepath.apps.hmtweetsapp.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model that holds all information of a user of a single tweet
 */
@Table(name = "Users")
public class User extends Model{

    @Column(name = "UserId", unique = true)
    long userId;
    @Column(name = "Name")
    String name;
    @Column(name = "ScreenName")
    String screenName;
    @Column(name = "ProfileImageUrl")
    String profileImageUrl;
    @Column(name = "ProfileBackgroundImageUrl")
    String profileBackgroundImageUrl;
    @Column(name = "ProfileBackgroundColor")
    int profileBackgroundColor;
    @Column(name = "NumFollowers")
    int numFollowers;
    @Column(name = "NumFollowing")
    int numFollowing;


    public long getUserId() {
        return userId;
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

    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    public int getProfileBackgroundColor() {
        return profileBackgroundColor;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public int getNumFollowing() {
        return numFollowing;
    }

    public User() {}
    /**
     * Deserialize the JSON object and create a User object
     * @param jsonObject    User part of the JSON object returned by the api for a single tweet
     * @return User         object used by the Tweet object
     */
    public static User fromJSON(JSONObject jsonObject) {
        User user = null;
        try {
            Long uId = jsonObject.getLong("id");
            user = new Select().from(User.class).where("userId = ?", uId).executeSingle();
            if(user == null) {
                user = new User();
                user.userId = jsonObject.getLong("id");
                user.name = jsonObject.getString("name");
                user.screenName = jsonObject.getString("screen_name");
                user.profileImageUrl = jsonObject.getString("profile_image_url");
                if(jsonObject.has("profile_banner_url")) {
                    user.profileBackgroundImageUrl = jsonObject.getString("profile_banner_url");
                } else if(jsonObject.has("profile_background_color")) {
                    user.profileBackgroundColor = jsonObject.getInt("profile_background_color");
                }
                user.numFollowers = jsonObject.getInt("followers_count");
                user.numFollowing = jsonObject.getInt("friends_count");
                user.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
