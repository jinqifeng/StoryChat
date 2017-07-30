package com.ids.storychat;

/**
 * Created by JongWN-D on 7/24/2017.
 */
import android.net.Uri;


import java.util.*;
public class story {

    public String title;
    public String key;
    public String photo;
    public story(String name){
        title = name;
    }

    public String getTitle(){
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    private static int lastContactId = 0;

    public static ArrayList<story> createStoryList(int numContacts) {
        ArrayList<story> storys = new ArrayList<story>();

        for (int i = 1; i <= numContacts; i++) {
            storys.add(new story("story" + ++lastContactId));
        }

        return storys;
    }
    public String getKey() {
        return key;
    }
    public Uri getPhoto() {
        Uri uri= Uri.parse("R.drawable."+title);
        return uri;
    }
}
