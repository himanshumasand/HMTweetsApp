package com.codepath.apps.hmtweetsapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.models.Tweet;

import java.util.List;

/**
 * Adapter for the stream of tweets shown in the Timeline Activity
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{


    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }
    
}
