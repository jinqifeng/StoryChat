package com.jwn.storychat;

import android.app.ProgressDialog;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {

    ArrayList<story> storys;
    FirebaseDatabase database;
    storyAdapter adapter;
    private Button mBtCreateStoryActivity;
    public static final String PREFS_NAME = "Prefs";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  ActionBar actionBar = getSupportActionBar();
      //  actionBar.hide();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Integer cusor_num = settings.getInt("cusor", 0);
        if(cusor_num!=0){
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("cusor", 0);
            editor.commit();
        }
        RecyclerView rvStorys = (RecyclerView) findViewById(R.id.storyList);
        storys = new ArrayList<story>();
        // Create adapter passing in the sample user data
        adapter = new storyAdapter(this,storys);
        // Attach the adapter to the recyclerview to populate items
        rvStorys.setAdapter(adapter);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading");
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
                        storys.add(chat);
                        //   rvStorys.scrollToPosition(story_view.size()-1);
                        adapter.notifyItemInserted(storys.size() - 1);
                    }
                    progressDialog.dismiss();

                } else {
                    Log.e("ddddd", "Not found: " );
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


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
