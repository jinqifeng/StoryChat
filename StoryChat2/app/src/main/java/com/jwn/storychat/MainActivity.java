package com.jwn.storychat;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.*;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    ArrayList<story> storys;
    private Button mBtCreateStoryActivity;
    public static final String PREFS_NAME = "Prefs";
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
        RecyclerView rvStorys = (RecyclerView) findViewById(R.id.storyList);

        // Initialize list
        storys = story.createStoryList(8);
        // Create adapter passing in the sample user data
        storyAdapter adapter = new storyAdapter(this,storys);
        // Attach the adapter to the recyclerview to populate items
        rvStorys.setAdapter(adapter);
        // Set layout manager to position the items
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
// Attach the layout manager to the recycler view
        rvStorys.setLayoutManager(gridLayoutManager);


        mBtCreateStoryActivity = (Button) findViewById(R.id.myStory);

        mBtCreateStoryActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchActivity();
            }
        });

    }

    private void launchActivity() {

        Intent intent = new Intent(this, storyCreateActivity.class);
        startActivity(intent);
    }
}
