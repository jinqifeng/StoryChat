package com.ids.storychat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    Uri image;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference mStorageRef;
    String imageurl;

    View popupView;
    String titstr;

    public static final String PREFS_NAME = "Prefs2";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        story_view = new ArrayList<storyContents>();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Integer cusor_num = settings.getInt("cusor", 0);

        datab=openOrCreateDatabase("x_DB", Context.MODE_PRIVATE, null);
        datab.execSQL("CREATE TABLE IF NOT EXISTS q_TB(name TEXT DEFAULT ' ',words TEXT DEFAULT ' ',url TEXT,clr Integer DEFAULT 0);");
        res = datab.rawQuery("SELECT * FROM q_TB ",null);



        Integer k=0;
        res.moveToFirst();
        while(k<=cusor_num && res.moveToNext())
        {

            String name = res.getString(0);
            String words = res.getString(1);
            String url = res.getString(2);
            Integer clr = res.getInt(3);

            storyContents p = new storyContents(name, words, url, clr);
            //ADD TO ARRAYLIS
            story_view.add(p);
            res.moveToNext();
            k++;
        }


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
    public void publish(){
        relativeLayout = (RelativeLayout) findViewById(R.id.view_layout);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.publish, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);

        final Button btnOpenPopup1 = (Button) popupView.findViewById(R.id.button);

        btnOpenPopup1.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                EditText title = (EditText) popupView.findViewById(R.id.editTextTitle);
                titstr = title.getText().toString();
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
                final String catstr = categr.getSelectedItem().toString();
                if(titstr.isEmpty()){
                    Toast toast=Toast.makeText(getApplicationContext(),"Please Input Title!",Toast.LENGTH_SHORT);
                    toast.setMargin(50,180);
                    toast.show();
                    return;
                }

                mStorageRef = storage.getReference();
                StorageReference mountainsRef = mStorageRef.child("cover").child(titstr+".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Log.d("Uir", "here-------3");
                if(image==null)
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.book2);

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

                        DatabaseReference myRef = database.getReference().child("search").child(titstr);

                        Hashtable<String,String> summary=new Hashtable<String,String>();
                        summary.put("author",autstr);
                        summary.put("date",datestr);
                        summary.put("photo",photoUri);
                        summary.put("category",catstr);
                        myRef.setValue(summary);
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String s=dataSnapshot.getValue().toString();
                                Log.d("Profile",s);
                                if(dataSnapshot !=null){
                                    Toast.makeText(getApplicationContext(), "Ok, Publish succeced ", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                Integer l=0;

                while(l<story_view.size()){
                    String str1 = story_view.get(l).getPerson();
                    String str2 = story_view.get(l).getConv();
                    String str3 = story_view.get(l).getUrl();
                    Integer str4 = story_view.get(l).getColor();

                    DatabaseReference myRef2 = database.getReference("story").child(titstr);
                    Hashtable<String,String> summary=new Hashtable<String,String>();
                    summary.put("a_personname",str1);
                    summary.put("b_conversation",str2);


                    if(!str3.equals("d")){

                        StorageReference imageRef = mStorageRef.child("image").child(titstr+".jpg");
                        ByteArrayOutputStream bytestrm = new ByteArrayOutputStream();
                        Log.d("Uir", "here-------3");
                        if(image==null)
                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.book2);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        Log.d("Uir", "here-------4");
                        byte[] data2 = bytestrm.toByteArray();
                        Log.d("Uir", "here-------5");
                        UploadTask upload = imageRef.putBytes(data2);
                        Log.d("Uir", "here-------6");
                        upload.addOnFailureListener(new OnFailureListener() {
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
                                imageurl = photoUri;
                            }
                        });
                    }else{
                        imageurl = "d";
                    }
                    summary.put("c_imageurl",imageurl);
                    summary.put("d_clr",Integer.toString(str4));

                    storyContents cnt = story_view.get(l);
                    cnt.setUrl(imageurl);
                    myRef2.push().setValue(summary);
                    l++;
                }

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
    private void validate(){

    }
    private void addPhoto(){

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        image = data.getData();
        ImageView ivGif = (ImageView) popupView.findViewById(R.id.coverImage);
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
