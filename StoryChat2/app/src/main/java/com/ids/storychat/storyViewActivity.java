package com.ids.storychat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.database.Cursor;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;
import com.ids.storychat.db.DBHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import static com.ids.storychat.R.id.addPhoto;
import static java.security.AccessController.getContext;

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
    Bitmap bitmap;
    private StorageReference mStorageRef;
    public static final String PREFS_NAME = "PrefsFile1";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        story_view = new ArrayList<storyContents>();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Integer cusor_num = settings.getInt("cusor", 0);

        datab=openOrCreateDatabase("y_DB", Context.MODE_PRIVATE, null);
        datab.execSQL("CREATE TABLE IF NOT EXISTS b_TB(id Integer primary key AUTOINCREMENT DEFAULT 0,name TEXT DEFAULT ' ',words TEXT DEFAULT ' ',url TEXT,clr Integer DEFAULT 0);");
        res = datab.rawQuery("SELECT * FROM b_TB ",null);



        Integer k=0;
        res.moveToFirst();
        while(k<=cusor_num && res.moveToNext())
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

  /*      } else {

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

        }*/
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


                onBackPressed();

                break;
            case R.id.next_view:

                onNext();

                break;
        }
    }
    @Override
    public void onBackPressed(){


        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        Integer cusor = res.getPosition();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("cusor", cusor);

        // Commit the edits!
        editor.commit();
        res = null;
        datab.close();
        story_view.clear();
        super.onBackPressed();
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);
        EditText title = (EditText) popupView.findViewById(R.id.editTextTitle);
        final String titstr = title.getText().toString();
        EditText author = (EditText) popupView.findViewById(R.id.editTextAuthor);
        final String autstr = author.getText().toString();
        EditText date = (EditText) popupView.findViewById(R.id.editDate);
        final String datestr = date.getText().toString();
        Spinner categr = (Spinner)popupView.findViewById(R.id.spinner);
        categr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected (AdapterView < ? > arg0, View arg1,
                                        int pos, long id){

                String workRequestType = arg0.getItemAtPosition(pos)
                        .toString();

                if (pos != 0)
                    Toast.makeText(getApplicationContext(), "dsds",
                            Toast.LENGTH_LONG).show();
            }


            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        String catstr = categr.getSelectedItem().toString();
        final Button btnOpenPopup1 = (Button) popupView.findViewById(R.id.button);



        btnOpenPopup1.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("story").child(titstr);

                Hashtable<String,String> summary=new Hashtable<String,String>();
                summary.put("personname","hhh");
                summary.put("conversation","jkjhkl");
                summary.put("url","hhhhh");
                myRef.setValue(summary);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                mStorageRef = storage.getReference();

                StorageReference mountainsRef = mStorageRef.child("cover").child(titstr+".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Log.d("Uir", "here-------3");
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                Log.d("Uir", "here-------4");
                byte[] data = baos.toByteArray();
                Log.d("Uir", "here-------5");
                UploadTask uploadTask = mountainsRef.putBytes(data);
                Log.d("Uir", "here-------6");
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d("Uir", String.valueOf(downloadUrl));
                        String photoUri=String.valueOf(downloadUrl);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("search").child(titstr);

                        Hashtable<String,String> summary=new Hashtable<String,String>();
                        summary.put("author",autstr);
                        summary.put("date",datestr);
                        summary.put("photo",photoUri);
                        myRef.setValue(summary);
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String s=dataSnapshot.getValue().toString();
                                Log.d("Profile",s);
                                if(dataSnapshot !=null){
                                    Toast.makeText(getApplicationContext(), "Ok, Photo Upload", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });



                popupWindow.dismiss();
            }
        });

        ImageView cover = (ImageView) popupView.findViewById(R.id.coverImage);
        cover.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                addPhoto();
            }
        });
    }
    private void addPhoto(){

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri image = data.getData();
        ImageView ivGif = (ImageView) findViewById(R.id.coverImage);
        Glide.with(this)
                .load(image)
                .asBitmap()
                .thumbnail(0.5f)
                .centerCrop()
                .placeholder(R.drawable.ic_loading_thumb)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivGif);
        try {
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
