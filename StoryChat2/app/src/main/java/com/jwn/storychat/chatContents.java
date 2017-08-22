package com.jwn.storychat;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by JongWN-D on 7/27/2017.
 */

public class chatContents implements Parcelable {

    private String person;
    private String conv;
    private String url;
    private Integer color;


    public chatContents(){}

    public chatContents(Integer clr_in, String cnt,String name,String picin )
    {
        color = clr_in;
        conv = cnt;
        person = name;
        url = picin;


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
        return person;
    }
    public String getConv(){
        return conv;
    }
    public String getUrl(){ return url;}
    public Integer getColor(){return color;}
    public void setConv(String s){
        conv = s;
    }
    public void setUrl(String in_url){
        url=in_url;
    }
}
