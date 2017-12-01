package com.jwn.storychat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import static com.jwn.storychat.MainActivity.PREFS_NAME;


/**
 * Created by JongWN-D on 7/24/2017.
 */

public class storyViewActivity extends AppCompatActivity implements View.OnClickListener {


    ArrayList<chatContents> story_view;
    ArrayList<chatContents> story_temp;
    private RecyclerView rvStorys;
    String user1,user2,color1,color2;
    private RelativeLayout relativeLayout;
    private PopupWindow popupWindow;
    View popupView;
    SQLiteDatabase datab;
    Cursor res;
    Integer position_recycle = 0;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference mStorageRef;
    DatabaseReference myRef2;
    Integer l;
    Integer index=0;
    List<String> imageurl2;
    ProgressDialog progressDialog;
    String titstr;
    Integer cusor_num;
    Boolean b ;

  //  Spinner categr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        user1 = intent.getExtras().getString("user1");
        user2 = intent.getExtras().getString("user2");
        color1 = intent.getExtras().getString("color1");
        color2 = intent.getExtras().getString("color2");


        b = false;
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        story_view = new ArrayList<chatContents>();
        imageurl2 = new ArrayList<String>();
        story_temp = new ArrayList<chatContents>();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        cusor_num = settings.getInt("cusor", 0);

        datab=openOrCreateDatabase("C_DB", Context.MODE_PRIVATE, null);
        res = datab.rawQuery("SELECT * FROM chat_table ",null);

        Integer k=0;
        res.moveToFirst();

        do{
            String name = res.getString(0);
            String words = res.getString(1);
            Integer clr = res.getInt(2);
            String url = res.getString(3);

            chatContents p = new chatContents(name,words,url);
            //ADD TO ARRAYLIS
            story_view.add(p);
            //res.moveToNext();
            k++;
        }while(k<=cusor_num && res.moveToNext());


        rvStorys = (RecyclerView) findViewById(R.id.storycnt);
        // Create adapter passing in the sample user data
        chatContentsAdapter adapter = new chatContentsAdapter(this,story_view);
        // Attach the adapter to the recyclerview to populate items
        rvStorys.setAdapter(adapter);
        rvStorys.setLayoutManager(new LinearLayoutManager(this));

        rvStorys.setItemAnimator(new DefaultItemAnimator());
        rvStorys.setHasFixedSize(true);

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button mBtStoryPublish = (Button) findViewById(R.id.publish_button);
        mBtStoryPublish.setOnClickListener(this);
     //   Button mBtBack= (Button) findViewById(R.id.view_backbutton);
     //   mBtBack.setOnClickListener(this);
        Button mBtNext= (Button) findViewById(R.id.next_view);
        mBtNext.setOnClickListener(this);
        Button mBtSave= (Button) findViewById(R.id.view_save);
        mBtSave.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.publish_button:
                publish();
                break;

