package com.jwn.storychat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.*;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.FrameLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;


public class MainActivity extends AppCompatActivity {

    ArrayList<story> storys;
    FirebaseDatabase database;
    Button mBtCreateStoryActivity;
  //  storyAdapter adapter;
    public static final String PREFS_NAME = "Prefs";

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Integer cusor_num = settings.getInt("cusor", 0);
        if(cusor_num!=0){
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("cusor", 0);
            editor.commit();
        }
      //  RecyclerView rvStorys = (RecyclerView) findViewById(R.id.storyList);
       // storys = new ArrayList<story>();
        // Create adapter passing in the sample user data
      //  adapter = new storyAdapter(this,storys);
        // Attach the adapter to the recyclerview to populate items
     //   rvStorys.setAdapter(adapter);

        mSwipeView = (SwipePlaceHolderView)findViewById(R.id.swipeView);
        mContext = getApplicationContext();

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

       /* final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading");
        progressDialog.show();*/
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("loading storys...");
        progressDialog.show();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("search");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        String name = postSnapshot.getKey();
                        story chat = postSnapshot.getValue(story.class);
                        chat.setTitle(name);
                        mSwipeView.addView(new TinderCard(mContext, chat, mSwipeView));
                        //storys.add(chat);
                        //   rvStorys.scrollToPosition(story_view.size()-1);
                      //  adapter.notifyItemInserted(storys.size() - 1);
                    }
                }
                progressDialog.dismiss();
                mBtCreateStoryActivity.setVisibility(View.VISIBLE);
                FrameLayout ly = (FrameLayout) findViewById(R.id.background);
                ly.setBackgroundColor(123456);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

/*
        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });
*/
        // Set layout manager to position the items
   //     StaggeredGridLayoutManager gridLayoutManager =
    //            new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
// Attach the layout manager to the recycler view
       // rvStorys.setLayoutManager(gridLayoutManager);


        mBtCreateStoryActivity = (Button) findViewById(R.id.myStory);

        mBtCreateStoryActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchActivity();
            }
        });



    }

    private void launchActivity() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Boolean is_possible_read = settings.getBoolean("is_possible_read", false);
        if(!is_possible_read){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("for",0);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, storyCreateActivity.class);
            startActivity(intent);
        }

    }
}
