package com.example.android.goalist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.goalist.TodoContract.TodoEntry;

/**
 * Created by DELL on 22-10-2017.
 */

public class TodoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Goal-ist.db";
    private static final int DATABASE_VERSION = 1;

    public TodoDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    //CREATE TABLE Todolist (_id integer PRIMARY KEY AUTOINCREMENT, Title text NOT NULL, Description text, Duedate Date, Time integer, Reminder integer, Priority integer NOT NULL);
    String SQL_CREATE_TODO_TABLE=
        "CREATE TABLE "+ TodoEntry.TABLE_NAME + "(" + TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TodoEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                TodoEntry.COLUMN_DESCRIPTION + " TEXT, " + TodoEntry.COLUMN_DUEDATE + " INTEGER, " + TodoEntry.COLUMN_TIME  + " TEXT, "+
                TodoEntry.COLUMN_REMINDER  + " INTEGER, " + TodoEntry.COLUMN_PRIORITY + " INTEGER);";

        db.execSQL(SQL_CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
