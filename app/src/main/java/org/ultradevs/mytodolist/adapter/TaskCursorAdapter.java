/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.ultradevs.mytodolist.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.ultradevs.mytodolist.R;
import org.ultradevs.mytodolist.helpers.db_helper;
import org.ultradevs.mytodolist.utils.TaskContract;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This CustomCursorAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class TaskCursorAdapter extends RecyclerView.Adapter<TaskCursorAdapter.TaskViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;


    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public TaskCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout, parent, false);

        return new TaskViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

        db_helper UDB = new db_helper(mContext);

        int LogedInUID = UDB.getUID();

        // Indices for the _id, description, and priority columns
        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        int priorityIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY);
        int timeIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TIME);
        int dateIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DATE);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        int uid = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_USER_ID);

        if(mCursor.getInt(uid) == LogedInUID) {
            // Determine the values of the wanted data
            final int id = mCursor.getInt(idIndex);
            String description = mCursor.getString(descriptionIndex);
            String date = mCursor.getString(dateIndex);
            String time = mCursor.getString(timeIndex);
            int priority = mCursor.getInt(priorityIndex);

            String prio_str = null;
            switch (priority) {
                case 0:
                    prio_str = "H";
                    break;
                case 1:
                    prio_str = "M";
                    break;
                case 2:
                    prio_str = "L";
                    break;
            }

            //Set values
            holder.itemView.setTag(id);
            holder.taskDescriptionView.setText(description);
            holder.DateView.setText(date);
            holder.TimeView.setText(time);

            // Programmatically set the text and color for the priority TextView
            String priorityString = prio_str;
            holder.priorityView.setText(priorityString);

            // Get the appropriate background color based on the priority
            int priorityColor = getPriorityColor(priority);
            holder.taskDescriptionView.setTextColor(priorityColor);

            String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            if (date.equals(today)) {
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.taskDescriptionView.setTextAppearance(R.style.AppTheme_oldTasks);
                    holder.DateView.setTextAppearance(R.style.AppTheme_oldTasks);
                    holder.TimeView.setTextAppearance(R.style.AppTheme_oldTasks);
                }
            }
        }

    }


    /*
    Helper method for selecting the correct priority circle color.
    P1 = red, P2 = orange, P3 = yellow
    */
    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch(priority) {
            case 0: priorityColor = ContextCompat.getColor(mContext, R.color.materialHigh);
                break;
            case 1: priorityColor = ContextCompat.getColor(mContext, R.color.materialMid);
                break;
            case 2: priorityColor = ContextCompat.getColor(mContext, R.color.materialLow);
                break;
            default: break;
        }
        return priorityColor;
    }


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    // Inner class for creating ViewHolders
    class TaskViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        TextView taskDescriptionView;
        TextView priorityView;
        TextView DateView;
        TextView TimeView;
        LinearLayout TaskBox;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TaskViewHolder(View itemView) {
            super(itemView);

            taskDescriptionView = (TextView) itemView.findViewById(R.id.taskDescription);
            priorityView = (TextView) itemView.findViewById(R.id.priorityTextView);
            DateView = (TextView) itemView.findViewById(R.id.date_txt);
            TimeView = (TextView) itemView.findViewById(R.id.time_txt);
            TaskBox = (LinearLayout) itemView.findViewById(R.id.task_box);
        }
    }
}