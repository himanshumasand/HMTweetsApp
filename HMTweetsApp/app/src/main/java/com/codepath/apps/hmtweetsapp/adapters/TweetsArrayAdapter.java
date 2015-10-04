package com.codepath.apps.hmtweetsapp.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.activities.TimelineActivity;
import com.codepath.apps.hmtweetsapp.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for the stream of tweets shown in the Timeline Activity
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private static class ViewHolder {
        ImageView profilePic;
        TextView name;
        TextView screenName;
        TextView time;
        TextView body;
        ImageView tweetImage;

        //Action buttons at the bottom
        ImageView replyIcon;
        ImageView retweetIcon;
        TextView retweetCount;
        ImageView favIcon;
        TextView favCount;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.ivProfilePic);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.screenName = (TextView) convertView.findViewById(R.id.tvScreenName);
            viewHolder.time = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.body = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tweetImage = (ImageView) convertView.findViewById(R.id.ivTweetImage);
            viewHolder.replyIcon = (ImageView) convertView.findViewById(R.id.ivReply);
            viewHolder.retweetIcon = (ImageView) convertView.findViewById(R.id.ivRetweet);
            viewHolder.favIcon = (ImageView) convertView.findViewById(R.id.ivFavorite);
            viewHolder.retweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
            viewHolder.favCount = (TextView) convertView.findViewById(R.id.tvFavCount);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Tweet tweet = getItem(position);

        viewHolder.profilePic.setImageResource(0);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.profilePic);

        viewHolder.name.setText(tweet.getUser().getName());
        viewHolder.screenName.setText("@" + tweet.getUser().getScreenName());

        viewHolder.body.setText(tweet.getBody());

        if(tweet.getImageUrl() != null && tweet.getImageUrl() != "") {
            viewHolder.tweetImage.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(tweet.getImageUrl()).fit().into(viewHolder.tweetImage);
        }
        else {
            viewHolder.tweetImage.setVisibility(View.GONE);
        }

        viewHolder.time.setText(getRelativeTimeAgo(tweet.getTimestamp()));

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        viewHolder.retweetCount.setText(formatter.format(tweet.getRetweetCount()));
        viewHolder.favCount.setText(formatter.format(tweet.getFavoriteCount()));

        viewHolder.replyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TimelineActivity) getContext()).openComposeTweetDialog("@" + tweet.getUser().getScreenName() + " ");
            }
        });

        return convertView;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] words = relativeDate.split("\\s+");
        switch (words[1]) {
            case "second":
            case "seconds":
                relativeDate = words[0] + "s";
                break;
            case "minute":
            case "minutes":
                relativeDate = words[0] + "m";
                break;
            case "hour":
            case "hours":
                relativeDate = words[0] + "h";
                break;
            case "day":
            case "days":
                relativeDate = words[0] + "d";
                break;
            case "week":
            case "weeks":
                relativeDate = words[0] + "w";
                break;
            default:
                relativeDate = "";
                break;
        }
        return relativeDate;
    }
}