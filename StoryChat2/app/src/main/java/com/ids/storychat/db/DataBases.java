package com.ids.storychat.db;

import android.provider.BaseColumns;

import static android.provider.MediaStore.Audio.Playlists.Members._ID;

/**
 * Created by JongWN-D on 7/31/2017.
 */

public class DataBases {
    public static final class CreateDB implements BaseColumns {
        public static final String NAME = "name";
        public static final String CONTACT = "contact";
        public static final String EMAIL = "email";
        public static final String _TABLENAME = "address";
        public static final String _CREATE =
                "create table "+_TABLENAME+"("
                        +_ID+" integer primary key autoincrement, "
                        +NAME+" text not null , "
                        +CONTACT+" text not null , "
                        +EMAIL+" text not null );";
    }

}
