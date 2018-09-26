package org.ultradevs.todolist.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ultradevs.todolist.R;
import org.ultradevs.todolist.utils.UserContract;

public class users_adapter extends RecyclerView.Adapter<users_adapter.UserViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;


    /**
     * Constructor for the users_adapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public users_adapter(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new UserViewHolder that holds the view for each task
     */
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.users_list, parent, false);

        return new UserViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {

        // Indices for the _id, description, and priority columns
        int idIndex = mCursor.getColumnIndex(UserContract.USERS_ENTERY._ID);
        int nameIndex = mCursor.getColumnIndex(UserContract.USERS_ENTERY.COLUMN_NAME);
        int passIndex = mCursor.getColumnIndex(UserContract.USERS_ENTERY.COLUMN_PASSWORD);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String name = mCursor.getString(nameIndex);
        int pass = mCursor.getInt(passIndex);

        //Set values
        holder.itemView.setTag(id);
        holder.UserNameView.setText(name);

        String priorityString = "" + pass; // converts int to String
        holder.passView.setText(priorityString);

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
    class UserViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        TextView UserNameView;
        TextView passView;

        /**
         * Constructor for the UserViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public UserViewHolder(View itemView) {
            super(itemView);

            UserNameView = (TextView) itemView.findViewById(R.id.u_name);
            passView = (TextView) itemView.findViewById(R.id.u_pass);
        }
    }
}