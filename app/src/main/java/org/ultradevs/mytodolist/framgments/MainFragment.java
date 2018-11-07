package org.ultradevs.mytodolist.framgments;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ultradevs.mytodolist.R;
import org.ultradevs.mytodolist.adapter.TaskCursorAdapter;
import org.ultradevs.mytodolist.helpers.db_helper;
import org.ultradevs.mytodolist.utils.TaskContract;
import org.ultradevs.mytodolist.utils.UserContract;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.ultradevs.mytodolist.activities.MainActivity.LOG_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener,
        View.OnTouchListener {

    private android.support.design.widget.TextInputEditText txtDesc;
    private Spinner sPrio;

    private String input;
    private int mPriority;
    private ImageButton addBtn;
    private LinearLayout add_box;

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

        txtDesc = (android.support.design.widget.TextInputEditText) view.findViewById(R.id.editTextTaskDescription);
        sPrio = (Spinner) view.findViewById(R.id.prio);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.task_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskCursorAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        add_box = (LinearLayout) view.findViewById(R.id.add_box);
        add_box.setOnTouchListener(this);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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
                            TaskContract.TaskEntry._ID);

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

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);
        contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority);
        contentValues.put(TaskContract.TaskEntry.COLUMN_DATE, date);
        contentValues.put(TaskContract.TaskEntry.COLUMN_TIME, mdformat.format(calendar.getTime()));
        db_helper USER_DB_HELPER = new db_helper(getContext());
        contentValues.put(TaskContract.TaskEntry.COLUMN_USER_ID, USER_DB_HELPER.getUID());

        Uri uri = getContext().getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

        if(uri != null) {
            Toast.makeText(getContext(), getContext().getString(R.string.task_added), Toast.LENGTH_LONG).show();
            txtDesc.setText("");
            getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        }
    }

    static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;

    public void onRightToLeftSwipe() {
    }
    public void onLeftToRightSwipe() {
    }
    public void onTopToBottomSwipe() {
        int new_height = add_box.getHeight() * 2;
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(add_box.getWidth(),new_height);
        //add_box.setLayoutParams(params);
        //add_box.requestLayout();
        //Toast.makeText(getContext(), "onTopToBottomSwipe", Toast.LENGTH_SHORT).show();

        boolean expanded = true;
        TransitionManager.beginDelayedTransition(add_box, new TransitionSet()
                .addTransition(new ChangeBounds()));

        ViewGroup.LayoutParams oldparams = add_box.getLayoutParams();
        expanded = !expanded;
        oldparams.height = expanded ? oldparams.height :
                new_height;
        add_box.setLayoutParams(oldparams);
    }
    public void onBottomToTopSwipe() {
        int new_height = add_box.getHeight() / 2;
        boolean unexpanded = true;
        TransitionManager.beginDelayedTransition(add_box, new TransitionSet()
                .addTransition(new ChangeBounds()));

        ViewGroup.LayoutParams oldparams = add_box.getLayoutParams();
        unexpanded = !unexpanded;
        oldparams.height = unexpanded ? oldparams.height :
                new_height;
        add_box.setLayoutParams(oldparams);
      //  Toast.makeText(getContext(), "onBottomToTopSwipe", Toast.LENGTH_SHORT).show();
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true;
                    }
                } else {
                    Log.i(LOG_TAG, "Swipe was only " + Math.abs(deltaX) + " long horizontally, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                // swipe vertical?
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe();
                        return true;
                    }
                } else {
                    Log.i(LOG_TAG, "Swipe was only " + Math.abs(deltaX) + " long vertically, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                return false; // no swipe horizontally and no swipe vertically
            }// case MotionEvent.ACTION_UP:
        }
        return false;
    }
}
