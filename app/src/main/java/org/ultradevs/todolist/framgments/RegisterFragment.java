package org.ultradevs.todolist.framgments;


import android.app.Application;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.ultradevs.todolist.R;
import org.ultradevs.todolist.activities.MainActivity;
import org.ultradevs.todolist.helpers.db_helper;
import org.ultradevs.todolist.utils.UserContract;

import static org.ultradevs.todolist.activities.MainActivity.LOG_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText mUserName;
    private EditText mUserPass;
    private EditText mEmail;
    private Spinner mGender;
    private Button mReg;

    private String name;
    private String pass;
    private String email;
    private int gender;
    private int status;

    private db_helper mUsersDbHelper;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mUserName = (EditText) view.findViewById(R.id.t_user_name);
        mUserPass = (EditText) view.findViewById(R.id.t_user_pass);
        mEmail = (EditText) view.findViewById(R.id.t_user_email);
        mGender = (Spinner) view.findViewById(R.id.t_user_gender);
        mReg = (Button) view.findViewById(R.id.regBtn);

        mReg.setOnClickListener(this);


        return view ;
    }

    @Override
    public void onClick(View v) {

        name = mUserName.getText().toString();
        pass = mUserPass.getText().toString();
        email = mEmail.getText().toString();
        gender = (int) mGender.getSelectedItemId();
        status = 0;

        if(name.length() > 0 && pass.length() > 0 && email.length() > 0) {

            if(pass.length() > 5) {

                mUsersDbHelper = new db_helper(getContext());

                String sql = String.format("INSERT INTO " + UserContract.USERS_ENTERY.TABLE_NAME +
                "(" + UserContract.USERS_ENTERY.COLUMN_NAME + ", " + UserContract.USERS_ENTERY.COLUMN_PASSWORD + ", "
                 + UserContract.USERS_ENTERY.COLUMN_EMAIL + ", " + UserContract.USERS_ENTERY.COLUMN_GENDER + ", "
                 + UserContract.USERS_ENTERY.COLUMN_STATUS + ") VALUES " +
                        "('" + name + "', '" + pass + "', '" + email + "', '" + String.valueOf(gender) + "', '" + status + "')");

                SQLiteDatabase sqlDB = mUsersDbHelper.getWritableDatabase();
                try {
                    sqlDB.execSQL(sql);
                    if(((MainActivity)getActivity()).CheckLogin() == false){
                        ((MainActivity)getActivity()).updateFragment(((MainActivity)getActivity()).mLogin);
                    }
                }catch (SQLException e){
                    Log.d(LOG_TAG, e.getMessage());
                }


            } else {
                Toast.makeText(getContext(), getString(R.string.pass_error_msg), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.comp_f_error_msg), Toast.LENGTH_LONG).show();
        }


    }
}
