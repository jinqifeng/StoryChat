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
    private String user1,user2;
 //   private String color1,color2;
    private String title;
    public story(){

    }
    public story(String author_in, String user1_in, String user2_in,String color1_in, String color2_in, String date_in, String photo_in ){
        author = author_in;
        user1 = user1_in;
        user2 = user2_in;
        date = date_in;
        photo = photo_in;
    //    color1 = color1_in;
    //    color2 = color2_in;

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
    public String getUser1() {

        return user1;
    }
    public String getUser2() {

        return user2;
    }
 /*   public String getColor2() {

        return color2;
    }
    public String getColor1() {

        return color1;
    }*/
    public void setTitle(String in) {
        title = in;
    }
    public String getTitle() {

        return title;
    }

}
