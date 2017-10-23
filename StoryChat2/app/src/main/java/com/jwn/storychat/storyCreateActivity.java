package com.jwn.storychat;

/**
 * Created by JongWN-D on 7/26/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jwn.storychat.emoji.EmojiconEditText;
import com.jwn.storychat.emoji.EmojiconsPopup;
import com.jwn.storychat.emoji.Emojicon;
import com.jwn.storychat.emoji.EmojiconGridView;

import java.util.ArrayList;
import android.content.Intent;

import static com.jwn.storychat.MainActivity.PREFS_NAME;
import static java.util.Objects.isNull;


public class storyCreateActivity extends AppCompatActivity implements View.OnClickListener  {


    private Button mBtEmoji;
    SQLiteDatabase datab;



    private EmojiconEditText emojiconEditText;
    private View rootView;
   // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
    private EmojiconsPopup popup;

    private static int userNumber=0;
    private View popupView;
    private PopupWindow popupWindow;
    private int userNum;
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

        init();
        emojiSetting();

    }
    public void init(){


        userNumber = 0;
        datab=openOrCreateDatabase("C_DB", Context.MODE_PRIVATE, null);
        //OPEN DB
        //db.openDB();
        //COMMIT
        //datab.execSQL("DELETE TABLE  a_TB;");
        datab.execSQL("CREATE TABLE IF NOT EXISTS chat_able (name TEXT DEFAULT ' ',words TEXT DEFAULT ' ',url TEXT,clr Integer DEFAULT 0);");
        datab.close();


        ImageButton mBtAddUser = (ImageButton) findViewById(R.id.addUserButton);
        mBtAddUser.setOnClickListener(this);

        Button mBtCreateUser = (Button) findViewById(R.id.createUser);
        mBtCreateUser.setOnClickListener(this);

        Button mBtUser1 = (Button) findViewById(R.id.user1Button);
        mBtUser1.setOnClickListener(this);

        Button mBtUser2 = (Button) findViewById(R.id.user2Button);
        mBtUser2.setOnClickListener(this);

        Button  mBtSendMsg = (Button) findViewById(R.id.previewButton);
        mBtSendMsg.setOnClickListener(this);

 //       Button  mBtUser3 = (Button) findViewById(R.id.commant);
 //       mBtUser3.setOnClickListener(this);

        mBtEmoji = (Button) findViewById(R.id.addEmoji);
        mBtEmoji.setOnClickListener(this);

        Button mBtphoto = (Button) findViewById(R.id.addPhoto);
        mBtphoto.setOnClickListener(this);

        TextView userName = (TextView) findViewById(R.id.messageUserName);
        userName.setOnClickListener(this);

        rootView = findViewById(R.id.root_view);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String user1 = settings.getString("user1", " ");
        String user2 = settings.getString("user2", " ");

        if(!user1.equals(" ")){
            LinearLayout ll = (LinearLayout) findViewById(R.id.edit);
            ll.setVisibility(View.VISIBLE);

            ll = (LinearLayout) findViewById(R.id.form2);
            ll.setVisibility(View.INVISIBLE);

                mBtUser1.setVisibility(View.VISIBLE);
                mBtUser1.setEnabled(true);
                userNumber++;
                mBtUser1.setText(user1);
        //        mBtUser3.setEnabled(true);

        }
        if(!user2.equals(" ")) {
            LinearLayout ll = (LinearLayout) findViewById(R.id.edit);
            ll.setVisibility(View.VISIBLE);

            ll = (LinearLayout) findViewById(R.id.form2);
            ll.setVisibility(View.INVISIBLE);

            mBtUser2.setVisibility(View.VISIBLE);
            mBtUser2.setEnabled(true);
            userNumber++;
            mBtUser2.setText(user2);
      //      mBtUser3.setEnabled(true);
        }
        userNum = 3;

    }

    public void emojiSetting(){

        emojiconEditText = (EmojiconEditText) findViewById(R.id.messageEditText);
         //  emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        //  submitButton = (ImageView) findViewById(R.id.submit_btn);

        popup = new EmojiconsPopup(rootView, this);
        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                         changeEmojiKeyboardIcon(mBtEmoji, R.drawable.ic_insert_emoticon_white_24dp);
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
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
                userNum = 1;
                inputText(1);
                break;
            case R.id.user2Button:
                userNum = 2;
                inputText(2);
                break;
   /*        case R.id.commant:
                userNum = 3;
                inputText(3);
                break;*/
            case R.id.previewButton:
                previewStory();
                break;

            case R.id.addEmoji:
                addEmoji();
                break;

            case R.id.addPhoto:
                addPhoto();// do your code
                break;
            case R.id.messageUserName:
                changeUserName();
                break;
            default:

                break;
        }

    }
  private void changeUserName(){
      if(userNum==3)return;
      RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.root_view);
      LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

      popupView = layoutInflater.inflate(R.layout.changename, null);
      Integer heigit = relativeLayout.getHeight()/2;
      popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      popupWindow.setFocusable(true);
      //
      //popupWindow.setAnimationStyle(R.style.AppTheme_popup);
      popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, -250);

      final Button ib_ok = (Button) popupView.findViewById(R.id.ok);
      final Button ib_cancel = (Button) popupView.findViewById(R.id.cancel);
      ib_ok.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View arg0) {
              String temp_name = "";
              String name_new = ((EditText)popupView.findViewById(R.id.new_username)).getText().toString();
              if(name_new==null){
                  Toast.makeText(storyCreateActivity.this, "Plz input username!!", Toast.LENGTH_SHORT).show();
                  return;
              }else{

                  TextView txtView = (TextView) findViewById(R.id.messageUserName);
                  txtView.setText(name_new);
                  if(userNum==1){
                      Button mBtUser1 = (Button) findViewById(R.id.user1Button);
                      temp_name = mBtUser1.getText().toString();
                      mBtUser1.setText(name_new);

                  }else if(userNum==2){
                      Button mBtUser2 = (Button) findViewById(R.id.user2Button);
                      temp_name = mBtUser2.getText().toString();
                      mBtUser2.setText(name_new);
                  }

                  datab=openOrCreateDatabase("C_DB", Context.MODE_PRIVATE, null);
                  Cursor cursor = datab.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name ='chat_table'" , null);
                  cursor.moveToFirst();
                  int k = cursor.getCount();
                  if(cursor.getCount()>0){
                      Cursor cursor2 = datab.rawQuery("SELECT count(*) FROM chat_table WHERE name ='"+temp_name+" ';",null);
                      cursor2.moveToFirst();
                      k = cursor2.getCount();
                      if(cursor2.getCount()>0)
                        datab.execSQL("UPDATE chat_table SET name = '"+name_new+"' WHERE name ='"+temp_name+" ';");
                      datab.close();
              }

                  popupWindow.dismiss();
              }

          }
      });
      ib_cancel.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View arg0) {
              popupWindow.dismiss();
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
        if(data!=null) {
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
    }

    private void addEmoji(){


        if (!popup.isShowing()) {

            //If keyboard is visible, simply show the emoji popup
            if (popup.isKeyBoardOpen()) {
                popup.showAtBottom();
                changeEmojiKeyboardIcon(mBtEmoji, R.drawable.ic_keyboard_hide_white_24dp);
            }

            //else, open the text keyboard first and immediately after that show the emoji popup
            else {
                emojiconEditText.setFocusableInTouchMode(true);
                emojiconEditText.requestFocus();
                popup.showAtBottomPending();
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                changeEmojiKeyboardIcon(mBtEmoji, R.drawable.ic_keyboard_hide_white_24dp);
            }
        }

        //If popup is showing, simply dismiss it to show the undelying text keyboard
        else {
            popup.dismiss();
        }
        // To toggle between text keyboard and emoji keyboard keyboard(Popup)


    }
    private void changeEmojiKeyboardIcon(Button iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setBackgroundResource(drawableResourceId);
    }

    private void previewStory(){
        Button mBtUser1 = (Button) findViewById(R.id.user1Button);
        Button mBtUser2 = (Button) findViewById(R.id.user2Button);

        Intent intent = new Intent(this,storyViewActivity.class);
        intent.putExtra("user1",mBtUser1.getText().toString());
        intent.putExtra("user2",mBtUser2.getText().toString());
        startActivity(intent);

    }

    private void inputText(int no){


        TextView txtView = (TextView) findViewById(R.id.messageUserName);
        EditText txt = (EditText) findViewById(R.id.messageEditText);
        ImageView im = (ImageView) findViewById(R.id.ivImg);
        TextView t = (TextView) findViewById(R.id.imagesource);
        Integer txtcolor = txtView.getCurrentTextColor();
        String username = txtView.getText().toString();
        String str = txt.getText().toString();
        String str1 = t.getText().toString();
  /*      if(no==3){
            txtcolor = Color.parseColor("#FFC40A0A");
            username = " ";
            txtView.setText(" ");
        }*/

        if(!txt.isEnabled()){
            txt.setEnabled(true);
            txt.setHint(" ");
            txt.setText(" ");
            im.setImageDrawable(null);
        } else if(!(str.equals(" ") && str1.equals(" "))){

           // chatContents itm = new chatContents(txtView.getText().toString(),Integer.toString(txtView.getCurrentTextColor()), txt.getText().toString(),t.getText().toString());
           // DBHelper db=new DBHelper(this);
            datab=openOrCreateDatabase("C_DB", Context.MODE_PRIVATE, null);
            datab.execSQL("CREATE TABLE IF NOT EXISTS chat_table (name TEXT DEFAULT ' ', speech TEXT DEFAULT ' ', color Integer DEFAULT 0, photo TEXT DEFAULT ' ');");
            datab.execSQL("INSERT INTO chat_table (name, speech, color, photo) VALUES('"+username+"','"+txt.getText().toString()+"','"+txtcolor+"','"+t.getText().toString()+"');");
            datab.close();

            txt.setText(" ");
            txt.setHint(" ");
            im.setImageDrawable(null);
            t.setText(" ");

        }

        if(no==1){
            Button mBtUser1 = (Button) findViewById(R.id.user1Button);
            Button mBtUser2 = (Button) findViewById(R.id.user2Button);
    //        Button mBtUser3 = (Button) findViewById(R.id.commant);
            mBtUser1.setAlpha((float)1);
            mBtUser2.setAlpha((float)0.3);
    //        mBtUser3.setAlpha((float)0.3);
            txtView.setText(mBtUser1.getText().toString());
            txtView.setTextColor(Color.parseColor("#33bcfc"));
            txtView.setGravity(Gravity.LEFT);

        }else if(no==2){
            Button mBtUser2 = (Button) findViewById(R.id.user2Button);
            Button mBtUser1 = (Button) findViewById(R.id.user1Button);
      //      Button mBtUser3 = (Button) findViewById(R.id.commant);
            mBtUser1.setAlpha((float)0.3);
      //      mBtUser3.setAlpha((float)0.3);
            mBtUser2.setAlpha((float)1);
            txtView.setText(mBtUser2.getText().toString());
            txtView.setTextColor(Color.parseColor("#6e3e0b"));
            txtView.setGravity(Gravity.RIGHT);

        }else{
            Button mBtUser = (Button) findViewById(R.id.user1Button);
            Button mBtUser2 = (Button) findViewById(R.id.user2Button);
     //       Button mBtUser3 = (Button) findViewById(R.id.commant);
            mBtUser.setAlpha((float)0.3);
            mBtUser2.setAlpha((float)0.3);
     //       mBtUser3.setAlpha((float)1);
    //        txtView.setText(mBtUser3.getText().toString());
            txtView.setTextColor(Color.parseColor("#ef6b6b"));
            txtView.setGravity(Gravity.LEFT);
        }


        Button imBtn = (Button) findViewById(R.id.previewButton);
        imBtn.setEnabled(true);
        imBtn.setAlpha(1);

        imBtn = (Button) findViewById(R.id.addEmoji);
        imBtn.setEnabled(true);
        imBtn.setAlpha(1);

        imBtn = (Button) findViewById(R.id.addPhoto);
        imBtn.setEnabled(true);
        imBtn.setAlpha(1);

    }


    private void addUserName() {

        if(userNumber>=2){
            Toast.makeText(this, "Can not add user more two", Toast.LENGTH_SHORT).show();
            return;
        }

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
        imBtn.setAlpha((float) 0.3);
        if (popup.isShowing()) {
            popup.dismiss();
        }

        imBtn = (Button) findViewById(R.id.addPhoto);
        imBtn.setEnabled(false);
        imBtn.setAlpha((float) 0.3);

    }
    private void createUserName() {
        Button mBtUser,mBtUser3;
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
        if((userNumber%2)==0) {
            mBtUser = (Button) findViewById(R.id.user1Button);
            mBtUser.setVisibility(View.VISIBLE);
            mBtUser.setEnabled(true);
            userNumber++;
        }else{
            mBtUser = (Button) findViewById(R.id.user2Button);
            mBtUser.setVisibility(View.VISIBLE);
            mBtUser.setEnabled(true);
            userNumber++;
        }


        mBtUser.setText((CharSequence) name);
        txt.setText("");

        LinearLayout ll = (LinearLayout) findViewById(R.id.edit);
        ll.setVisibility(View.VISIBLE);

        ll = (LinearLayout) findViewById(R.id.form2);
        ll.setVisibility(View.INVISIBLE);

        txt = (EditText) findViewById(R.id.messageEditText);
        txt.setHint("Please Selcet User!");

    /*    mBtUser3 = (Button) findViewById(R.id.commant);
        mBtUser3.setVisibility(View.VISIBLE);
        mBtUser3.setEnabled(true);*/
    }
}