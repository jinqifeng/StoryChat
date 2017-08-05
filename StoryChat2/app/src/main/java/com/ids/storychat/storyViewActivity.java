package com.ids.storychat;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.database.Cursor;
import com.ids.storychat.db.DBHelper;

import java.util.ArrayList;
/**
 * Created by JongWN-D on 7/24/2017.
 */

public class storyViewActivity extends AppCompatActivity implements View.OnClickListener {


    ArrayList<storyContents> story_view;
    private RecyclerView rvStorys;
    Button mBtStoryPublish;
    Button mBtBack;
    Button mBtNext;
    private RelativeLayout relativeLayout;
    private PopupWindow popupWindow;
    SQLiteDatabase datab;
    Cursor res;
    Integer position_recycle = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        story_view = new ArrayList<storyContents>();
     //   Intent intent = getIntent();
      //  story_view =  intent.getParcelableArrayListExtra("story_view");


        //DBHelper db=new DBHelper(this);
       // storyContents p = db.getContents(1);
            //ADD TO ARRAYLIS
       // story_view.add(p);
        //story_view = db.getAllCotents();
        Integer id = 1;
        datab=openOrCreateDatabase("y_DB", Context.MODE_PRIVATE, null);

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

        mBtStoryPublish = (Button) findViewById(R.id.publish_button);
        mBtStoryPublish.setOnClickListener(this);
        mBtBack= (Button) findViewById(R.id.view_backbutton);
        mBtBack.setOnClickListener(this);
        mBtNext= (Button) findViewById(R.id.next_view);
        mBtNext.setOnClickListener(this);



    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.publish_button:
                publish();
                break;
            case R.id.view_backbutton:

               super.onBackPressed();

                break;
            case R.id.next_view:

                onNext();

                break;
        }
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    /*    Log.i(TAG, "onSaveInstanceState");

        final EditText textBox =
                (EditText) findViewById(R.id.editText1);
        CharSequence userText = textBox.getText();
        outState.putCharSequence("savedText", userText);*/
        Integer cusor = res.getPosition();
        outState.putInt("cusor",cusor);

    }
    protected void onRestoreInstanceState(Bundle savedState) {
     /*   Log.i(TAG, "onRestoreInstanceState");

        final EditText textBox =
                (EditText) findViewById(R.id.editText1);

        CharSequence userText =
                savedState.getCharSequence("savedText");

        textBox.setText(userText);*/
        Integer id = savedState.getInt("cusor");
        Integer k=0;
        res.moveToFirst();
        while(k<=id)
        {

            Integer ikd = res.getInt(0);
            String name = res.getString(1);
            String words = res.getString(2);
            String url = res.getString(3);
            Integer clr = res.getInt(4);

            storyContents p = new storyContents(name, words, url, clr);
            //ADD TO ARRAYLIS
            story_view.add(p);
            res.moveToNext();
            k++;
        }
        // datab.close();
        rvStorys = (RecyclerView) findViewById(R.id.storycnt);

        // Create adapter passing in the sample user data
        storyContentsAdapter adapter = new storyContentsAdapter(this,story_view);
        // Attach the adapter to the recyclerview to populate items
        rvStorys.setAdapter(adapter);
        // Set layout manager to position the items
        rvStorys.setLayoutManager(new LinearLayoutManager(this));

        rvStorys.setItemAnimator(new DefaultItemAnimator());
        rvStorys.setHasFixedSize(true);
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
    public void publish(){
        relativeLayout = (RelativeLayout) findViewById(R.id.view_layout);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.publish, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 840, 100);
        final Button btnOpenPopup1 = (Button) popupView.findViewById(R.id.button);
        btnOpenPopup1.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
            }
        });
    }

}
