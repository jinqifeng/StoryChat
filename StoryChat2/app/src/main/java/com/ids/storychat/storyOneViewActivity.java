package com.ids.storychat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

        datab=openOrCreateDatabase("y_DB", Context.MODE_PRIVATE, null);
        datab.execSQL("CREATE TABLE IF NOT EXISTS b_TB(id Integer primary key AUTOINCREMENT DEFAULT 0,name TEXT DEFAULT ' ',words TEXT DEFAULT ' ',url TEXT,clr Integer DEFAULT 0);");
        res = datab.rawQuery("SELECT * FROM b_TB ",null);

        res.moveToFirst();
        if(res.moveToNext())
        {
            Integer ikd = res.getInt(0);
            String name = res.getString(1);
            String words = res.getString(2);
            String url = res.getString(3);
            Integer clr = res.getInt(4);

            storyContents p = new storyContents(name, words, url, clr);
            //ADD TO ARRAYLIS
            story_view.add(p);

            position_recycle++;
        }
        // datab.close();
        rvStorys = (RecyclerView) findViewById(R.id.rvStory_one);

        // Create adapter passing in the sample user data
        storyContentsAdapter adapter = new storyContentsAdapter(this,story_view);
        // Attach the adapter to the recyclerview to populate items
        rvStorys.setAdapter(adapter);
        // Set layout manager to position the items
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
            Integer ikd = res.getInt(0);
            String name = res.getString(1);
            String words = res.getString(2);
            String url = res.getString(3);
            Integer clr = res.getInt(4);

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
