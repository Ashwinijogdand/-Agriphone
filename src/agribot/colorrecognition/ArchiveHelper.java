package com.agribot.colorrecognition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ArchiveHelper extends SQLiteOpenHelper {

    private static final String NAME = "CTArchiveDB";
    private static final int VERSION = 1;
    
    public ArchiveHelper(Context context) {
            super(context, NAME, null, VERSION);
    }

    SQLiteDatabase mDB;
    
    private static final String Q_CREATE = "create table archive (id integer primary key autoincrement, " +
                    "creation_date date default (datetime('now', 'localtime')), " +
                    "color_name text not null, " +
                    "color_value integer not null);";
    
    @Override
    public void onCreate(SQLiteDatabase database) {
            mDB = database;
            mDB.execSQL(Q_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub      
    }

}
