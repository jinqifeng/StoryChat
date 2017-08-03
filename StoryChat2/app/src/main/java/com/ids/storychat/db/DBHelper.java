package com.ids.storychat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ids.storychat.storyContents;

import java.util.ArrayList;

/**
 * Created by Hp on 3/18/2016.
 */
public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);

    }

    //TABLE CREATION
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
           // db.execSQL(Constants.CREATE_TB);

            db.execSQL(
                    "create table contacts " +
                            "(id integer primary key, name text,words text,url text, clr int)"
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    //INSERT
    public long add(String name, String words, String url_in, Integer clr_in) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            cv.put("words", words);
            cv.put("url", url_in);
            cv.put("clr", clr_in);
            SQLiteDatabase db = getWritableDatabase();
            long rt = db.insert("d_TB", null, cv);
            db.close();
            return rt;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //TABLE UPGRADE
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TB_NAME);
        onCreate(db);

    }

    public ArrayList<storyContents> getAllCotents() {
        ArrayList<storyContents> array_list = new ArrayList<storyContents>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from d_TB", null);
        res.moveToFirst();
        while(res.moveToNext())
        {
            String name = res.getString(1);
            String words = res.getString(2);
            String url = res.getString(3);
            Integer clr = res.getInt(4);
            storyContents p = new storyContents(name, words, url, clr);
            //ADD TO ARRAYLIS
            array_list.add(p);
        }
        return array_list;
    }
    // Getting single contact
    public storyContents getContents(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery( "select * from d_TB where id="+id+"", null );
        if (cursor != null)
            cursor.moveToFirst();

        storyContents contents = new storyContents(cursor.getString(1),
                cursor.getString(2), cursor.getString(3),Integer.parseInt(cursor.getString(4)));
        // return contact
        return contents;
    }


}
