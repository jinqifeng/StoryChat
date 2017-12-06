package com.jwn.storychat.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.jwn.storychat.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by panl on 15/9/6.
 */
public class ShareUtils2 {

    private ShareUtils2() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static Bitmap captureScreen(Activity activity) {
        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
        Bitmap bitmap = activity.getWindow().getDecorView().getDrawingCache();
        return bitmap;
    }

    public static void shareLove2048(Activity activity) throws FileNotFoundException {
        Intent intent = new Intent(Intent.ACTION_SEND);
        File f = new File(saveLove2048Capture(activity));
        if (f != null && f.exists() && f.isFile()) {
            intent.setType("image/*");
            Uri u = Uri.fromFile(f);
            intent.putExtra(Intent.EXTRA_STREAM, u);
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, "StoryChat");
        intent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.record));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent,"StoryChat"));
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    public static String getLove2048Dir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "storyChat";
    }

    public static String saveLove2048Capture(Activity activity) throws FileNotFoundException {
      /*  if (!isSDCardEnable()){
            Toast.makeText(activity,R.string.endgame_win_text, Toast.LENGTH_SHORT).show();
            return "";
        }*/
        File love2048Dir = new File(getLove2048Dir());
        if (!love2048Dir.exists())
            love2048Dir.mkdir();
        File file = new File(getLove2048Dir(),"Storychat.jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {

        }
        FileOutputStream outputStream = new FileOutputStream(file);
        captureScreen(activity).compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static String saveWeChatPay(Activity activity) {
    /*    if (!isSDCardEnable()){
            Toast.makeText(activity, R.string.endgame_win_text, Toast.LENGTH_SHORT).show();
            return "";
        }*/
        File love2048Dir = new File(getLove2048Dir());
        if (!love2048Dir.exists())
            love2048Dir.mkdir();
        File file = new File(getLove2048Dir(),"Storychat.jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {

        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),R.drawable.gear);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
