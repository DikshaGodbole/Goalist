package com.example.android.goalist;

import android.app.DatePickerDialog;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.MotionEvent;
import org.w3c.dom.Text;
import android.app.AlertDialog;

import es.dmoral.toasty.Toasty;

/**
 * Created by DELL on 20-10-2017.
 */

public class TodoNewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText Todo_title;
    EditText Todo_description;
    ImageButton Todo_date;
    ImageButton Todo_time;
    SwitchCompat Todo_reminder;
    RadioGroup Priority_radio;
    RadioButton Priority_normal;
    RadioButton Priority_medium;
    RadioButton Priority_high;
    TextView reminder_set;
    TextView reminder_text;
    FloatingActionButton floatingActionButton;

    public static final int TODO_LOADER = 0;
    private Uri mCurrentTodoUri;

    private boolean mTodoChanged = false;

    long date;
    String time = new String();

    Calendar calendar = Calendar.getInstance();


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mTodoChanged = true;
            return false;
       }
   };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);

        Todo_title = (EditText) findViewById(R.id.Todo_title);
        Todo_description = (EditText) findViewById(R.id.Todo_description);
        Todo_date = (ImageButton) findViewById(R.id.newToDoChooseDateButton);
        Todo_time = (ImageButton) findViewById(R.id.newToDoChooseTimeButton);
        Todo_reminder = (SwitchCompat) findViewById(R.id.toDoHasDateSwitchCompat);
        Priority_radio = (RadioGroup) findViewById(R.id.priority_radio);
        Priority_normal = (RadioButton) findViewById(R.id.low_priority);
        Priority_medium = (RadioButton) findViewById(R.id.medium_priority);
        Priority_high = (RadioButton) findViewById(R.id.high_priority);
        reminder_set = (TextView) findViewById(R.id.ReminderSetTextView);
        reminder_text = (TextView) findViewById(R.id.ReminderText);

        Todo_title.setOnTouchListener(mTouchListener);
        Todo_description.setOnTouchListener(mTouchListener);
        Todo_date.setOnTouchListener(mTouchListener);
        Todo_time.setOnTouchListener(mTouchListener);
        Todo_reminder.setOnTouchListener(mTouchListener);
        Priority_radio.setOnTouchListener(mTouchListener);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_todo);
        myToolbar.setTitle("New Todo");
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateTextReminderDate();
            }
        };

        Todo_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TodoNewActivity.this, R.style.MyDatePickerStyle, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
                        Calendar.DAY_OF_MONTH)).show();
            }
        });

        Todo_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerl = new TimePickerDialog(TodoNewActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectHour, int selectedMinute) {
                        reminder_text.setVisibility(View.VISIBLE);
                        reminder_set.setText(selectHour + ":" + selectedMinute);
                        time = selectHour + ":" + selectedMinute;
                    }
                }, hour, minute, true);
                timePickerl.setTitle("Selected Time");
                timePickerl.show();
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.makeToDoFloatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    saveItem();
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    String fragmentString = "Todo";
                    intent.putExtra("FragmentTodo", fragmentString);
                    startActivity(intent);
                }
            }
        });

        Intent intent = getIntent();
        mCurrentTodoUri = intent.getData();

        if (mCurrentTodoUri == null) {
            myToolbar.setTitle("New Todo");
        } else {
            myToolbar.setTitle("Edit Todo");
            getLoaderManager().initLoader(TODO_LOADER, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:

                if(!mTodoChanged){
                    NavUtils.navigateUpFromSameTask(TodoNewActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(TodoNewActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mTodoChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    public boolean validateForm() {
        boolean valid = true;

        String title = Todo_title.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Todo_title.setError("Required.");
            valid = false;
        } else {
            Todo_title.setError(null);
        }

        String description = Todo_description.getText().toString();
        if (TextUtils.isEmpty(description)) {
            Todo_description.setError("Required.");
            valid = false;
        } else {
            Todo_description.setError(null);
        }
        return valid;
    }

    public void updateTextReminderDate() {
        String format = "yyyy/MM/dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        reminder_text.setVisibility(View.VISIBLE);
        reminder_set.setText(simpleDateFormat.format(calendar.getTime()));
        date = calendar.getTimeInMillis();
    }

    public void saveItem() {

        String title = Todo_title.getText().toString().trim();
        String description = Todo_description.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoEntry.COLUMN_TITLE, title);
        values.put(TodoContract.TodoEntry.COLUMN_DESCRIPTION, description);

        //INTEGER VALUES IN MILLISECONDS
        values.put(TodoContract.TodoEntry.COLUMN_DUEDATE, date);
        values.put(TodoContract.TodoEntry.COLUMN_TIME, time);

        if (Priority_radio.getCheckedRadioButtonId() == -1) {
            values.put(TodoContract.TodoEntry.COLUMN_PRIORITY, TodoContract.TodoEntry.PRIORITY_NONE);
        } else {
            int radioId = Priority_radio.getCheckedRadioButtonId();

            if (radioId == R.id.low_priority) {

                values.put(TodoContract.TodoEntry.COLUMN_PRIORITY, TodoContract.TodoEntry.PRIORITY_NORMAL);

            } else if (radioId == R.id.medium_priority) {

                values.put(TodoContract.TodoEntry.COLUMN_PRIORITY, TodoContract.TodoEntry.PRIORITY_MEDIUM);

            } else if (radioId == R.id.high_priority) {

                values.put(TodoContract.TodoEntry.COLUMN_PRIORITY, TodoContract.TodoEntry.PRIORITY_HIGH);
            }
        }

        if (Todo_reminder.isChecked()) {
            values.put(TodoContract.TodoEntry.COLUMN_REMINDER, TodoContract.TodoEntry.REMINDER_ON);
        } else {
            values.put(TodoContract.TodoEntry.COLUMN_REMINDER, TodoContract.TodoEntry.REMINDER_OFF);
        }

       /* Uri newUri = getContentResolver().insert(TodoContract.TodoEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toasty.custom(TodoNewActivity.this, "Database Error", R.drawable.t_error, getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT, true, true).show();

        } else {
            Toasty.custom(TodoNewActivity.this, "To-Do", R.drawable.t_success, getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT, true, true).show();
        }*/

       if(mCurrentTodoUri == null){
           Uri newUri = getContentResolver().insert(TodoContract.TodoEntry.CONTENT_URI,values);
           if (newUri == null) {
               Toasty.custom(TodoNewActivity.this, "Database Error", R.drawable.t_error, getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT, true, true).show();

           } else {
               Toasty.custom(TodoNewActivity.this, "To-Do", R.drawable.t_success, getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT, true, true).show();
           }
       }else{
           int rowsAffected = getContentResolver().update(mCurrentTodoUri,values,null,null);

           if(rowsAffected == 0){
               Toasty.custom(TodoNewActivity.this, "Update Error", R.drawable.t_error, getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT, true, true).show();

           }else{
               Toasty.custom(TodoNewActivity.this, "Updated", R.drawable.t_success, getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT, true, true).show();
           }
       }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                TodoContract.TodoEntry._ID,
                TodoContract.TodoEntry.COLUMN_TITLE,
                TodoContract.TodoEntry.COLUMN_DESCRIPTION,
                TodoContract.TodoEntry.COLUMN_PRIORITY,
                TodoContract.TodoEntry.COLUMN_REMINDER
        };

        return new CursorLoader(this, mCurrentTodoUri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor == null || cursor.getCount() < 1){
            return;
        }

        if(cursor.moveToFirst()){
            int titleColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TITLE);
            int descColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DESCRIPTION);
           // int dateColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DUEDATE);
            //int timeColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TIME);
            int priorityColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_PRIORITY);
            int reminderColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_REMINDER);

            String title = cursor.getString(titleColumnIndex);
            String description = cursor.getString(descColumnIndex);
            //String time = cursor.getString(timeColumnIndex);
            //long date = cursor.getLong(dateColumnIndex);
            int priority = cursor.getInt(priorityColumnIndex);
            int reminder = cursor.getInt(reminderColumnIndex);

            Todo_title.setText(title);
            Todo_description.setText(description);

            switch(priority){
                case TodoContract.TodoEntry.PRIORITY_NORMAL:
                    Priority_normal.setChecked(true);
                    break;
                case TodoContract.TodoEntry.PRIORITY_MEDIUM:
                    Priority_medium.setChecked(true);
                    break;
                case TodoContract.TodoEntry.PRIORITY_HIGH:
                    Priority_high.setChecked(true);
                    break;
            }

            switch (reminder){
                case TodoContract.TodoEntry.REMINDER_ON:
                    Todo_reminder.setChecked(true);
                    break;
                case TodoContract.TodoEntry.REMINDER_OFF:
                    Todo_reminder.setChecked(true);
                    break;
            }
         }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
            Todo_title.setText("");
            Todo_description.setText("");
            Todo_reminder.setChecked(false);
            Priority_normal.setChecked(true);
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void delete(){
        if(mCurrentTodoUri != null){
            int rowsDeleted = getContentResolver().delete(mCurrentTodoUri,null,null);

            if(rowsDeleted==0){
                Toasty.custom(TodoNewActivity.this, "Deletion Error", R.drawable.t_error, getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT, true, true).show();
            }else {
                Toasty.custom(TodoNewActivity.this, "Deleted", R.drawable.t_success, getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT, true, true).show();
            }
        }
    }

}

