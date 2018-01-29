package com.example.android.goalist;

/**
 * Created by DELL on 23-10-2017.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class TodoProvider extends ContentProvider {

    private TodoDbHelper mDbHelper;

    private static final int TODOLIST = 100;
    private static final int TODOLIST_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(TodoContract.CONTENT_AUTHORITY,TodoContract.PATH_TODO,TODOLIST);
        sUriMatcher.addURI(TodoContract.CONTENT_AUTHORITY,TodoContract.PATH_TODO+"/#",TODOLIST_ID);
    }

    /** Tag for the log messages */
    public static final String LOG_TAG = TodoProvider.class.getSimpleName();

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder)  {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

            switch (match){

                //ENTIRE TABLE URI REFERENCE
                case TODOLIST:

                    cursor = database.query(TodoContract.TodoEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                    break;

                //SINGLE ROW URI REFERENCE
                case TODOLIST_ID:

                    selection = TodoContract.TodoEntry._ID + "=?";
                    selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                    cursor = database.query(TodoContract.TodoEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                    break;

                default:

                throw  new IllegalArgumentException("Cannot query unknown URI"+uri);
            }
            //SET NOTIFICATION URI ON CURSOR
            //IF DATA AT THIS URI CHANGES THEN WE KNOW WE NEED TO UPDATE CURSOR
            cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
        }


    @Override
    public boolean onCreate() {
         mDbHelper = new TodoDbHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        int match = sUriMatcher.match(uri);
        switch (match){
            case TODOLIST:
                return insertTodo(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not Supported for "+ uri);
        }
    }

    private Uri insertTodo(Uri uri,ContentValues contentValues){

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(TodoContract.TodoEntry.TABLE_NAME,null,contentValues);

        if(id == -1){
            Log.e(LOG_TAG,"FAILED TO INSERT"+uri);
            return null;
        }
        //NOTFIY ALL LISTENERS THAT THE DATA HAS CHANGED FOR CONTENT URI
        getContext().getContentResolver().notifyChange(uri,null);

    //return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case TODOLIST:
                return updateTodo(uri,contentValues,selection,selectionArgs);
            case TODOLIST_ID:
                selection = TodoContract.TodoEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateTodo(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported "+uri);
        }
    }

    private int updateTodo(Uri uri,ContentValues contentValues,String selection,String[] selectionArgs){

        if(contentValues.containsKey(TodoContract.TodoEntry.COLUMN_TITLE)){
            String title = contentValues.getAsString(TodoContract.TodoEntry.COLUMN_TITLE);
            if(title==null){
                throw new IllegalArgumentException("Todo item requires at least Title and Description");
            }
        }

        if(contentValues.containsKey(TodoContract.TodoEntry.COLUMN_DESCRIPTION)){
            String description = contentValues.getAsString(TodoContract.TodoEntry.COLUMN_DESCRIPTION);
            if(description==null){
                throw new IllegalArgumentException("Todo item requires at least Description and Title");
            }
        }

        if(contentValues.size()==0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowId =  database.update(TodoContract.TodoEntry.TABLE_NAME,contentValues,selection,selectionArgs);

        if(rowId != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowId;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDelete;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case TODOLIST:
                rowsDelete =  database.delete(TodoContract.TodoEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case TODOLIST_ID:
                selection = TodoContract.TodoEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDelete= database.delete(TodoContract.TodoEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion not supported for "+uri);
        }

        if(rowsDelete != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDelete;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}