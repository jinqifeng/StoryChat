package com.ids.storychat;

/**
 * Created by JongWN-D on 7/26/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.ids.storychat.emoji.EmojiconEditText;
import com.ids.storychat.emoji.EmojiconsPopup;
import com.ids.storychat.emoji.Emojicon;
import com.ids.storychat.emoji.EmojiconGridView;


public class storyCreateActivity extends AppCompatActivity implements View.OnClickListener  {

    private ImageButton mBtAddUser;
    private Button mBtCreateUser;
    private Button mBtUser1;
    private Button mBtUser2;
    private Button mBtSendMsg;
    private Button mBtEmoji;



    private static int userNumber=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_create);

        mBtAddUser = (ImageButton) findViewById(R.id.addUserButton);
        mBtAddUser.setOnClickListener(this);

        mBtCreateUser = (Button) findViewById(R.id.createUser);
        mBtCreateUser.setOnClickListener(this);

        mBtUser1 = (Button) findViewById(R.id.user1Button);
        mBtUser1.setOnClickListener(this);

        mBtUser2 = (Button) findViewById(R.id.user2Button);
        mBtUser2.setOnClickListener(this);

        mBtSendMsg = (Button) findViewById(R.id.sendMessageButton);
        mBtSendMsg.setOnClickListener(this);

        mBtEmoji = (Button) findViewById(R.id.addEmoji);
        mBtEmoji.setOnClickListener(this);

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
            case R.id.sendMessageButton:
                sendMessage();
                break;

            case R.id.addEmoji:
                addEmoji();
                break;

            case R.id.addPhoto:
                // do your code
                break;

            default:
                break;
        }
    }


    private void addEmoji(){

        final EmojiconEditText emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        final View rootView = findViewById(R.id.root_view);
        final ImageView emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        final ImageView submitButton = (ImageView) findViewById(R.id.submit_btn);
        final TextView txtEmojis = (TextView) findViewById(R.id.messageEditText);

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
               //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
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

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        emojiconEditText.setFocusableInTouchMode(true);
                        emojiconEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        //On submit, add the edittext text to listview and clear the edittext
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String newText = emojiconEditText.getText().toString();
                txtEmojis.setText(newText);
                emojiconEditText.getText().clear();


            }
        });

    }
    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }
    private void sendMessage(){

    }

    private void inputText(int no){

        LinearLayout ll = (LinearLayout) findViewById(R.id.edit);
        ll.setVisibility(View.VISIBLE);

        ll = (LinearLayout) findViewById(R.id.form2);
        ll.setVisibility(View.INVISIBLE);

        EditText txt = (EditText) findViewById(R.id.messageEditText);
        txt.setText("");

        TextView txtView = (TextView) findViewById(R.id.messageUserName);

        if(no==1){
            Button mBtUser = (Button) findViewById(R.id.user1Button);
            txtView.setText(mBtUser.getText().toString()+":");
            txtView.setTextColor(Color.rgb(255, 102, 153));
        }else {
            Button mBtUser = (Button) findViewById(R.id.user2Button);
            txtView.setText(mBtUser.getText().toString()+":");
            txtView.setTextColor(Color.GREEN);
        }

        Button imBtn = (Button) findViewById(R.id.sendMessageButton);
        imBtn.setEnabled(true);
        imBtn.setTextColor(Color.GREEN);

        imBtn = (Button) findViewById(R.id.addEmoji);
        imBtn.setEnabled(true);
        imBtn.setBackgroundResource(R.drawable.emoji1);


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

        Button imBtn = (Button) findViewById(R.id.sendMessageButton);
        imBtn.setTextColor(Color.GRAY);
        imBtn.setEnabled(false);

        imBtn = (Button) findViewById(R.id.addEmoji);
        imBtn.setEnabled(false);
        imBtn.setBackgroundResource(R.drawable.emoji2);
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

        LinearLayout ll = (LinearLayout) findViewById(R.id.form2);
        ll.setVisibility(View.INVISIBLE);
        txt.setText("");
    }
}