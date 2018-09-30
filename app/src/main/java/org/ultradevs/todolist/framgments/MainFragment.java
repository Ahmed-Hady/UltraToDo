package org.ultradevs.todolist.framgments;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.ultradevs.todolist.R;
import org.ultradevs.todolist.adapter.TaskCursorAdapter;
import org.ultradevs.todolist.utils.TaskContract;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import static org.ultradevs.todolist.activities.MainActivity.LOG_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener{

    private EditText txtDesc;
    private Spinner sPrio;

    private String input;
    private int mPriority;
    private ImageButton addBtn;

    // Constants for logging and referring to a unique loader
    private static final int TASK_LOADER_ID = 0;

    // Member variables for the adapter and RecyclerView
    private TaskCursorAdapter mAdapter;
    RecyclerView mRecyclerView;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        addBtn = (ImageButton) view.findViewById(R.id.task_add_btn);
        addBtn.setOnClickListener(this);

        txtDesc = (EditText) view.findViewById(R.id.editTextTaskDescription);
        sPrio = (Spinner) view.findViewById(R.id.prio);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.task_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskCursorAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int id = (int) viewHolder.itemView.getTag();

                String stringId = Integer.toString(id);
                Uri uri = TaskContract.TaskEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContext().getContentResolver().delete(uri, null, null);

                getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainFragment.this);

            }
        }).attachToRecyclerView(mRecyclerView);

        getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // re-queries for all tasks
        getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(getContext()) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContext().getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            TaskContract.TaskEntry.COLUMN_PRIORITY);

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View v) {
        input = txtDesc.getText().toString();
        if (input.length() == 0) {
            return;
        }
        mPriority = (int) sPrio.getSelectedItemId();

        String date = new SimpleDateFormat("dd/mm/yyyy").format(new Date());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);
        contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority);
        contentValues.put(TaskContract.TaskEntry.COLUMN_DATE, date);
        contentValues.put(TaskContract.TaskEntry.COLUMN_TIME, mdformat.format(calendar.getTime()));

        Uri uri = getContext().getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

        if(uri != null) {
            Toast.makeText(getContext(), "Task Added !", Toast.LENGTH_LONG).show();
            txtDesc.setText("");
            getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        }
    }
}
