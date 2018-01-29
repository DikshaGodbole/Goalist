package com.example.android.goalist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DELL on 23-10-2017.
 */

public class TodoCursorAdapter extends CursorAdapter {


    public TodoCursorAdapter(Context context, Cursor c) {
        super(context, c , 0);
    }


    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView titleView = (TextView)view.findViewById(R.id.title);
        TextView descriptionView = (TextView)view.findViewById(R.id.description);
        TextView dateView = (TextView)view.findViewById(R.id.date);
        TextView timeView = (TextView)view.findViewById(R.id.time);
        LinearLayout priorityLayout = (LinearLayout) view.findViewById(R.id.priority_layout);


        String title = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DESCRIPTION));
        long date = cursor.getLong(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DUEDATE));
        Date date1 = new Date(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM", Locale.US);
        String format = simpleDateFormat.format(date1);
        String time = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TIME));
        int priority = cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_PRIORITY));

        if(priority == TodoContract.TodoEntry.PRIORITY_NORMAL){
            priorityLayout.setBackground(view.getResources().getDrawable(R.drawable.priority_noramal));
        }else if(priority == TodoContract.TodoEntry.PRIORITY_MEDIUM){
            priorityLayout.setBackground(view.getResources().getDrawable(R.drawable.priority_medium));
        }else if (priority == TodoContract.TodoEntry.PRIORITY_HIGH){
            priorityLayout.setBackground(view.getResources().getDrawable(R.drawable.priority_high));
        }else if (priority == TodoContract.TodoEntry.PRIORITY_NONE){
            priorityLayout.setBackground(view.getResources().getDrawable(R.drawable.prioriy_none));
        }

        titleView.setText(title);
        descriptionView.setText(description);
        dateView.setText(format);
        timeView.setText(time);
    }
}
