package com.alastar.todolist.dbHistory;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by alast on 15.05.2016.
 */
public class HistoryDbHelper extends SQLiteOpenHelper {
    public HistoryDbHelper(Context context) {
        super(context, HistoryContract.DB_NAME, null, HistoryContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + HistoryContract.HistEntry.TABLE + " ( " +
                HistoryContract.HistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HistoryContract.HistEntry.COL_TASK_TITLE + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + HistoryContract.HistEntry.TABLE);
        onCreate(db);
    }
}
