package com.jwn.storychat;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by JongWN-D on 7/27/2017.
 */

public class chatContents implements Parcelable {

    private String name;
    private String speech;
    private Integer speech_color;
    private String with_photo;


    public chatContents(){}

    public chatContents(String name_in, String cnt,Integer clr_in,String picin )
    {
        name = name_in;
        speech = cnt;
        speech_color = clr_in;
        with_photo = picin;


    }

    private chatContents(Parcel in) {
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

    public static final Parcelable.Creator<chatContents> CREATOR = new Parcelable.Creator<chatContents>() {
        public chatContents createFromParcel(Parcel in) {
            return new chatContents(in);
        }

        public chatContents[] newArray(int size) {
            return new chatContents[size];

        }
    };

    // all get , set method
    public String getPerson(){
        return name;
    }
    public String getConv(){
        return speech;
    }
    public String getUrl(){ return with_photo;}
    public Integer getColor(){return speech_color;}
    public void setConv(String s){
        speech = s;
    }
    public void setUrl(String in_url){
        with_photo=in_url;
    }
}
