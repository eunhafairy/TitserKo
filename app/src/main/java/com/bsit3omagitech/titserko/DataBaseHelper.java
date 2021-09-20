package com.bsit3omagitech.titserko;

import android.content.ContentValues;
import android.database.SQLException;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String USER_TABLE = "USER_TABLE";
    public static final String COLUMN_USER_NAME = "USER_NAME";
    public static final String COLUMN_USER_BDAY = "USER_BDAY";
    public static final String COLUMN_ID = "COLUMN_ID";

    public static final int DB_VERSION = 7;
    Context context;

    public DataBaseHelper(@Nullable Context context) {
        super(context, "user.db", null, DB_VERSION);
        this.context = context;
    }

    //create when called for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableStatement = "CREATE TABLE " + USER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_NAME + " TEXT, " + COLUMN_USER_BDAY + " DATE)";

        try {
            db.execSQL(createTableStatement);
            Toast.makeText(context, "Table Created", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "Walang na create", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }




    public boolean addOne(UserModel userModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //check if same name
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String checkUsername = "SELECT "+ COLUMN_USER_NAME+" FROM "+ USER_TABLE;
        Cursor cursor = dbRead.rawQuery(checkUsername, null);
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0).equals(userModel.getName())){
                 return false;
                }
            } while (cursor.moveToNext());
        }

        cv.put(COLUMN_USER_NAME, userModel.getName());
        cv.put(COLUMN_USER_BDAY, userModel.getDate());



        long insert = db.insert(USER_TABLE, null, cv);

        if(insert == -1){
            return false;

        }else{
            return true;
        }


    }

    //function to select all usernames and store it in List
    public List<String> getAllUsername(){
        List<String> usernames = new ArrayList<String>();

        //select query
        String selectQuery =  "SELECT "+COLUMN_USER_NAME+" FROM "+ USER_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // loop
        if (cursor.moveToFirst()) {
            do {
                usernames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();
        // returning usernames
        return usernames;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop table

        final String DROP_USER_TABLE  = "DROP TABLE IF EXISTS "+ USER_TABLE;
        try{

            db.execSQL(DROP_USER_TABLE);
            Toast.makeText(context, "Table created", Toast.LENGTH_SHORT).show();
            onCreate(db);
        }
        catch (SQLException e) {
            Toast.makeText(context, "Walang na create", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