            case R.id.next_view:
                onNext();
                break;
            case R.id.view_save:
                onSave();
                break;
            default:
                break;
        }
    }
    public void onSave(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user1", user1);
        editor.commit();
        editor.putString("user2", user2);
        editor.commit();
        editor.putString("color1", color1);
        editor.commit();
        editor.putString("color2", color2);
        editor.commit();
       // editor.putInt("user3", cusor_num);
        editor.commit();
    }
    @Override
    public void onBackPressed(){

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        Integer cusor = res.getPosition();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("cusor", cusor_num);
        editor.commit();

        if(res!=null)
         res = null;
        datab.close();
        if(!story_view.isEmpty())
        story_view.clear();
        if(b){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        super.onBackPressed();
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    public void onNext(){
        if(story_view.isEmpty()){
            Toast.makeText(this, "There is not story to view", Toast.LENGTH_SHORT).show();
            return;
        }

        if(res.moveToNext())
        {
            String name = res.getString(0);
            String words = res.getString(1);
            Integer clr = res.getInt(2);
            String url = res.getString(3);

            chatContents p = new chatContents(name,words,url);
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
            cusor_num++;
        }
    }

    public void titleUpload(){

        EditText author = (EditText) popupView.findViewById(R.id.editTextAuthor);
        final String autstr = author.getText().toString();
        EditText date = (EditText) popupView.findViewById(R.id.editDate);
        final String datestr = date.getText().toString();

    //    final String catstr = categr.getSelectedItem().toString();
        if (titstr.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please Input Title!", Toast.LENGTH_SHORT);
            toast.setMargin(50, 180);
            toast.show();
            return ;
        }
        if (autstr.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please Input Auther name!", Toast.LENGTH_SHORT);
            toast.setMargin(50, 180);
            toast.show();
            return ;
        }
        if (datestr.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please Input date!", Toast.LENGTH_SHORT);
            toast.setMargin(50, 180);
            toast.show();
            return ;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        StorageReference mref = mStorageRef.child("cover");
        StorageReference mountainsRef = mref.child(titstr);


        InputStream stream = null;

      /*  if (image == null) {
            //  bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.book2);
            image = Uri.parse("android.resource://com.jwn.storychat/" + R.drawable.book2);

        }*/
        ImageView imv = (ImageView) popupView.findViewById(R.id.coverImage);
        Bitmap bmp =  ((BitmapDrawable)imv.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // UploadTask uploadTask = mountainsRef.putBytes(data);

        mountainsRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                String photoUri = String.valueOf(downloadUrl);

                DatabaseReference myRef = database.getReference().child("search").child(titstr);

                Hashtable<String, String> summary = new Hashtable<String, String>();
                summary.put("author", autstr);
                summary.put("date", datestr);
                summary.put("photo", photoUri);
                summary.put("user_1", user1);
         //       summary.put("user_1_color", color1);
                summary.put("user_2", user2);
         //         summary.put("user_1_color", color2);
                myRef.setValue(summary);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String s = dataSnapshot.getValue().toString();

                        if (dataSnapshot != null) {
                            return ;

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                Toast.makeText(getApplicationContext(), "Wow??, Publish failed. network is busy ", Toast.LENGTH_SHORT).show();
                return ;
            }
        });

    }
    public void upload() {


        EditText title = (EditText) popupView.findViewById(R.id.editTextTitle);
        titstr = title.getText().toString();// in order to do first contentupload.
        mStorageRef = storage.getReference();


        titleUpload(); // upload title after success uploading content
        contentUpload();

    }
    public void contentUpload() {

        l = 0;
        myRef2 = database.getReference("story").child(titstr);

        DatabaseReference child = myRef2.child("conversation");
        res.moveToFirst();
       // while (l < story_view.size()) {
          do{

              String name = res.getString(0);
              String words = res.getString(1);
             // Integer clr = res.getInt(2);
              String url = res.getString(3);
        //      chatContents p = new chatContents(name,words,url);

          //  String str3 = story_view.get(l).getUrl();
              //Uri imageuri = Uri.parse(str3);
              Uri imageuri = Uri.parse(url);

         /*   chatContents cnt = story_view.get(l);

            DatabaseReference child = myRef2.push();
            child.setValue(cnt);
            String key = child.getKey();*/

            //  long now = System.currentTimeMillis();
              String key = Integer.toString(l);
              if(l<10){
                  key = "-event0000"+key;
              }else if(l<100){
                  key = "-event000"+key;
              }else if(l<1000){
                  key = "-event00"+key;
              }else if(l<10000){
                  key = "-event0"+key;
              }else{
                  key = "-event0"+key;
              }


          //  String push_key = myRef2.push().getKey();

              DatabaseReference myRef =  child.child(key);
         //     myRef.setValue(p);
         //     child.setValue(p);
              myRef.child("name").setValue(name);
              myRef.child("speech").setValue(words);
              myRef.child("with_photo").setValue(url);
            if (!url.equals(" ")) {

                imageurl2.add(key);
              //  story_temp.add(p);
                StorageReference imageReftitle = mStorageRef.child("image").child(titstr);
                StorageReference imageRef = imageReftitle.child(key);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageuri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask upload = imageRef.putBytes(data);
                upload.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to publish ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        String photoUri = String.valueOf(downloadUrl);
                       // chatContents tmp = story_temp.get(index);
                       // tmp.setUrl(photoUri);
                        String read_key = imageurl2.get(index);
                        myRef2.child("conversation").child(read_key).child("with_photo").setValue(photoUri);
                        index++;
                        if(imageurl2.size()==index){
                            Toast.makeText(getApplicationContext(), "Ok, Publish succeced ", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.commit();
                            b = true;
                            cusor_num = 0;
                            datab.execSQL("DROP TABLE IF EXISTS chat_table");
                            story_view.clear();
                            getApplicationContext().deleteDatabase("C_DB");
                            popupWindow.dismiss();
                        }
                    }
                });
            }
            l++;
        }while (res.moveToNext());
        if(imageurl2.isEmpty()){
            Toast.makeText(getApplicationContext(), "Ok, Publish succeced ", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

            SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            b = true;
            cusor_num = 0;
            datab.execSQL("DROP TABLE IF EXISTS chat_table");
            story_view.clear();
            getApplicationContext().deleteDatabase("C_DB");
            popupWindow.dismiss();
        }
    }
    public void publish(){
        if(story_view.isEmpty()){
            Toast.makeText(this, "There is not story to publish", Toast.LENGTH_SHORT).show();
            return;
        }
        relativeLayout = (RelativeLayout) findViewById(R.id.view_layout);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.publish, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);

      /*  categr = (Spinner) popupView.findViewById(R.id.spinner);
        categr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });*/
        final Button btnOpenPopup1 = (Button) popupView.findViewById(R.id.button);

        btnOpenPopup1.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                upload();
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
        i.setType("image/*");
        startActivityForResult(i, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       // image = data.getData();
        final Uri urlimg = data.getData();

        ImageView ivGif = (ImageView) popupView.findViewById(R.id.coverImage);
   /*     Glide.with(this)
                .load(urlimg)
                .asBitmap()
                .thumbnail(0.5f)
                .centerCrop()
                .placeholder(R.drawable.ic_loading_thumb)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivGif);*/

        try {
            Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),urlimg);
            ivGif.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
