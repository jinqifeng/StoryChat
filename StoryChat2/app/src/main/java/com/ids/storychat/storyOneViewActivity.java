package com.ids.storychat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.ids.storychat.R.layout.story_view;

/**
 * Created by JongWN-D on 8/4/2017.
 */

public class storyOneViewActivity extends AppCompatActivity implements OnClickListener{

    Button mBtBack;
    Button mBtNext;
    Cursor res;
    ArrayList<storyContents> story_view;
    private RecyclerView rvStorys;
    Integer position_recycle = 0;
    SQLiteDatabase datab;
    FirebaseDatabase database;
    storyContentsAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_one_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        story_view = new ArrayList<storyContents>();

        mBtBack = (Button) findViewById(R.id.button3);
        mBtNext = (Button) findViewById(R.id.button2);
        mBtBack.setOnClickListener(this);
        mBtNext.setOnClickListener(this);

        rvStorys = (RecyclerView) findViewById(R.id.rvStory_one);

        // Create adapter passing in the sample user data
        adapter = new storyContentsAdapter(this,story_view);
        // Attach the adapter to the recyclerview to populate items
        rvStorys.setAdapter(adapter);
        // Set layout manager to position the items

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("story").child("ttt");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        Log.d("jmrTAG", "onChildAdded:" + postSnapshot.getKey());
                        storyContents chat = postSnapshot.getValue(storyContents.class);
                        story_view.add(chat);
                        rvStorys.scrollToPosition(story_view.size()-1);
                        adapter.notifyItemInserted(story_view.size() - 1);
                    }

                } else {
                    Log.e("ddddd", "Not found: " );
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // Attach the layout manager to the recycler view
        rvStorys.setLayoutManager(gridLayoutManager);

        rvStorys.setItemAnimator(new DefaultItemAnimator());
        rvStorys.setHasFixedSize(true);


    }
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.button3:

                super.onBackPressed();

                break;
            case R.id.button2:

                onNext();

                break;
        }
    }
    public void onNext(){


        if(res.moveToNext())
        {

            String name = res.getString(0);
            String words = res.getString(1);
            String url = res.getString(2);
            Integer clr = res.getInt(3);

            storyContents p = new storyContents(name, words, url, clr);
            //ADD TO ARRAYLIS
            story_view.add(p);

            storyContentsAdapter adapter = new storyContentsAdapter(this,story_view);
            // Attach the adapter to the recyclerview to populate items
            rvStorys.setAdapter(adapter);
            StaggeredGridLayoutManager gridLayoutManager =
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            // Attach the layout manager to the recycler view
            rvStorys.setLayoutManager(gridLayoutManager);

            rvStorys.setItemAnimator(new DefaultItemAnimator());
            rvStorys.setHasFixedSize(true);
            rvStorys.scrollToPosition(position_recycle);
            rvStorys.refreshDrawableState();
            position_recycle++;

        }
    }
}
