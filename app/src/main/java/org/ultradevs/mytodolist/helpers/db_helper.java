package org.ultradevs.mytodolist.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.ultradevs.mytodolist.utils.UserContract.USERS_ENTERY;

import static org.ultradevs.mytodolist.activities.MainActivity.LOG_TAG;
import static org.ultradevs.mytodolist.utils.UserContract.USERS_ENTERY.TABLE_NAME;

public class db_helper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UsersDB.db";
    private static final int VERSION = 1;

    public db_helper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            // Create Users table (careful to follow SQL formatting rules)

            final String CREATE_USERS_TABLE = "CREATE TABLE "  + TABLE_NAME + " (" +
                    USERS_ENTERY._ID                + " INTEGER PRIMARY KEY, " +
                    USERS_ENTERY.COLUMN_NAME + " VARCHAR(255) NOT NULL, " +
                    USERS_ENTERY.COLUMN_PASSWORD + " VARCHAR(255) NOT NULL, " +
                    USERS_ENTERY.COLUMN_EMAIL + " EMAIL NOT NULL, " +
                    USERS_ENTERY.COLUMN_STATUS + " INT, " +
                    USERS_ENTERY.COLUMN_GENDER    + " VARCHAR(255) NOT NULL);";

            db.execSQL(CREATE_USERS_TABLE);
    }

    public int getUsersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getNumOfLog() {
        String countQuery = "SELECT  *  FROM " + TABLE_NAME + " WHERE " + USERS_ENTERY.COLUMN_STATUS + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[] { "1" } );
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
