package org.ultradevs.todolist.framgments;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        addBtn = (ImageButton) view.findViewById(R.id.task_add_btn);
        addBtn.setOnClickListener(this);

        txtDesc = (EditText) view.findViewById(R.id.editTextTaskDescription);
        sPrio = (Spinner) view.findViewById(R.id.prio);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.task_list);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskCursorAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
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

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
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
                // Will implement to load data

                // COMPLETED (5) Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

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
        // Not yet implemented
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        input = txtDesc.getText().toString();
        if (input.length() == 0) {
            return;
        }
        mPriority = (int) sPrio.getSelectedItemId();

        // Insert new task data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);
        contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority);
        // Insert the content values via a ContentResolver
        Uri uri = getContext().getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

        // Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
        if(uri != null) {
            Toast.makeText(getContext(), "Task Added !", Toast.LENGTH_LONG).show();
            txtDesc.setText("");
            getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        }
    }
}
