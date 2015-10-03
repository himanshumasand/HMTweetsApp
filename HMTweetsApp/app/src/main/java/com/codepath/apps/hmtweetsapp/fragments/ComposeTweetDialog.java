package com.codepath.apps.hmtweetsapp.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.hmtweetsapp.R;
import com.codepath.apps.hmtweetsapp.TwitterApplication;
import com.codepath.apps.hmtweetsapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;


public class ComposeTweetDialog extends DialogFragment implements View.OnClickListener {

    private ImageView ivUserPic;
    private ImageButton ibClose;
    private EditText etTweet;
    private Button btTweet;
    private TextView tvCharCount;

    public ComposeTweetDialog() {
        // Required empty public constructor
    }

    public interface ComposeTweetDialogListener {
        void onComposeTweetSuccess();
    }

    public static ComposeTweetDialog newInstance() {
        ComposeTweetDialog fragment = new ComposeTweetDialog();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
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
        ibClose = (ImageButton) view.findViewById(R.id.ibClose);
        etTweet = (EditText) view.findViewById(R.id.etTweet);
        btTweet = (Button) view.findViewById(R.id.btTweet);
        tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);

        ibClose.setOnClickListener(this);
        btTweet.setOnClickListener(this);
        tvCharCount.setText("140");

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

            case R.id.ibClose:
                dismiss();
                break;
        }
    }
}
