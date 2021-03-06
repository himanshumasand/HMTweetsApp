package com.codepath.apps.hmtweetsapp.fragments;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.TwitterApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.Header;
import org.json.JSONObject;


/**
 * Fragment where a user can compose a new tweet
 */
public class ComposeTweetDialog extends DialogFragment implements View.OnClickListener {

    private ImageView ivUserPic;
    private ImageView ivClose;
    private EditText etTweet;
    private Button btTweet;
    private TextView tvCharCount;

    /**
     * Required empty public constructor
     */
    public ComposeTweetDialog() {}

    public interface ComposeTweetDialogListener {
        void onComposeTweetSuccess();
    }

    public static ComposeTweetDialog newInstance(String profilePicUrl, String replyText) {
        ComposeTweetDialog fragment = new ComposeTweetDialog();
        Bundle args = new Bundle();
        args.putString("url", profilePicUrl);
        args.putString("replyText", replyText);
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compose_tweet, container, false);

        ivUserPic = (ImageView) view.findViewById(R.id.ivUserPic);
        ivClose = (ImageView) view.findViewById(R.id.ivClose);
        etTweet = (EditText) view.findViewById(R.id.etTweet);
        btTweet = (Button) view.findViewById(R.id.btTweet);
        tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);

        if(getArguments().getString("url") != "") {
            Picasso.with(getActivity()).load(getArguments().getString("url")).into(ivUserPic);
            Picasso.with(getActivity()).load(getArguments().getString("url")).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getActivity().getResources(), bitmap);
                    dr.setCornerRadius(2.0f);
                    ivUserPic.setImageDrawable(dr);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

        etTweet.setText(getArguments().getString("replyText"));
        etTweet.setSelection(etTweet.getText().length());
        tvCharCount.setText(String.valueOf(140 - etTweet.getText().length()));
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharCount.setText(String.valueOf(140 - s.length()));
            }
        });
        ivClose.setOnClickListener(this);
        btTweet.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btTweet:
                final ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
                TwitterApplication.getTwitterClient().postTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponse) {
                        Log.d("DEBUG", jsonResponse.toString());
                        listener.onComposeTweetSuccess();
                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", errorResponse.toString());
                        Toast.makeText(getActivity(), "ERROR: Unable to post tweet!", Toast.LENGTH_SHORT).show();
                    }
                }, etTweet.getText().toString());

                break;

            case R.id.ivClose:
                dismiss();
                break;
        }
    }
}
