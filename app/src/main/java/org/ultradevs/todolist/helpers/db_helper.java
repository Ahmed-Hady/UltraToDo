package org.ultradevs.todolist.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.ultradevs.todolist.utils.UserContract.USERS_ENTERY;

import static org.ultradevs.todolist.activities.MainActivity.LOG_TAG;

public class db_helper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UltraDB.db";
    private static final int VERSION = 1;

    public db_helper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            // Create Users table (careful to follow SQL formatting rules)
            final String CREATE_USERS_TABLE = "CREATE TABLE "  + USERS_ENTERY.TABLE_NAME + " (" +
                    USERS_ENTERY._ID                + " INTEGER PRIMARY KEY, " +
                    USERS_ENTERY.COLUMN_NAME + " VARCHAR(255) NOT NULL, " +
                    USERS_ENTERY.COLUMN_PASSWORD + " VARCHAR(255) NOT NULL, " +
                    USERS_ENTERY.COLUMN_EMAIL + " EMAIL NOT NULL, " +
                    USERS_ENTERY.COLUMN_GENDER    + " INTEGER NOT NULL);";

            db.execSQL(CREATE_USERS_TABLE);
            db.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
