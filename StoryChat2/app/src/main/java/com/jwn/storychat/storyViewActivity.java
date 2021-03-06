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
import java.util.ArrayList;
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
    String user1,user2;
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

    InputStream is;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        user1 = intent.getExtras().getString("user1");
        user2 = intent.getExtras().getString("user2");


        b = false;
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        story_view = new ArrayList<chatContents>();
        imageurl2 = new ArrayList<String>();
        story_temp = new ArrayList<chatContents>();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        cusor_num = settings.getInt("cusor", 0);

        datab=openOrCreateDatabase("S_DB", Context.MODE_PRIVATE, null);
        res = datab.rawQuery("SELECT * FROM chattable ",null);

        Integer k=0;
       // res.moveToFirst();
        while(k<=cusor_num && res.moveToNext())
        {
            Integer clr = res.getInt(0);
            String words = res.getString(1);
            String name = res.getString(2);
            String url = res.getString(3);

            chatContents p = new chatContents(clr,words,name,url);
            //ADD TO ARRAYLIS
            story_view.add(p);
            res.moveToNext();
            k++;
        }


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
        Button mBtBack= (Button) findViewById(R.id.view_backbutton);
        mBtBack.setOnClickListener(this);
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
            case R.id.view_backbutton:
                onBackPressed();
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
            Integer clr = res.getInt(0);
            String words = res.getString(1);
            String name = res.getString(2);
            String url = res.getString(3);


            chatContents p = new chatContents(clr,words,name,url);
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
        Spinner categr = (Spinner) popupView.findViewById(R.id.spinner);
        categr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {

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
        if (titstr.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please Input Title!", Toast.LENGTH_SHORT);
            toast.setMargin(50, 180);
            toast.show();
            return ;
        }


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
                summary.put("category", catstr);
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        EditText title = (EditText) popupView.findViewById(R.id.editTextTitle);
        titstr = title.getText().toString();// in order to do first contentupload.
        mStorageRef = storage.getReference();

        contentUpload();
        titleUpload(); // upload title after success uploading content



        Toast.makeText(getApplicationContext(), "Ok, Publish succeced ", Toast.LENGTH_SHORT).show();
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        b = true;
        cusor_num = 0;
        datab.execSQL("DROP TABLE IF EXISTS chattable");
        story_view.clear();
        getApplicationContext().deleteDatabase("S_DB");
        popupWindow.dismiss();

    }
    public void contentUpload() {

        l = 0;
        myRef2 = database.getReference("story").child(titstr);
        while (l < story_view.size()) {

            String str3 = story_view.get(l).getUrl();
            Uri imageuri = Uri.parse(str3);

            chatContents cnt = story_view.get(l);
           // cnt.setUrl("d");
            DatabaseReference child = myRef2.push();
            child.setValue(cnt);
            String key = child.getKey();
            if (!str3.equals("d")) {

                imageurl2.add(key);
                story_temp.add(story_view.get(l));
                StorageReference imageRef = mStorageRef.child("image").child(titstr).child(Integer.toString(l) + ".jpg");
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
                        chatContents tmp = story_temp.get(index);
                        tmp.setUrl(photoUri);
                        String read_key = imageurl2.get(index);
                        myRef2.child(read_key).setValue(tmp);
                        index++;
                        if(imageurl2.size()==index){
                            progressDialog.dismiss();
                        }
                    }
                });
            }



            l++;
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
