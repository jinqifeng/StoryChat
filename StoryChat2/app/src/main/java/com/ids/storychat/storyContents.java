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

    private String personname;
    private String conversation;
    private String url;
    private String clr;

    public storyContents(String name, String clr_in, String cnt, String picin)
    {
        personname = name;
        clr = clr_in;
        conversation = cnt;
        url = picin;
    }

    private storyContents(Parcel in) {
        personname = in.readString();
        clr = in.readString();
        conversation = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(personname);
        dest.writeString(clr);
        dest.writeString(conversation);
        dest.writeString(url);
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
        return personname;
    }
    public String getConv(){
        return conversation;
    }
    public String getUrl(){ return url;}
    public String getColor(){return clr;}
    public void setConv(String s){
        conversation = s;
    }
}
