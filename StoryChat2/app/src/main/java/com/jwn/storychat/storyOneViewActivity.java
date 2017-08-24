package com.jwn.storychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

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
    Integer limit;
    Boolean is_possible_read;
    RelativeLayout relativeLayout;
    View popupView;
    private PopupWindow popupWindow;
    Integer subscrition_level;
    private FirebaseAuth auth;
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
        String prev_read_title = settings.getString("title", " ");
        if(!prev_read_title.equals(titlename)){
            read_num = 0;
        }


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
        editor.commit();
        editor.putString("title",titlename);
        editor.commit();

        super.onBackPressed();
    }
    public void onNext(){

        if(read_num >= limit && !is_possible_read){
            subscript();
        }
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
    public void subscript(){

        relativeLayout = (RelativeLayout) findViewById(R.id.view_layout);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.subscription, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, 1000);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);

        final Button btnOpenPopup1 = (Button) popupView.findViewById(R.id.free);
        final Button btnOpenPopup2 = (Button) popupView.findViewById(R.id.month);
        final Button btnOpenPopup3 = (Button) popupView.findViewById(R.id.year);
        final TextView tvLogin = (TextView) popupView.findViewById(R.id.textView3);

        btnOpenPopup1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                subscrition_level = 0;
                openRegister();
            }

        });
        btnOpenPopup2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                subscrition_level = 1;
                openRegister();
            }


        });
        btnOpenPopup3.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                subscrition_level = 2;
                openRegister();
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                subscrition_level = 2;
                openLogin();
            }
        });

    }
    public void openRegister(){
        relativeLayout = (RelativeLayout) findViewById(R.id.view_layout);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.register, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, 1000);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);

        Button btnOpenPopup1 = (Button) popupView.findViewById(R.id.submit);
        Button btnOpenPopup2 = (Button) popupView.findViewById(R.id.cancel);
        auth = FirebaseAuth.getInstance();



        final EditText inputEmail = (EditText) findViewById(R.id.emailaddress);
        final EditText inputPassword = (EditText) findViewById(R.id.password);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");


        btnOpenPopup1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Get Firebase auth instance

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(storyOneViewActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(storyOneViewActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(storyOneViewActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    Intent intent = new Intent(storyOneViewActivity.this, paymentActivity.class);
                                    intent.putExtra("subscriptionlevel",subscrition_level);
                                    startActivity(intent);

                                }
                            }
                        });
            }



        });
        btnOpenPopup2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
            }
        });
    }
    public void openLogin(){
        relativeLayout = (RelativeLayout) findViewById(R.id.view_layout);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.login, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, 1000);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);

        final Button btnOpenPopup1 = (Button) popupView.findViewById(R.id.submit);
        final Button btnOpenPopup2 = (Button) popupView.findViewById(R.id.cancel);
        final TextView tvResetPassword = (TextView) popupView.findViewById(R.id.tvReset);
        final EditText inputEmail = (EditText) popupView.findViewById(R.id.username);
        final EditText inputPassword = (EditText) popupView.findViewById(R.id.password);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");


        btnOpenPopup1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(storyOneViewActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressDialog.dismiss();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError("please re-input password");
                                    } else {
                                        Toast.makeText(storyOneViewActivity.this, "login failled", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    afterLogin();

                                }
                            }
                        });
            }


        });
        btnOpenPopup2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
            }

        });

        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }

        });
    }
    public  void afterLogin(){
        is_possible_read = true;
        Intent intent = new Intent(storyOneViewActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
