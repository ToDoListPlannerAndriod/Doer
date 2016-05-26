package com.alastar.todolist;

/**
 * Created by alast on 14.05.2016.
 */
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alastar.todolist.db.TaskContract;
import com.alastar.todolist.db.TaskDbHelper;

import com.alastar.todolist.dbHistory.HistoryContract;
import com.alastar.todolist.dbHistory.HistoryDbHelper;

import java.util.ArrayList;

import android.support.design.widget.FloatingActionButton;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Menu menu;
    private TaskDbHelper mHelper;
    private HistoryDbHelper mHistHelper;

    private ListView mTaskListView;
    private ListView mHistListView;
    private ArrayAdapter<String> mAdapter;
    private ArrayAdapter<String> mHistAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Tasks");
        mHistHelper = new HistoryDbHelper(this);
        mHelper = new TaskDbHelper(this);

        mTaskListView = (ListView) findViewById(R.id.list_todo);
        mHistListView = (ListView) findViewById(R.id.list_done);
        updateUI();
    }
    private void updateUI(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }
    private void updateUIHist(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHistHelper.getReadableDatabase();
        Cursor cursor = db.query(HistoryContract.HistEntry.TABLE,
                new String[]{HistoryContract.HistEntry._ID, HistoryContract.HistEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(HistoryContract.HistEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mHistAdapter == null) {
            mHistAdapter = new ArrayAdapter<>(this,
                    R.layout.item_history,
                    R.id.task_title,
                    taskList);
            mHistListView.setAdapter(mHistAdapter);
        } else {
            mHistAdapter.clear();
            mHistAdapter.addAll(taskList);
            mHistAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:

                menu.findItem(R.id.action_history).setVisible(true);
                mHistListView.setVisibility(View.GONE);
                mTaskListView.setVisibility(View.VISIBLE);
                menu.findItem(R.id.action_current).setVisible(false);

                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
            case R.id.action_history:
                setTitle("History");
                menu.findItem(R.id.action_current).setVisible(true);
                mTaskListView.setVisibility(View.GONE);
                mHistListView.setVisibility(View.VISIBLE);
                item.setVisible(false);
                updateUIHist();
                return true;
            case R.id.action_current:
                setTitle("Tasks");
                menu.findItem(R.id.action_history).setVisible(true);
                mHistListView.setVisibility(View.GONE);
                mTaskListView.setVisibility(View.VISIBLE);
                item.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void completeTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();


        SQLiteDatabase dbHist = mHistHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HistoryContract.HistEntry.COL_TASK_TITLE, task);
        dbHist.insertWithOnConflict(HistoryContract.HistEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        dbHist.close();

    }
    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHistHelper.getWritableDatabase();
        db.delete(HistoryContract.HistEntry.TABLE,
                HistoryContract.HistEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUIHist();

    }
}