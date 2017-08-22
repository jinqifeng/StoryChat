package com.jwn.storychat;

/**
 * Created by JongWN-D on 7/24/2017.
 */
import android.net.Uri;


import java.util.*;
public class story {

    //public String title;
    private String photo;
    private String date;
    private String author;
    private String category;
    private String title;
    public story(){

    }
    public story(String author_in, String category_in, String date_in, String photo_in ){
        author = author_in;
        category = category_in;
        date = date_in;
        photo = photo_in;

    }



    private static int lastContactId = 0;

    public static ArrayList<story> createStoryList(int numContacts) {
        ArrayList<story> storys = new ArrayList<story>();

        for (int i = 1; i <= numContacts; i++) {
            storys.add(new story());
        }

        return storys;
    }

    public String getPhoto() {

        return photo;
    }
    public String getAuthor() {

        return author;
    }
    public String getDate() {

        return date;
    }
    public String getCategory() {

        return category;
    }
    public void setTitle(String in) {
        title = in;
    }
    public String getTitle() {

        return title;
    }

}
