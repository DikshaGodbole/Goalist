package com.example.android.goalist;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Pranav on 14-Oct-17.
 */

public class TodoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private FloatingActionButton floatingActionButton;
    private TextView titleView;
    private TextView descriptionView;
    private TextView dateView;
    private TextView timeView;
    private LinearLayout priorityLayout;
    private ListView listView;
    private RecyclerView recyclerView;
    private View emptyView;
    private View todoView;

    private TodoDbHelper mDbHelper;

    public static final int TODO_LOADER = 0;

    TodoCursorAdapter todoCursorAdapter;

    TodoNewActivity todoNewActivity = new TodoNewActivity();

    public static TodoFragment newInstance(){
        TodoFragment todoFragment = new TodoFragment();
        return todoFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        todoView = inflater.inflate(R.layout.fragment_todo,container,false);
        listView = (ListView) todoView.findViewById(R.id.todo_list);
        emptyView = todoView.findViewById(R.id.empty_view);
        //recyclerView = (RecyclerView) todoView.findViewById(R.id.activity_recycle_todo);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));

        floatingActionButton = (FloatingActionButton) todoView.findViewById(R.id.fab_todo);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),TodoNewActivity.class);
                startActivity(intent);
            }
        });

        titleView = (TextView) todoView.findViewById(R.id.title);
        descriptionView = (TextView) todoView.findViewById(R.id.description);
        dateView = (TextView) todoView.findViewById(R.id.date);
        timeView = (TextView) todoView.findViewById(R.id.time);
        priorityLayout = (LinearLayout) todoView.findViewById(R.id.priority_layout);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),TodoNewActivity.class);
                Uri currentTodoUri = ContentUris.withAppendedId(TodoContract.TodoEntry.CONTENT_URI,id);
                intent.setData(currentTodoUri);
                startActivity(intent);
            }
        });

        listView.setEmptyView(emptyView);

        todoCursorAdapter = new TodoCursorAdapter(getActivity(),null);
        listView.setAdapter(todoCursorAdapter);

       /* ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                todoNewActivity.delete();
            }

        });*/

        getLoaderManager().initLoader(TODO_LOADER,null,this);
        //displayDatabase();
        return todoView;
    }

    /*private void displayDatabase(){

        mDbHelper = new TodoDbHelper(getActivity());

        String[] projection = {
                TodoContract.TodoEntry._ID,
                TodoContract.TodoEntry.COLUMN_TITLE,
                TodoContract.TodoEntry.COLUMN_DESCRIPTION,
                TodoContract.TodoEntry.COLUMN_DUEDATE,
                TodoContract.TodoEntry.COLUMN_TIME,
                TodoContract.TodoEntry.COLUMN_PRIORITY,
                TodoContract.TodoEntry.COLUMN_REMINDER
        };

        Cursor cursor = getActivity().getContentResolver().query(TodoContract.TodoEntry.CONTENT_URI,projection,null,null,null);

        listView.setEmptyView(emptyView);

        TodoCursorAdapter adapter = new TodoCursorAdapter(getActivity(),cursor);

        listView.setAdapter(adapter);
    }*/


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TodoContract.TodoEntry._ID,
                TodoContract.TodoEntry.COLUMN_TITLE,
                TodoContract.TodoEntry.COLUMN_DESCRIPTION,
                TodoContract.TodoEntry.COLUMN_DUEDATE,
                TodoContract.TodoEntry.COLUMN_TIME,
                TodoContract.TodoEntry.COLUMN_PRIORITY
        };

        return  new CursorLoader(getActivity(), TodoContract.TodoEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        todoCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        todoCursorAdapter.swapCursor(null);
    }
}
