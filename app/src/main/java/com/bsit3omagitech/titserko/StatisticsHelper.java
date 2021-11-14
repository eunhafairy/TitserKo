package com.bsit3omagitech.titserko;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class StatisticsHelper {

    private DataBaseHelper db;
    Context ct;
    public StatisticsHelper(DataBaseHelper db, Context ct) {
        this.db = db;
        this.ct = ct;
    }


    // ------------------------------------------ FOR OVERALL PROGRESS METHODS ------------------------------------------
    public int getOverallProgress(String username){

        float percent = 0;
        float a = db.getUserTotalStars(username);
        float b = (db.getAllLessonId().size() * 3);
        percent = ((a/b) * 100);
        return (int) percent;

    }

    public int userTotalStars(String username){
        return db.getUserTotalStars(username);
    }

    public int userTotalBadges(String username){
        int badges = 0;
        badges = db.getTotalUserBadge(username);
        return badges;
    }

    public int getLessonFinished(String username){

        return db.getLessonFinished(username);

    }



}
