package com.ids.storychat;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Parcelable;
import android.os.Parcel;
import android.content.Context;
/**
 * Created by JongWN-D on 7/27/2017.
 */

public class storyContents implements Parcelable {

    private String a_personname;
    private String b_conversation;
    private String c_imageurl;
    private Integer d_clr;

    public storyContents(){}

    public storyContents(String name,String cnt ,String picin , Integer clr_in)
    {
        a_personname = name;
        b_conversation = cnt;
        c_imageurl = picin;
        d_clr = clr_in;
    }

    private storyContents(Parcel in) {
    /*    personname = in.readString();
        clr = in.readString();
        conversation = in.readString();
        url = in.readString();*/
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
     /*   dest.writeString(personname);
        dest.writeString(clr);
        dest.writeString(conversation);
        dest.writeString(url);*/
    }

    public static final Parcelable.Creator<storyContents> CREATOR = new Parcelable.Creator<storyContents>() {
        public storyContents createFromParcel(Parcel in) {
            return new storyContents(in);
        }

        public storyContents[] newArray(int size) {
            return new storyContents[size];

        }
    };

    // all get , set method
    public String getPerson(){
        return a_personname;
    }
    public String getConv(){
        return b_conversation;
    }
    public String getUrl(){ return c_imageurl;}
    public Integer getColor(){return d_clr;}
    public void setConv(String s){
        b_conversation = s;
    }
    public void setUrl(String in_url){
        c_imageurl=in_url;
    }
}
