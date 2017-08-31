package com.jwn.storychat;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

@Layout(R.layout.tinder_card_view)
public class TinderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameDateTxt)
    private TextView nameDateTxt;

    @View(R.id.titleNameTxt)
    private TextView titleNameTxt;

    private story mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    public TinderCard(Context context, story profile, SwipePlaceHolderView swipeView) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved(){
        Glide.with(mContext).load(mProfile.getPhoto()).into(profileImageView);
        nameDateTxt.setText(mProfile.getAuthor() + ", " + mProfile.getDate());
        titleNameTxt.setText(mProfile.getTitle());
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        String t1 = titleNameTxt.getText().toString();
        Intent it = new Intent(mContext.getApplicationContext(), storyOneViewActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra("title",t1);
        mContext.getApplicationContext().startActivity(it);
        Log.d("EVENT", "onSwipedAccepted");
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }
}