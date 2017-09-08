package com.jwn.storychat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.ImageView;
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
import java.util.concurrent.TimeUnit;

import com.jwn.storychat.util.IabBroadcastReceiver;
import com.jwn.storychat.util.IabBroadcastReceiver.IabBroadcastListener;
import com.jwn.storychat.util.IabHelper;
import com.jwn.storychat.util.IabHelper.IabAsyncInProgressException;
import com.jwn.storychat.util.IabResult;
import com.jwn.storychat.util.Inventory;
import com.jwn.storychat.util.Purchase;
import android.os.CountDownTimer;

import static com.jwn.storychat.MainActivity.PREFS_NAME;


/**
 * Created by JongWN-D on 8/4/2017.
 */

public class storyOneViewActivity extends AppCompatActivity implements IabBroadcastListener, OnClickListener{

    static final String TAG = "TrivialDrive";

    ArrayList<chatContents> story_view;
    ArrayList<chatContents> story_temp;
    private RecyclerView rvStorys;
    Integer position_recycle = 0;
    FirebaseDatabase database;
    storyViewAdapter adapter;
    String titlename;
    Integer read_num;
    Integer next_num;
    Boolean is_possible_read;
    RelativeLayout relativeLayout;
    View popupView;
    private PopupWindow popupWindow;
    Integer subscrition_level;
    // The helper object
    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    static final String SKU_PREMIUM = "premium";
    static final String SKU_GAS = "gas";
    // SKU for our subscription (infinite gas)
    static final String SKU_INFINITE_GAS_MONTHLY = "infinite_gas_monthly";
    static final String SKU_INFINITE_GAS_YEARLY = "infinite_gas_yearly";
    // Tracks the currently owned infinite gas SKU, and the options in the Manage dialog
    String mInfiniteGasSku = "";
    String mFirstChoiceSku = "";
    String mSecondChoiceSku = "";
    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;
    // Does the user have the premium upgrade?
    boolean mIsPremium = false;
    // Does the user have an active subscription to the infinite gas plan?
    boolean mSubscribedToInfiniteGas = false;
    // How many units (1/4 tank is our unit) fill in the tank.
    static final int TANK_MAX = 4;

    // Current amount of gas in tank, in units
    int mTank;

    TextView editTextMinute;
    TextView textViewTime;
    ProgressBar progressBarCircle;
    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private CountDownTimer countDownTimer;

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


        Button mBtNext = (Button) findViewById(R.id.button2);

        mBtNext.setOnClickListener(this);

