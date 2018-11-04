package org.ultradevs.mytodolist.framgments;


import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ultradevs.mytodolist.R;
import org.ultradevs.mytodolist.activities.MainActivity;
import org.ultradevs.mytodolist.helpers.db_helper;
import org.ultradevs.mytodolist.utils.UserContract;

import static org.ultradevs.mytodolist.activities.MainActivity.LOG_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment
        implements View.OnClickListener {

    private EditText mUserName;
    private EditText mUserPass;
    private Button mLoginBtn;

    private String name;
    private String pass;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mUserName = (EditText) view.findViewById(R.id.log_u_name);
        mUserPass = (EditText) view.findViewById(R.id.log_u_pass);
        mLoginBtn = (Button) view.findViewById(R.id.LogBtn);

        mLoginBtn.setOnClickListener(this);

        return  view;
    }

    private db_helper mUsersDbHelper;

    @Override
    public void onClick(View v) {

        name = mUserName.getText().toString();
        pass = mUserPass.getText().toString();

        mUsersDbHelper = new db_helper(getContext());

        String sql = String.format("UPDATE " + UserContract.USERS_ENTERY.TABLE_NAME + " SET " +
                UserContract.USERS_ENTERY.COLUMN_STATUS + "=1 WHERE " + UserContract.USERS_ENTERY.COLUMN_NAME + "='" + name +
                "' AND " + UserContract.USERS_ENTERY.COLUMN_PASSWORD + "='" + pass + "'");

        SQLiteDatabase sqlDB = mUsersDbHelper.getWritableDatabase();
        try {
            sqlDB.execSQL(sql);
            if(((MainActivity)getActivity()).CheckLogin() == true) {
                ((MainActivity) getActivity()).SetDrawers();
                ((MainActivity) getActivity()).updateFragment(((MainActivity) getActivity()).mMain);
            } else {
                Toast.makeText(getContext(), "Login Failed ! .. Please try again", Toast.LENGTH_LONG).show();
            }
        }catch (SQLException e){
            Log.d(LOG_TAG, e.getMessage());
        }
    }
}
