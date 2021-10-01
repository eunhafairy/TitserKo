package com.bsit3omagitech.titserko;


import android.content.ContentValues;
import android.database.SQLException;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "db_debug";

    //user table
    public static final String USER_TABLE = "USER_TABLE";
    public static final String COLUMN_USER_NAME = "USER_NAME";
    public static final String COLUMN_USER_BDAY = "USER_BDAY";
    public static final String COLUMN_ID = "COLUMN_ID";

    //lesson table
    public static final String LESSON_TABLE = "LESSON_TABLE";
    public static final String LESSON_ID = "LESSON_ID";
    public static final String LESSON_NAME = "LESSON_NAME";

    //user-lesson-record
    public static final String LESSON_PROGRESS_TABLE = "LESSON_PROGRESS_TABLE";
    public static final String LP_ID = "LP_ID";
    public static final String LP_USER_NAME = "USER_NAME"; //FOREGIN KEY
    public static final String LP_LESSON_ID = "LESSON_ID"; //FOREGIN KEY
    public static final String LP_QUIZ_HIGHSCORE = "LP_QUIZ_HIGHSCORE"; //RECORD OF USER'S HIGH SCORE IN QUIZ IN THAT LESSON, INT
    public static final String LP_LESSON_PROGRESS = "LP_LESSON_PROGRESS"; //RECORD OF USER'S PROGRESS IN PERCENTAGE, INT


    public static final int DB_VERSION = 12;
    Context context;

    public DataBaseHelper(@Nullable Context context) {
        super(context, "user.db", null, DB_VERSION);
        this.context = context;
    }

    //create when called for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {

        int i = 0;
        //FOR USER TABLE
        String createTableStatement = "CREATE TABLE "
                + USER_TABLE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_NAME + " TEXT, "
                + COLUMN_USER_BDAY + " DATE)";

        try {
            db.execSQL(createTableStatement);
            i++;
        } catch (SQLException e) {

            e.printStackTrace();
        }

        //FOR LESSON TABLE
        String createLessonTableStatement = "CREATE TABLE "
                + LESSON_TABLE + " ("
                + LESSON_ID + " STRING PRIMARY KEY, "
                + LESSON_NAME + " TEXT)";

        try {
            db.execSQL(createLessonTableStatement);
            i++;
        } catch (SQLException e) {

            e.printStackTrace();
        }

        //FOR USER-LESSON-PROGRESS

        String createProgressTableStatement = "CREATE TABLE "
                + LESSON_PROGRESS_TABLE + " ("
                + LP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LP_USER_NAME + " TEXT, "
                + LP_LESSON_ID + " TEXT, "
                + LP_QUIZ_HIGHSCORE + " INTEGER, "
                + LP_LESSON_PROGRESS + " INTEGER)";

        try {
            db.execSQL(createProgressTableStatement);
            i++;
        } catch (SQLException e) {

            e.printStackTrace();
        }

        if (i >= 3) Toast.makeText(context, "Table created", Toast.LENGTH_SHORT).show();

        //insert values from JSON to LESSON_TABLE
        try {
            loadLessonsFromJSON(db);
            Log.d("Tag", "Successfully inserted!");
        } catch (IOException e) {

            e.printStackTrace();
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }


    public boolean addOne(UserModel userModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //check if same name
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String checkUsername = "SELECT " + COLUMN_USER_NAME + " FROM " + USER_TABLE;
        Cursor cursor = dbRead.rawQuery(checkUsername, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(userModel.getName())) {
                    return false;
                }
            } while (cursor.moveToNext());
        }

        cv.put(COLUMN_USER_NAME, userModel.getName());
        cv.put(COLUMN_USER_BDAY, userModel.getDate());

        long insert = db.insert(USER_TABLE, null, cv);

        if (insert == -1) {
            return false;

        } else {

  /*
            //check if create new records for lesson record
            SQLiteDatabase usernameRead = this.getReadableDatabase();
            String userIDQuery = "SELECT "+ COLUMN_ID + " FROM "+ USER_TABLE + " WHERE "+ COLUMN_USER_NAME + " = " + userModel.getName();
            Cursor cursor1 = dbRead.rawQuery(checkUsername, null);
            if (cursor.moveToFirst()) {
                do {
                    if(cursor.getString(0).equals(userModel.getName())){
                        return false;
                    }
                } while (cursor.moveToNext());
            }

            final String _LESSON_ID = "lesson_id";
*/
            return true;


        }
    }

    //function to select all usernames and store it in List
    public List<String> getAllUsername() {
        List<String> usernames = new ArrayList<String>();

        //select query
        String selectQuery = "SELECT " + COLUMN_USER_NAME + " FROM " + USER_TABLE;

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


    public void createLessonProgressEntry(String userName, String lessonID) {

        boolean isExisting = false;
        List<String> ctr = new ArrayList<String>();

        //check if existing
        //select query
        String selectQuery = "SELECT " + LP_ID + " FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + userName + "' AND " + LP_LESSON_ID + " = '" + lessonID + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            isExisting = true;
        } else {
            isExisting = false;


        }

        if (!isExisting) {
            //insert entry

            SQLiteDatabase db1 = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(LP_USER_NAME, userName);
            cv.put(LP_LESSON_ID, lessonID);
            cv.put(LP_QUIZ_HIGHSCORE, 0);
            cv.put(LP_LESSON_PROGRESS, 0);

            long insert = db.insert(LESSON_PROGRESS_TABLE, null, cv);

            if (insert == -1) {
              Log.d(TAG, "not inserted.");

            } else {
                Log.d(TAG, "inserted.");

            }


        }


    }

    public int getLessonProgress(String username,String lessonId){

        int i = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_LESSON_PROGRESS + " FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + username + "' AND " + LP_LESSON_ID + " = '" + lessonId  + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                i =  cursor.getInt(0);
            } while (cursor.moveToNext());
        }
            return i;

    }

    public void updateLessonProgress(int index, String userName, String lessonID){

        //select the row
        int lessonProgressID = 0;
        int currentIndex= 0;
        //select query
        String selectQuery = "SELECT " + LP_ID + " FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + userName + "' AND " + LP_LESSON_ID + " = '" + lessonID + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                lessonProgressID = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        //check if index is greater than the current progress
        String selectQuery2 = "SELECT " + LP_LESSON_PROGRESS + " FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_ID + " = " + lessonProgressID;
        Cursor cursor2 = db.rawQuery(selectQuery2, null);
        if (cursor2.moveToFirst()) {
            do {
                currentIndex = cursor2.getInt(0);
            } while (cursor.moveToNext());
        }

        if(currentIndex<index){

            String updateQuery = "UPDATE " + LESSON_PROGRESS_TABLE
                    + " SET " + LP_LESSON_PROGRESS + " = " + index
                    + " WHERE " + LP_ID + " = " + lessonProgressID;

            SQLiteDatabase db1 = this.getWritableDatabase();
            db.execSQL(updateQuery);

        }


    }







    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop table

        final String DROP_USER_TABLE  = "DROP TABLE IF EXISTS "+ USER_TABLE;
        final String DROP_LESSON_TABLE  = "DROP TABLE IF EXISTS "+ LESSON_TABLE;
        final String DROP_LESSON_PROGRESS_TABLE  = "DROP TABLE IF EXISTS "+ LESSON_PROGRESS_TABLE;
        try{

            db.execSQL(DROP_USER_TABLE);
            db.execSQL(DROP_LESSON_TABLE);
            db.execSQL(DROP_LESSON_PROGRESS_TABLE);
            Toast.makeText(context, "Table created", Toast.LENGTH_SHORT).show();
            onCreate(db);

        }
        catch (SQLException e) {
            Toast.makeText(context, "Walang na create", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadLessonsFromJSON(SQLiteDatabase db) throws IOException, JSONException {

        //name of key in JSON
        final String _LESSON_ID = "lesson_id";
        final String _LESSON_NAME = "lesson_name";

        try{
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");

            for (int i = 0; i < m_jArry.length(); i++) {

                String name, id;
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                id = jo_inside.getString(_LESSON_ID);
                name = jo_inside.getString(_LESSON_NAME);



                ContentValues cv = new ContentValues();
                cv.put(LESSON_NAME, name);
                cv.put(LESSON_ID, id);

                db.insert(LESSON_TABLE, null, cv);

            }
        }
        catch (JSONException e){

         e.printStackTrace();

        }



    }


    //get the JSON file of lessons
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("lessons.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


}
