package com.bsit3omagitech.titserko;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String USER_TABLE = "USER_TABLE";
    public static final String COLUMN_USER_NAME = "USER_NAME";
    public static final String COLUMN_USER_BDAY = "USER_BDAY";
    public static final String COLUMN_ID = "ID";
    ;

    public DataBaseHelper(@Nullable Context context) {
        super(context, "user.db", null, 1);
    }

    //create when called for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableStatement = "CREATE TABLE " + USER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_NAME + " TEXT, " + COLUMN_USER_BDAY + " DATE)";

        db.execSQL(createTableStatement);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(UserModel userModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER_NAME, userModel.getName());
        cv.put(COLUMN_USER_BDAY, userModel.getDate());

        long insert = db.insert(USER_TABLE, null, cv);

        if(insert == -1){
            return false;

        }else{
            return true;
        }


    }
}