        rvStorys = (RecyclerView) findViewById(R.id.rvStory_one);
        // Create adapter passing in the sample user data
        adapter = new storyViewAdapter(this,story_view);
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
            read_num = 1;
        }
        is_possible_read = settings.getBoolean("is_possible_read", false);
        next_num = 0;

    }
    public void setPaymentSystem(){
        String base64EncodedPublicKey = "CONSTRUCT_YOUR_KEY_AND_PLACE_IT_HERE";

        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please put your app's public key in MainActivity.java. See README.");
        }
        if (getPackageName().startsWith("com.example")) {
            throw new RuntimeException("Please change the sample's package name! See README.");
        }

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(storyOneViewActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }
    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // First find out which subscription is auto renewing
            Purchase gasMonthly = inventory.getPurchase(SKU_INFINITE_GAS_MONTHLY);
            Purchase gasYearly = inventory.getPurchase(SKU_INFINITE_GAS_YEARLY);
            if (gasMonthly != null && gasMonthly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_MONTHLY;
                mAutoRenewEnabled = true;
            } else if (gasYearly != null && gasYearly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_YEARLY;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteGasSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToInfiniteGas = (gasMonthly != null && verifyDeveloperPayload(gasMonthly))
                    || (gasYearly != null && verifyDeveloperPayload(gasYearly));
            Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");
            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                Log.d(TAG, "We have gas. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
                } catch (IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                }
                return;
            }

         //   updateUi();
         //   setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
               // saveData();
                alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
            }
            else {
                complain("Error while consuming: " + result);
            }
         //   updateUi();
         //   setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };
    // updates UI to reflect model
    public void updateUi() {
      /*  // update the car color to reflect premium status or lack thereof
        ((ImageView)findViewById(R.id.free_or_premium)).setImageResource(mIsPremium ? R.drawable.premium : R.drawable.free);

        // "Upgrade" button is only visible if the user is not premium
        findViewById(R.id.upgrade_button).setVisibility(mIsPremium ? View.GONE : View.VISIBLE);

        ImageView infiniteGasButton = (ImageView) findViewById(R.id.infinite_gas_button);
        if (mSubscribedToInfiniteGas) {
            // If subscription is active, show "Manage Infinite Gas"
            infiniteGasButton.setImageResource(R.drawable.manage_infinite_gas);
        } else {
            // The user does not have infinite gas, show "Get Infinite Gas"
            infiniteGasButton.setImageResource(R.drawable.get_infinite_gas);
        }

        // update gas gauge to reflect tank status
        if (mSubscribedToInfiniteGas) {
            ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(R.drawable.gas_inf);
        }
        else {
            int index = mTank >= TANK_RES_IDS.length ? TANK_RES_IDS.length - 1 : mTank;
            ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(TANK_RES_IDS[index]);
        }*/
    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
     /*   findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);*/
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }
    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }
    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }


    public void onClick(View v) {
        switch (v.getId()) {


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
    /**
     * method to start and stop count down timer
     */
    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();

            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {


            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {
        int time = 0;
        if (!editTextMinute.getText().toString().isEmpty()) {
            // fetching value from edit text and type cast to integer
            time = Integer.parseInt(editTextMinute.getText().toString().trim());
        } else {
            // toast message to fill edit text
            Toast.makeText(getApplicationContext(), "Please Enter Minutes...", Toast.LENGTH_LONG).show();
        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 60 * 1000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;

                popupWindow.dismiss();
                next_num = 0;
            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
    }
    public void limitdialog(){

        relativeLayout = (RelativeLayout) findViewById(R.id.publish_layout);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.limitdialog, null);
        Integer heigit = relativeLayout.getHeight()/2;
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        //
        popupWindow.setAnimationStyle(R.style.AppTheme_popup);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);

        progressBarCircle = (ProgressBar) popupView.findViewById(R.id.progressBarCircle);
        editTextMinute = (TextView) popupView.findViewById(R.id.editTextMinute);
        textViewTime = (TextView) popupView.findViewById(R.id.textViewTime);


       final ImageView imageViewUnlimited = (ImageView) popupView.findViewById(R.id.imageViewUnlimited);

        imageViewUnlimited.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                timerStatus = TimerStatus.STOPPED;
                stopCountDownTimer();
                popupWindow.dismiss();
                subscript();
            }
        });
        startStop();
    }
    public void onNext(){

        if(next_num >= 5 && !is_possible_read){
            limitdialog();
        }
        if(read_num<story_temp.size())
        {

            chatContents p = story_temp.get(read_num);
            //ADD TO ARRAYLIS
            story_view.add(p);

            storyViewAdapter adapter = new storyViewAdapter(this,story_view);
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
            next_num++;

        }
    }
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        is_possible_read = settings.getBoolean("is_possible_read", false);

    }
    public void subscript(){
      //  setPaymentSystem();
        relativeLayout = (RelativeLayout) findViewById(R.id.publish_layout);
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.subscription, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);

     //   popupWindow.setAnimationStyle(R.style.AppTheme_popup);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, 0, 0);
   //     popupWindow.showAsDropDown(popupView);

        final Button btnOpenPopup1 = (Button) popupView.findViewById(R.id.free);
        final Button btnOpenPopup2 = (Button) popupView.findViewById(R.id.month);
        final Button btnOpenPopup3 = (Button) popupView.findViewById(R.id.year);
        final TextView tvLogin = (TextView) popupView.findViewById(R.id.textView3);
        final TextView tvBack = (TextView) popupView.findViewById(R.id.textView8);

        btnOpenPopup1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                subscrition_level = 0;
                Toast.makeText(storyOneViewActivity.this, "Current is working for payment system!!!", Toast.LENGTH_SHORT).show();
            }

        });
        btnOpenPopup2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                subscrition_level = 1;
                Toast.makeText(storyOneViewActivity.this, "Current is working for payment system!!!", Toast.LENGTH_SHORT).show();
            }


        });
        btnOpenPopup3.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                subscrition_level = 2;
                Toast.makeText(storyOneViewActivity.this, "Current is working for payment system!!!", Toast.LENGTH_SHORT).show();
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
                Intent it = new Intent(storyOneViewActivity.this, LoginActivity.class);
                it.putExtra("for",1);
                it.putExtra("titlename",titlename);
                startActivity(it);
            }
        });
        tvBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
                limitdialog();

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }



}
