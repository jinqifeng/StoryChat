package com.ids.storychat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import java.util.ArrayList;
/**
 * Created by JongWN-D on 7/24/2017.
 */

public class storyViewActivity extends AppCompatActivity implements View.OnClickListener {


    ArrayList<storyContents> story_view;
    private RecyclerView rvStorys;
    Button mBtStoryContents;
    Button mBtBack;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_view);
        story_view = new ArrayList<storyContents>();
        Intent intent = getIntent();
        story_view =  intent.getParcelableArrayListExtra("story_contents");

        rvStorys = (RecyclerView) findViewById(R.id.storycnt);

        // Create adapter passing in the sample user data
        storyContentsAdapter adapter = new storyContentsAdapter(this,story_view);
        // Attach the adapter to the recyclerview to populate items
        rvStorys.setAdapter(adapter);
        // Set layout manager to position the items
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        // Attach the layout manager to the recycler view
        rvStorys.setLayoutManager(new LinearLayoutManager(this));

        rvStorys.setItemAnimator(new DefaultItemAnimator());
        rvStorys.setHasFixedSize(true);

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mBtStoryContents = (Button) findViewById(R.id.contents_view_button);
        mBtStoryContents.setOnClickListener(this);
        mBtBack= (Button) findViewById(R.id.view_backbutton);
        mBtBack.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.contents_view_button:

                break;
            case R.id.view_backbutton:

                super.onBackPressed();

                break;
        }
    }

}
