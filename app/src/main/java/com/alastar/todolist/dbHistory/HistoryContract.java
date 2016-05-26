package com.alastar.todolist.dbHistory;
import android.provider.BaseColumns;
/**
 * Created by alast on 15.05.2016.
 */
public class HistoryContract {
    public static final String DB_NAME = "com.aziflaj.todolist.dbHistory";
    public static final int DB_VERSION = 1;

    public class HistEntry implements BaseColumns {
        public static final String TABLE = "history";

        public static final String COL_TASK_TITLE = "title";
    }
}




