package com.example.android.goalist;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DELL on 22-10-2017.
 */

public final class TodoContract {

    private TodoContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.goalist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_TODO = "Todolist";

    public static abstract class TodoEntry implements BaseColumns{

        public static final String TABLE_NAME = "Todolist";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_DESCRIPTION = "Description";
        public static final String COLUMN_DUEDATE = "Duedate";
        public static final String COLUMN_TIME = "Time";
        public static final String COLUMN_REMINDER = "Reminder";
        public static final String COLUMN_PRIORITY = "Priority";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_TODO);

        /*
        VALUES FOR RADIO BUTTON PRIORITY
         */

        public static final int PRIORITY_NORMAL = 3;
        public static final int PRIORITY_MEDIUM = 2;
        public static final int PRIORITY_HIGH = 1;
        public static final int PRIORITY_NONE = 0;

        public static final int REMINDER_ON = 1;
        public static final int REMINDER_OFF = 0;

    }
}
