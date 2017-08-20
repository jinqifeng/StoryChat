package com.ids.storychat;

/**
 * Created by JongWN-D on 7/26/2017.
 */

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ids.storychat.db.Constants;
import com.ids.storychat.db.DBHelper;
import com.ids.storychat.emoji.EmojiconEditText;
import com.ids.storychat.emoji.EmojiconsPopup;
import com.ids.storychat.emoji.Emojicon;
import com.ids.storychat.emoji.EmojiconGridView;

import java.util.ArrayList;
import android.content.Intent;


public class storyCreateActivity extends AppCompatActivity implements View.OnClickListener  {

    private ImageButton mBtAddUser;
    private Button mBtCreateUser;
    private Button mBtUser1;
    private Button mBtUser2;
    private Button mBtSendMsg;
    private Button mBtEmoji;
    private Intent intent;
    ArrayList<storyContents> storyCnt;

    private EmojiconEditText emojiconEditText;
    private View rootView;
   // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
    private EmojiconsPopup popup;

    private static int userNumber=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_create);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        storyCnt = new ArrayList<storyContents>();

        mBtAddUser = (ImageButton) findViewById(R.id.addUserButton);
        mBtAddUser.setOnClickListener(this);

        mBtCreateUser = (Button) findViewById(R.id.createUser);
        mBtCreateUser.setOnClickListener(this);

        mBtUser1 = (Button) findViewById(R.id.user1Button);
        mBtUser1.setOnClickListener(this);

        mBtUser2 = (Button) findViewById(R.id.user2Button);
        mBtUser2.setOnClickListener(this);

        mBtSendMsg = (Button) findViewById(R.id.previewButton);
        mBtSendMsg.setOnClickListener(this);

        mBtEmoji = (Button) findViewById(R.id.addEmoji);
        mBtEmoji.setOnClickListener(this);

        mBtEmoji = (Button) findViewById(R.id.addPhoto);
        mBtEmoji.setOnClickListener(this);


        emojiconEditText = (EmojiconEditText) findViewById(R.id.messageEditText);
        rootView = findViewById(R.id.root_view);

      //  emojiButton = (ImageView) findViewById(R.id.emoji_btn);
      //  submitButton = (ImageView) findViewById(R.id.submit_btn);

        popup = new EmojiconsPopup(rootView, this);
        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
       //         changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon == null) {
                    return;
                }

                int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if (start < 0) {
                    emojiconEditText.append(emojicon.getEmoji());

                } else {
                    emojiconEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }

            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojiconEditText.dispatchKeyEvent(event);
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onClick(View v) {
        // default method for handling onClick Events..
        switch (v.getId()) {

            case R.id.addUserButton:
                addUserName();
                break;

            case R.id.createUser:
                createUserName();
                break;
            case R.id.user1Button:
                inputText(1);
                break;
            case R.id.user2Button:
                inputText(2);
                break;
            case R.id.previewButton:
                previewStory();
                break;

            case R.id.addEmoji:
                addEmoji();
                break;

            case R.id.addPhoto:
                addPhoto();// do your code
                break;

            default:
                break;
        }
    }

    private void addPhoto(){

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri image = data.getData();
        ImageView ivGif = (ImageView) findViewById(R.id.ivImg);
        Glide.with(this)
             .load(image)
             .asBitmap()
             .thumbnail(0.5f)
             .centerCrop()
             .placeholder(R.drawable.ic_loading_thumb)
             .diskCacheStrategy(DiskCacheStrategy.ALL)
             .into(ivGif);
        TextView t = (TextView) findViewById(R.id.imagesource);
        t.setText(image.toString());
    }

    private void addEmoji(){


        if (!popup.isShowing()) {

            //If keyboard is visible, simply show the emoji popup
            if (popup.isKeyBoardOpen()) {
                popup.showAtBottom();
              //  changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
            }

            //else, open the text keyboard first and immediately after that show the emoji popup
            else {
                emojiconEditText.setFocusableInTouchMode(true);
                emojiconEditText.requestFocus();
                popup.showAtBottomPending();
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
              //  changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
            }
        }

        //If popup is showing, simply dismiss it to show the undelying text keyboard
        else {
            popup.dismiss();
        }
        // To toggle between text keyboard and emoji keyboard keyboard(Popup)


    }
   /*
    }*/

    private void previewStory(){

       // Intent intent = new Intent(this,storyViewActivity.class);
       // intent.putParcelableArrayListExtra("story_contents", (ArrayList <storyContents>) storyCnt);
        if(intent==null)
        intent = new Intent(this,storyViewActivity.class);

        startActivity(intent);

    }

    private void inputText(int no){


        TextView txtView = (TextView) findViewById(R.id.messageUserName);
        EditText txt = (EditText) findViewById(R.id.messageEditText);
        ImageView im = (ImageView) findViewById(R.id.ivImg);
        TextView t = (TextView) findViewById(R.id.imagesource);
        String str = txt.getText().toString();
        String str1 = t.getText().toString();

        if(!txt.isEnabled()){
            txt.setEnabled(true);
            txt.setHint(" ");
            txt.setText(" ");
            im.setImageDrawable(null);
        } else if((!str.equals(" ")) && (!str1.equals(" "))){

           // storyContents itm = new storyContents(txtView.getText().toString(),Integer.toString(txtView.getCurrentTextColor()), txt.getText().toString(),t.getText().toString());
           // DBHelper db=new DBHelper(this);
            SQLiteDatabase datab=openOrCreateDatabase("x_DB", Context.MODE_PRIVATE, null);
            //OPEN DB
            //db.openDB();
            //COMMIT
            //datab.execSQL("DELETE TABLE  a_TB;");
            datab.execSQL("CREATE TABLE IF NOT EXISTS q_TB(name TEXT DEFAULT ' ',words TEXT DEFAULT ' ',url TEXT,clr Integer DEFAULT 0);");

            datab.execSQL("INSERT INTO q_TB(name, words, url, clr) VALUES('"+txtView.getText().toString()+"','"+txt.getText().toString()+"','"+t.getText().toString()+"','"+txtView.getCurrentTextColor()+"');");

            datab.close();


        //    if(result>0) {
                txt.setText(" ");
                txt.setHint(" ");
                im.setImageDrawable(null);
       /*     }else{
                Toast toast=Toast.makeText(getApplicationContext(),"Don,t use db!",Toast.LENGTH_SHORT);
                toast.setMargin(50,180);
                toast.show();
            }
           // db.closeDB();*/
        }

        if(no==1){
            Button mBtUser = (Button) findViewById(R.id.user1Button);
            txtView.setText(mBtUser.getText().toString());
            txtView.setTextColor(Color.parseColor("#ffffff"));
            txtView.setGravity(Gravity.LEFT);
            LinearLayout ll = (LinearLayout) findViewById(R.id.messageEditText_bubble);
          //  ll.setBackgroundResource(R.drawable.bubble1);

        }else {
            Button mBtUser = (Button) findViewById(R.id.user2Button);
            txtView.setText(mBtUser.getText().toString());
            txtView.setTextColor(Color.parseColor("#6e3e0b"));
            txtView.setGravity(Gravity.RIGHT);
            LinearLayout ll = (LinearLayout) findViewById(R.id.messageEditText_bubble);
         //   ll.setBackgroundResource(R.drawable.bubble2);

        }


        Button imBtn = (Button) findViewById(R.id.previewButton);
        imBtn.setEnabled(true);
        imBtn.setAlpha(1);

        imBtn = (Button) findViewById(R.id.addEmoji);
        imBtn.setEnabled(true);
        imBtn.setBackgroundResource(R.drawable.emoji1);

        imBtn = (Button) findViewById(R.id.addPhoto);
        imBtn.setEnabled(true);
        imBtn.setAlpha(1);

    }


    private void addUserName() {

        LinearLayout ll = (LinearLayout) findViewById(R.id.form2);
        if(ll.isShown()) {
            ll.setVisibility(View.INVISIBLE);
        }else{
            ll.setVisibility(View.VISIBLE);
        }

        ll = (LinearLayout) findViewById(R.id.edit);
        ll.setVisibility(View.INVISIBLE);

        Button imBtn = (Button) findViewById(R.id.previewButton);
        imBtn.setAlpha((float) 0.3);
        imBtn.setEnabled(false);

        imBtn = (Button) findViewById(R.id.addEmoji);
        imBtn.setEnabled(false);
        imBtn.setBackgroundResource(R.drawable.smiley);
        if (popup.isShowing()) {
            popup.dismiss();
        }

        imBtn = (Button) findViewById(R.id.addPhoto);
        imBtn.setEnabled(false);
        imBtn.setAlpha((float) 0.3);

    }
    private void createUserName() {
        Button mBtUser;
        EditText txt;

        txt = (EditText) findViewById(R.id.userEditText);
        String name = txt.getText().toString();
        /* first check whether textview is empty or not*/
        if(name.isEmpty()){
            Toast toast=Toast.makeText(getApplicationContext(),"Please Input User Name!",Toast.LENGTH_SHORT);
            toast.setMargin(50,180);
            toast.show();
            return;
        }
        if(userNumber==0) {
            mBtUser = (Button) findViewById(R.id.user1Button);
            mBtUser.setVisibility(View.VISIBLE);
            userNumber++;
        }else{
            mBtUser = (Button) findViewById(R.id.user2Button);
            mBtUser.setVisibility(View.VISIBLE);
            userNumber--;
        }


        mBtUser.setText((CharSequence) name);
        txt.setText("");

        LinearLayout ll = (LinearLayout) findViewById(R.id.edit);
        ll.setVisibility(View.VISIBLE);

        ll = (LinearLayout) findViewById(R.id.form2);
        ll.setVisibility(View.INVISIBLE);

        txt = (EditText) findViewById(R.id.messageEditText);
        txt.setHint("Please Selcet User!");

    }
}