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
                user.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
