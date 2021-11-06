package com.bsit3omagitech.titserko;


import android.app.Dialog;
import android.content.ContentValues;
import android.database.SQLException;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;
import android.app.Dialog;
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
    public static final String COLUMN_ACHIEVEMENT_ID = "COLUMN_ACHIEVEMENT_ID";

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
    public static final String LP_LESSON_STARS = "LP_LESSON_STAR"; //RECORD OF USER'S PROGRESS IN PERCENTAGE, INT
    public static final String LP_LESSON_MAX = "LP_LESSON_MAX"; //RECORD OF USER'S PROGRESS IN PERCENTAGE, INT
    public static final String LP_QUIZ_LOWSWCORE= "LP_QUIZ_LOWSWCORE";
    public static final String LP_QUIZ_TAKEN = "LP_QUIZ_TAKEN";
    //user-achievement_record
    public static final String ACHIEVEMENT_TABLE = "achievements";
    public static final String ACHIEVEMENT_PRIMARY = "achievements_primary"; //AUTO
    public static final String ACHIEVEMENT_ID = "achievements_ID"; //ID FROM JSON
    public static final String ACHIEVEMENT_USER = "achievements_user"; //user
    public static final String ACHIEVEMENT_FLAG = "achievements_flag"; // boolean
    public Dialog dialog;
    LinkedBlockingQueue<Dialog> dialogsToShow = new LinkedBlockingQueue<>();
    public static final int DB_VERSION = 41;
    Context context;
    GlobalFunctions gf;

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
                + COLUMN_ACHIEVEMENT_ID + " TEXT, "
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
                + LP_LESSON_PROGRESS + " INTEGER, "
                + LP_LESSON_MAX + " INTEGER, "
                + LP_QUIZ_LOWSWCORE + " INTEGER, "
                + LP_QUIZ_TAKEN + " INTEGER, "
                + LP_LESSON_STARS + " INTEGER)";

        try {
            db.execSQL(createProgressTableStatement);
            i++;
        } catch (SQLException e) {

            e.printStackTrace();
        }

        //FOR USER-LESSON-PROGRESS
        String createAchievementTableStatement = "CREATE TABLE "
                + ACHIEVEMENT_TABLE + " ("
                + ACHIEVEMENT_PRIMARY + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ACHIEVEMENT_ID + " TEXT, "
                + ACHIEVEMENT_USER + " TEXT, "
                + ACHIEVEMENT_FLAG + " BOOLEAN)";

        try {
            db.execSQL(createAchievementTableStatement);
            i++;
        } catch (SQLException e) {

            e.printStackTrace();
        }


        if (i >= 4){
         Toast.makeText(context, "Table created", Toast.LENGTH_SHORT).show();

        }
        Log.d("what is i", "i is: "+i);
        Log.d("progress", createProgressTableStatement);
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
        cv.put(COLUMN_ACHIEVEMENT_ID, "A00000");
        long insert = db.insert(USER_TABLE, null, cv);

        if (insert == -1) {
            return false;

        } else {

            return true;


        }
    }

    public int getTotalUserBadge(String username){
        int badge = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + ACHIEVEMENT_FLAG + " FROM " + ACHIEVEMENT_TABLE+ " WHERE "+ACHIEVEMENT_USER + " = '" + username+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                boolean flag = cursor.getInt(0) > 0;
                if(flag){
                    badge++;
                }

            } while (cursor.moveToNext());
        }

        return badge;

    }

    public List<String> getProfileBadges(String username){

        List<String> imagePaths = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + ACHIEVEMENT_ID + ", "+ ACHIEVEMENT_FLAG + " FROM " + ACHIEVEMENT_TABLE+ " WHERE "+ACHIEVEMENT_USER + " = '" + username+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                boolean flag = cursor.getInt(1) > 0;

                if(flag){

                    String achieve_id = cursor.getString(0), imageurl = "";


                    try{
                        JSONObject obj = new JSONObject(loadJSONFromAsset("achievements.json"));
                        JSONArray m_jArry = obj.getJSONArray("achievements");

                        for (int i = 0; i < m_jArry.length(); i++) {
                            JSONObject jo_inside = m_jArry.getJSONObject(i);
                            String _id = jo_inside.getString("achieve_id");
                            if(_id.equals(achieve_id)){
                                imageurl = jo_inside.getString("achieve_img");
                                break;
                            }
                        }
                    }
                    catch (JSONException e){

                        e.printStackTrace();

                    }


                   imagePaths.add(imageurl);

                }
                else{
                    imagePaths.add("lockedbadge");

                }

            } while (cursor.moveToNext());
        }



        return imagePaths;

    }

    public Uri getUserBadge(String username){



        String imageurl = "", achieve_id = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + COLUMN_ACHIEVEMENT_ID + " FROM " + USER_TABLE+ " WHERE "+COLUMN_USER_NAME + " = '" + username+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                achieve_id = cursor.getString(0);
            } while (cursor.moveToNext());
        }


        //--------------PARSE THROUGH JSON
        try{
            JSONObject obj = new JSONObject(loadJSONFromAsset("achievements.json"));
            JSONArray m_jArry = obj.getJSONArray("achievements");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String _id = jo_inside.getString("achieve_id");
                if(_id.equals(achieve_id)){
                    imageurl = jo_inside.getString("achieve_img");
                    break;
                }
            }
        }
        catch (JSONException e){

            e.printStackTrace();

        }

        Uri imageUri =Uri.parse("android.resource://com.bsit3omagitech.titserko/raw/" + imageurl);
        return imageUri;
    }


    public String getCurrentUsername(int userID){

        String username = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + COLUMN_USER_NAME + " FROM " + USER_TABLE+ " WHERE "+COLUMN_ID + " = '" + userID+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                username = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return username;
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

    public void saveHighscore(String userName, String lessonId, int score){

        int lp_id = 0, currentHighscore = 0, currentLowscore = 0, ctr = 0;

        String selectQuery = "SELECT " + LP_ID + ", "+ LP_QUIZ_HIGHSCORE +", "+LP_QUIZ_LOWSWCORE+ ", "+LP_QUIZ_TAKEN+" FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + userName + "' AND " + LP_LESSON_ID + " = '" + lessonId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                lp_id =  cursor.getInt(0);
                currentHighscore = cursor.getInt(1);
                currentLowscore = cursor.getInt(2);
                ctr = cursor.getInt(3);
            } while (cursor.moveToNext());
        }


        if(score > currentHighscore){

            //update the highscore
            String updateQuery = "UPDATE " + LESSON_PROGRESS_TABLE
                    + " SET " + LP_QUIZ_HIGHSCORE + " = " + score
                    + " WHERE " + LP_ID + " = '" + lp_id + "'";

            SQLiteDatabase db1 = this.getWritableDatabase();
            db1.execSQL(updateQuery);


        }

        if(ctr == 0){

            //update the lowscore regardless
            String updateQuery = "UPDATE " + LESSON_PROGRESS_TABLE
                    + " SET " + LP_QUIZ_LOWSWCORE + " = " + score
                    + " WHERE " + LP_ID + " = '" + lp_id + "'";

            SQLiteDatabase db1 = this.getWritableDatabase();
            db1.execSQL(updateQuery);

        }
        else if(score < currentLowscore){
            //update the lowscore
            String updateQuery = "UPDATE " + LESSON_PROGRESS_TABLE
                    + " SET " + LP_QUIZ_LOWSWCORE + " = " + score
                    + " WHERE " + LP_ID + " = '" + lp_id + "'";

            SQLiteDatabase db1 = this.getWritableDatabase();
            db1.execSQL(updateQuery);


        }

        ctr++;
        String updateQuery = "UPDATE " + LESSON_PROGRESS_TABLE
                + " SET " + LP_QUIZ_TAKEN + " = " + ctr
                + " WHERE " + LP_ID + " = '" + lp_id + "'";

        SQLiteDatabase db1 = this.getWritableDatabase();
        db1.execSQL(updateQuery);

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


            ContentValues cv = new ContentValues();

            cv.put(LP_USER_NAME, userName);
            cv.put(LP_LESSON_ID, lessonID);
            cv.put(LP_QUIZ_HIGHSCORE, 0);
            cv.put(LP_LESSON_PROGRESS, 0);
            cv.put(LP_LESSON_STARS, 0);
            cv.put(LP_QUIZ_LOWSWCORE, 0);
            cv.put(LP_QUIZ_TAKEN, 0);
            cv.put(LP_LESSON_MAX, getMaxIndex(lessonID));

            long insert = db.insert(LESSON_PROGRESS_TABLE, null, cv);

            if (insert == -1) {
              Log.d(TAG, "not inserted.");

            } else {
                Log.d(TAG, "inserted.");

            }


        }


    }

    public int getMaxScore(String lessonId){
        int score = 0;

        try{
            JSONObject obj = new JSONObject(loadJSONFromAsset("lessons.json"));
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");

            for (int i = 0; i < m_jArry.length(); i++) {

                String id;
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                id = jo_inside.getString("lesson_id");
                if(id.equals(lessonId))
                {

                    score = jo_inside.getJSONArray("quiz").length();
                    break;
                }

            }
        }
        catch (JSONException e){

            e.printStackTrace();

        }

        return score;
    }

    public int getMaxIndex(String lessonId){
        int index = 0;

        try{
            JSONObject obj = new JSONObject(loadJSONFromAsset("lessons.json"));
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");

            for (int i = 0; i < m_jArry.length(); i++) {

                String id;
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                id = jo_inside.getString("lesson_id");
                if(id.equals(lessonId))
                {

                    index = jo_inside.getJSONArray("parts").length();
                    break;
                }

            }
        }
        catch (JSONException e){

            e.printStackTrace();

        }

     return index-1;
    }

    public int getQuizProgress(String username,String lessonId){

        int i = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_QUIZ_HIGHSCORE + " FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + username + "' AND " + LP_LESSON_ID + " = '" + lessonId  + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                i =  cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return i;

    }

    //create all lesson progress at user registration
    public void createAllLessonProgress(String username){


        List<String> lessonIds = getAllLessonId();
        // LOOP INSERT INTO

        SQLiteDatabase db1 = this.getWritableDatabase();
        int ctr = lessonIds.size();
        for(int i = 0; i < ctr; i++){
            ContentValues cv = new ContentValues();
            cv.put(LP_USER_NAME, username);
            cv.put(LP_LESSON_ID, lessonIds.get(i));
            cv.put(LP_LESSON_PROGRESS, 0);
            cv.put(LP_QUIZ_HIGHSCORE, 0);
            cv.put(LP_LESSON_STARS, 0);
            cv.put(LP_QUIZ_TAKEN, 0);
            cv.put(LP_QUIZ_LOWSWCORE, 0);
            cv.put(LP_LESSON_MAX, getMaxIndex(lessonIds.get(i)));
            db1.insert(LESSON_PROGRESS_TABLE, null, cv);
        }


    }

    public void createAllAchievements(String username){

        List<String> achievementIds = getAllAchievementId();
        SQLiteDatabase db = this.getWritableDatabase();
        //---------------LOOP INSERT INTO STATEMENT-----------------------
        int ctr = achievementIds.size();
        for(int i = 0; i < ctr; i++){

            ContentValues cv = new ContentValues();
            cv.put(ACHIEVEMENT_USER, username);
            cv.put(ACHIEVEMENT_ID, achievementIds.get(i));
            if(i == 0){
                    cv.put(ACHIEVEMENT_FLAG, 1);

            }
            else{
                cv.put(ACHIEVEMENT_FLAG, 0);
            }
            db.insert(ACHIEVEMENT_TABLE, null, cv);
        }




    }

    public List<String> getAllAchievementId(){
        List<String> achievementId = new ArrayList<>();

        try{
            JSONObject obj = new JSONObject(loadJSONFromAsset("achievements.json"));
            JSONArray m_jArry = obj.getJSONArray("achievements");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                achievementId.add(jo_inside.getString("achieve_id"));
            }
        }
        catch (JSONException e){

            e.printStackTrace();

        }
        return achievementId;

    }

    public List<Integer> getStars(){
        List<Integer> stars = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_LESSON_STARS + " FROM "
                + LESSON_PROGRESS_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                stars.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        return stars;
    }

    public List<Integer> getUserStars(String username){
        List<Integer> stars = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_LESSON_STARS + " FROM "
                + LESSON_PROGRESS_TABLE +
                " WHERE " +
                LP_USER_NAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                stars.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        return stars;
    }

    public boolean checkExisting(String username){


        SQLiteDatabase dbRead = this.getReadableDatabase();
        String checkUsername = "SELECT " + COLUMN_USER_NAME + " FROM " + USER_TABLE;
        Cursor cursor = dbRead.rawQuery(checkUsername, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(username)) {
                    return true;
                }
            } while (cursor.moveToNext());
        }

        return false;

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

    //------------------- METHODS FOR LIST RETURNS
    public List<Integer> getAllLessonProgress(String username){

        List<Integer> i = new ArrayList<>();
        float a, b, c;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_LESSON_PROGRESS + ", "+LP_LESSON_MAX+" FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                a = (float) cursor.getInt(0);
                b = (float) cursor.getInt(1);
                c = (a / b) * 100;
                i.add((int)c);
            } while (cursor.moveToNext());
        }
        return i;

    }

    public List<Integer> getAllQuizHighscore(String username){

        List<Integer> i = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_QUIZ_HIGHSCORE + " FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                i.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        return i;

    }

    public List<Integer> getAllQuizLowscore(String username){

        List<Integer> i = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_QUIZ_LOWSWCORE + " FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                i.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        return i;

    }

    public List<Integer> getAllQuizTaken(String username){

        List<Integer> i = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_QUIZ_TAKEN + " FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                i.add(cursor.getInt(0));
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
            db1.execSQL(updateQuery);

        }


    }

    public void updateBadge(String username, String achievementId){

        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + USER_TABLE
                + " SET " + COLUMN_ACHIEVEMENT_ID + " = '" + achievementId+"'"
                + " WHERE " + COLUMN_USER_NAME + " = '" + username+"'";
        db.execSQL(updateQuery);

    }

    public boolean isEquipped(String username, String achievementId){

        boolean isEquipped = false;
       //get the achievement id in user table
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + COLUMN_ACHIEVEMENT_ID + " FROM " + USER_TABLE + " WHERE " + LP_USER_NAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String _id =  cursor.getString(0);
                if(_id.equals(achievementId)){
                    isEquipped =true;
                    break;
                }

            } while (cursor.moveToNext());
        }

        return isEquipped;
    }





    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop table

        final String DROP_USER_TABLE  = "DROP TABLE IF EXISTS "+ USER_TABLE;
        final String DROP_LESSON_TABLE  = "DROP TABLE IF EXISTS "+ LESSON_TABLE;
        final String DROP_LESSON_PROGRESS_TABLE  = "DROP TABLE IF EXISTS "+ LESSON_PROGRESS_TABLE;
        final String DROP_ACHIEVEMENT_TABLE  = "DROP TABLE IF EXISTS "+ ACHIEVEMENT_TABLE;
        try{

            db.execSQL(DROP_USER_TABLE);
            db.execSQL(DROP_LESSON_TABLE);
            db.execSQL(DROP_LESSON_PROGRESS_TABLE);
            db.execSQL(DROP_ACHIEVEMENT_TABLE);
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
            JSONObject obj = new JSONObject(loadJSONFromAsset("lessons.json"));
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");

            for (int i = 0; i < m_jArry.length(); i++) {

                String name, id;
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                id = jo_inside.getString(_LESSON_ID);
                name = jo_inside.getString(_LESSON_NAME);



                ContentValues cv = new ContentValues();
                cv.put(LESSON_ID, id);
                cv.put(LESSON_NAME, name);


                db.insert(LESSON_TABLE, null, cv);

            }
        }
        catch (JSONException e){

         e.printStackTrace();

        }



    }

    public Boolean isUnlocked(String id, String username){

        boolean isUnlock = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + ACHIEVEMENT_FLAG + " FROM " + ACHIEVEMENT_TABLE+ " WHERE " + ACHIEVEMENT_USER + " = '" + username + "' AND "+ ACHIEVEMENT_ID + " = '" + id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                boolean flag = cursor.getInt(0) > 0 ;
                if(flag){
                    isUnlock = true;
                }

            } while (cursor.moveToNext());
        }

        return isUnlock;
    }

    public void refreshAllStars(String username){

        //lessons have stars for progress
        //zero stars means it isnt touched
        //one star means the lesson progress is finished
        //two stars if the quiz have passing score
        //three stars if the quiz is perfected

        //select all lesson progress with the current user
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_ID + " FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        //store all LP_ID to a list of array for ease of access first
        List<Integer> lessonProgress_ID = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
               //first, check if lesson is finished for one star
                lessonProgress_ID.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        //check now for individual entries
        for(int i = 0; i < lessonProgress_ID.size(); i++){
            ContentValues _cv = getInformation(lessonProgress_ID.get(i));
            int _progress = (int) _cv.get("progress");
            int _highscore = (int) _cv.get("highscore");
            String _lesson = (String)_cv.get("lesson");


            int currentStar = 0;
            //check for progress first and flag for accomplishments

            if(_progress >= getMaxIndex(_lesson))
                {
                    Log.d("half", "flagg1!");
                    currentStar++;

                }


                if(_highscore >= (getMaxScore(_lesson)/2)){

                    currentStar++;
                    Log.d("half", "flagg2!");
                }


                if(_highscore >= getMaxScore(_lesson)){

                    currentStar++;
                }

                updateStar(lessonProgress_ID.get(i), currentStar);


            }

        }

   public Date getUserBirthday(String username){


     String dt = "";
       SQLiteDatabase db = this.getReadableDatabase();
       String selectQuery = "SELECT " + COLUMN_USER_BDAY + " FROM " + USER_TABLE + " WHERE " + COLUMN_USER_NAME + " = '" + username + "'";
       Cursor cursor = db.rawQuery(selectQuery, null);

       if (cursor.moveToFirst()) {
           do {
               Log.d("bday", "cursor.getString: "+cursor.getString(0));


                   dt = (cursor.getString(0));



           } while (cursor.moveToNext());
       }
       SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
       Date date = null;
       try {
           date  = format.parse(dt);

       } catch (ParseException e) {
           e.printStackTrace();
       }



       return date;

   }

   public boolean checkAchievement(String username, String achievementId){

        boolean flag = false;
       SQLiteDatabase db = this.getReadableDatabase();
       String selectQuery = "SELECT " + ACHIEVEMENT_FLAG + " FROM " + ACHIEVEMENT_TABLE + " WHERE " + ACHIEVEMENT_USER + " = '" + username + "' AND "+ACHIEVEMENT_ID+" = '"+achievementId+"'";
       Cursor cursor = db.rawQuery(selectQuery, null);

       if (cursor.moveToFirst()) {
           do {
               flag = cursor.getInt(0) > 0;
           } while (cursor.moveToNext());
       }

        return flag;
   }


    public List<String> refreshAchievements(String username){

        List<String> updatedAchievements = new ArrayList<>();

        //---------------- FOR ACHIEVEMENT A00001---------------------
            /**
             *          "achieve_id" : "A00001"
             *         "achieve_name" : "First Steps!",
             *         "achieve_desc" : "Start your first lesson.",
             *
             **/
        int a = getLessonStar(username, "00001");
        if(a > 0 && !checkAchievement(username, "A00001")){
            updatedAchievements.add("A00001");
            updateAchievement(username, "A00001");
        }

        //---------------- FOR ACHIEVEMENT A00002---------------------
        /**
         *         "achieve_id" : "A00002",
         *         "achieve_name" : "Born A Star I",
         *         "achieve_desc" : "Acquire a total of three stars.",
         *
         **/
        a = getUserTotalStars(username);
        if(a >=3 && !checkAchievement(username, "A00002")){
            updatedAchievements.add("A00002");
            updateAchievement(username, "A00002");

        }

        //---------------- FOR ACHIEVEMENT A00003---------------------
        /**
         *          "achieve_id" : "A00003",
         *         "achieve_name" : "Born A Star II",
         *         "achieve_desc" : "Acquire a total of ten stars.",
         *
         **/

        a = getUserTotalStars(username);
        if(a >=10 && !checkAchievement(username, "A00003")){
            updatedAchievements.add("A00003");
            updateAchievement(username, "A00003");

        }

        //---------------- FOR ACHIEVEMENT A00004---------------------
        /**
         *         "achieve_id" : "A00004",
         *         "achieve_name" : "Born A Star III",
         *         "achieve_desc" : "Acquire a total of twenty stars.",
         *
         **/

        a = getUserTotalStars(username);
        if(a >= 20 && !checkAchievement(username, "A00004") ){
            updatedAchievements.add("A00004");
            updateAchievement(username, "A00004");

        }

        //---------------- FOR ACHIEVEMENT A00005---------------------
        /**
         *          "achieve_id" : "A00005",
         *         "achieve_name" : "Ace!",
         *         "achieve_desc" : "Get a perfect score on a quiz.",
         *
         **/
        a = perfectScoreCounter(username);
        if(a > 0 && !checkAchievement(username, "A00005")){
            updatedAchievements.add("A00005");
            updateAchievement(username, "A00005");
        }


        //---------------- FOR ACHIEVEMENT A00006---------------------
        /**
         *          "achieve_id" : "A00006",
         *         "achieve_name" : "Fly high!",
         *         "achieve_desc" : "Get three perfect score on quizzes.",
         *
         **/
        a = perfectScoreCounter(username);
        if(a >= 3 && !checkAchievement(username, "A00006")){
            updatedAchievements.add("A00006");
            updateAchievement(username, "A00006");
        }

        //---------------- FOR ACHIEVEMENT A00007---------------------
        /**
         *           "achieve_id" : "A00007",
         *         "achieve_name" : "People Everywhere!",
         *         "achieve_desc" : "Get 100% progress and a perfect score on \"People\" lesson",
         *
         **/
        a = getLessonStar(username, "00001");
        if(a >= 3 && !checkAchievement(username, "A00007")){
            updatedAchievements.add("A00007");
            updateAchievement(username, "A00007");
        }

        //---------------- FOR ACHIEVEMENT A00008---------------------
        /**
         *         "achieve_id" : "A00008",
         *         "achieve_name" : "Animal Lover!",
         *         "achieve_desc" : "Get 100% progress and a perfect score on \"Animal\" lesson",
         *
         **/

        a = getLessonStar(username, "00002");
        if(a >= 3 && !checkAchievement(username, "A00008")){
            updatedAchievements.add("A00008");
            updateAchievement(username, "A00008");
        }

        //---------------- FOR ACHIEVEMENT A10000---------------------
        /**
         *         "achieve_id" : "A10000",
         *         "achieve_name" : "Master",
         *         "achieve_desc" : "Get 100% progress on all lessons.",
         *
         **/

        a = getUserTotalStars(username);
        int b = getAllLessonId().size();

        if(a >= (b*3) && !checkAchievement(username, "A10000")){
            updatedAchievements.add("A10000");
            updateAchievement(username, "A10000");
        }


        return updatedAchievements;


    }



    public List<String> getAchievementInfo(String achieve_id){
        List<String> info = new ArrayList<>();
        try{
            JSONObject obj = new JSONObject(loadJSONFromAsset("achievements.json"));
            JSONArray m_jArry = obj.getJSONArray("achievements");

            for (int j = 0; j < m_jArry.length(); j++) {
                JSONObject jo_inside = m_jArry.getJSONObject(j);
                String _id = jo_inside.getString("achieve_id");
                if(_id.equals(achieve_id)){
                    info.add(jo_inside.getString("achieve_name"));
                    info.add(jo_inside.getString("achieve_desc"));
                    info.add(jo_inside.getString("achieve_img"));
                    break;

                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    return info;
    }


    public void updateAchievement(String username, String achievementID){

        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + ACHIEVEMENT_TABLE
                + " SET " + ACHIEVEMENT_FLAG + " = 1"
                + " WHERE " + ACHIEVEMENT_USER + " = '" + username+"' AND "+
                ACHIEVEMENT_ID + " = '" + achievementID+"'";
        db.execSQL(updateQuery);


    }

    public int getUserTotalStars(String username){

        int totalStars = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_LESSON_STARS + " FROM "
                + LESSON_PROGRESS_TABLE +
                " WHERE " +
                LP_USER_NAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                totalStars+= cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        return totalStars;
    }
    public void updateStar(int lessonprogress_ID, int currentStar){

        String updateQuery = "UPDATE " + LESSON_PROGRESS_TABLE
                + " SET " + LP_LESSON_STARS + " = " + currentStar
                + " WHERE " + LP_ID + " = " + lessonprogress_ID;
        SQLiteDatabase db1 = this.getWritableDatabase();
        db1.execSQL(updateQuery);

    }

    public int perfectScoreCounter(String username){

        int ctr = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_QUIZ_HIGHSCORE + ", "+ LP_LESSON_ID +
                " FROM "
                + LESSON_PROGRESS_TABLE +
                " WHERE " +
                LP_USER_NAME + " = '" + username + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int maxScore = getMaxScore(cursor.getString(1));
                if(cursor.getInt(0) >= maxScore){
                    ctr++;
                }

            } while (cursor.moveToNext());
        }

        return ctr;
    }

    public void updateNewAchievements(String username){
        //first get all achievement id
        List<String> achieveIds = getAllAchievementId();
        //check if user's achievement progress of each achievement is existing
        for(int x = 0; x < achieveIds.size(); x++) {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT " + ACHIEVEMENT_PRIMARY +
                    " FROM "
                    + ACHIEVEMENT_TABLE +
                    " WHERE " +
                    ACHIEVEMENT_USER + " = '" + username + "' AND " +
                    ACHIEVEMENT_ID + " = '" + achieveIds.get(x) + "'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if(cursor.getCount() == 0) {

                //create entry
                SQLiteDatabase db2 = this.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(ACHIEVEMENT_USER, username);
                cv.put(ACHIEVEMENT_ID, achieveIds.get(x));
                cv.put(ACHIEVEMENT_FLAG, 0);
                db2.insert(ACHIEVEMENT_TABLE, null, cv);

            }
        }

    }



    public void updateNewLessons(String username){

        //first get all lesson id
        List<String> lessonids = getAllLessonId();

        //check if user's lesson progress of each lessonid is existing

        for(int x = 0; x < lessonids.size(); x++){
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT " + LP_ID +
                    " FROM "
                    + LESSON_PROGRESS_TABLE +
                    " WHERE " +
                    LP_USER_NAME + " = '" + username + "' AND "+
                    LP_LESSON_ID + " = '" + lessonids.get(x) + "'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if(cursor.getCount() == 0){
                //create entry

                //insert entry


                ContentValues cv = new ContentValues();

                cv.put(LP_USER_NAME, username);
                cv.put(LP_LESSON_ID, lessonids.get(x));
                cv.put(LP_QUIZ_HIGHSCORE, 0);
                cv.put(LP_LESSON_PROGRESS, 0);
                cv.put(LP_LESSON_STARS, 0);
                cv.put(LP_QUIZ_LOWSWCORE, 0);
                cv.put(LP_QUIZ_TAKEN, 0);
                cv.put(LP_LESSON_MAX, getMaxIndex(lessonids.get(x)));

                SQLiteDatabase db2 = this.getWritableDatabase();
                long insert = db2.insert(LESSON_PROGRESS_TABLE, null, cv);
                if (insert == -1) {
                    Log.d(TAG, "not inserted.");

                } else {
                    Log.d(TAG, "inserted.");

                }

            }

        }


    }



    public int getLessonStar(String username, String lessonId){

        int i = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_LESSON_STARS + " FROM " + LESSON_PROGRESS_TABLE + " WHERE " + LP_USER_NAME + " = '" + username + "' AND " + LP_LESSON_ID + " = '" + lessonId  + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                i =  cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return i;

    }

    public ContentValues getInformation(int lessonProgress_ID){

      ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_LESSON_STARS + ", "+LP_LESSON_PROGRESS+", "
                +LP_QUIZ_HIGHSCORE+", "+LP_LESSON_ID+" FROM "
                + LESSON_PROGRESS_TABLE + " WHERE "
                + LP_ID + " = " + lessonProgress_ID;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                //first, check if lesson is finished for one star
                cv.put("stars",cursor.getInt(0));
                cv.put("progress",cursor.getInt(1));
                cv.put("highscore",cursor.getInt(2));
                cv.put("lesson", cursor.getString(3));
            } while (cursor.moveToNext());
        }

        return cv;
    }

    public int getLessonFinished(String username){

        int ctr = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + LP_LESSON_PROGRESS + ", "+LP_LESSON_MAX
                +" FROM "
                + LESSON_PROGRESS_TABLE + " WHERE "
                + LP_USER_NAME + " = '" + username+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getInt(0) >= cursor.getInt(1)){
                    ctr++;
                }
            } while (cursor.moveToNext());
        }

        return ctr;
    }

    public List<String> getAllLessonId(){

        List<String> lessonIds = new ArrayList<>();

        try{
            JSONObject obj = new JSONObject(loadJSONFromAsset("lessons.json"));
            JSONArray m_jArry = obj.getJSONArray("lesson_arr");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                lessonIds.add(jo_inside.getString("lesson_id"));
            }
        }
        catch (JSONException e){

            e.printStackTrace();

        }
        return lessonIds;

    }


    //get the JSON file of lessons
    public String loadJSONFromAsset(String name) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(name);
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
