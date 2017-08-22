package com.jwn.storychat;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.jwn.storychat.MainActivity.PREFS_NAME;

/**
 * Created by JongWN-D on 8/4/2017.
 */

public class storyOneViewActivity extends AppCompatActivity implements OnClickListener{

    Button mBtBack;
    Button mBtNext;
    Cursor res;
    ArrayList<chatContents> story_view;
    ArrayList<chatContents> story_temp;
    private RecyclerView rvStorys;
    Integer position_recycle = 0;
    SQLiteDatabase datab;
    FirebaseDatabase database;
    chatContentsAdapter adapter;
    String titlename;
    Integer read_num;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_one_view);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            titlename = bundle.getString("title");
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        story_view = new ArrayList<chatContents>();
        story_temp = new ArrayList<chatContents>();

        mBtBack = (Button) findViewById(R.id.button3);
        mBtNext = (Button) findViewById(R.id.button2);
        mBtBack.setOnClickListener(this);
        mBtNext.setOnClickListener(this);

        rvStorys = (RecyclerView) findViewById(R.id.rvStory_one);
        // Create adapter passing in the sample user data
        adapter = new chatContentsAdapter(this,story_view);
        // Attach the adapter to the recyclerview to populate items
        rvStorys.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        // Attach the layout manager to the recycler view
        rvStorys.setLayoutManager(new LinearLayoutManager(this));
        rvStorys.setItemAnimator(new DefaultItemAnimator());
        rvStorys.setHasFixedSize(true);


        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("story").child(titlename);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        Log.d("jmrTAG", "onChildAdded:" + postSnapshot.getKey());
                        chatContents chat = postSnapshot.getValue(chatContents.class);
                        story_temp.add(chat);
                      //  adapter.notifyItemInserted(story_temp.size()-1);
                     //   rvStorys.scrollToPosition(story_view.size()-1);

                    }
                    // to read first item
                    chatContents ct = story_temp.get(0);
                    story_view.add(ct);
                    adapter.notifyItemInserted(story_temp.size()-1);

                    //
                } else {
                    Log.e("ddddd", "Not found: " );
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        read_num = settings.getInt("readnum", 0);


    }
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.button3:

                onBackPressed();

                break;
            case R.id.button2:

                onNext();

                break;
        }
    }
    public void onBackPressed(){
        // to register last number so that reader can last item when reader who had read fully  re-open book
        if(read_num>=story_temp.size()){
            read_num = story_temp.size()-1;
        }
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("readnum", read_num);
        editor.commit();
        Boolean isNewStory = false;
        editor.putBoolean("isnewstory", isNewStory);
        editor.commit();

        super.onBackPressed();
    }
    public void onNext(){


        if(read_num<story_temp.size())
        {

            chatContents p = story_temp.get(read_num);
            //ADD TO ARRAYLIS
            story_view.add(p);

            chatContentsAdapter adapter = new chatContentsAdapter(this,story_view);
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
            read_num++;

        }
    }
}
