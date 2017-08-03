package com.ids.storychat.db;

/**
 * Created by Hp on 3/18/2016.
 */
public class Constants {
    //COLUMNS
    static final String ROW_ID="id";
    static final String NAME = "name";
    static final String WORDS = "words";
    static final String URL = "url";
    static final String CLR = "clr";

    //DB PROPERTIES
    static final String DB_NAME="d_DB";
    static final String TB_NAME="d_TB";
    static final int DB_VERSION='1';

    static final String CREATE_TB="CREATE TABLE IF NOT EXISTS d_TB(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT,words TEXT,url TEXT,clr INT);";
}
