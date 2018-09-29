package org.ultradevs.todolist.framgments;


import android.app.Application;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.ultradevs.todolist.R;
import org.ultradevs.todolist.activities.MainActivity;
import org.ultradevs.todolist.utils.UserContract;

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
    public LoginFragment mLogin;
    @Override
    public void onClick(View v) {

        name = mUserName.getText().toString();
        pass = mUserPass.getText().toString();
        email = mEmail.getText().toString();
        gender = (int) mGender.getSelectedItemId();

        if(name.length() > 0 && pass.length() > 0 && email.length() > 0) {

            if(pass.length() > 5) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(UserContract.USERS_ENTERY.COLUMN_NAME, name);
                contentValues.put(UserContract.USERS_ENTERY.COLUMN_PASSWORD, pass);
                contentValues.put(UserContract.USERS_ENTERY.COLUMN_EMAIL, email);
                contentValues.put(UserContract.USERS_ENTERY.COLUMN_GENDER, String.valueOf(gender));
                contentValues.put(UserContract.USERS_ENTERY.COLUMN_STATUS, "0");

                Uri uri = getContext().getContentResolver().insert(UserContract.USERS_ENTERY.CONTENT_URI, contentValues);

                mLogin = new LoginFragment();

                if (uri != null) {
                    if(((MainActivity)getActivity()).CheckLogin() == false){
                        ((MainActivity)getActivity()).updateFragment(mLogin);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Password shouldn't be less than 6 letters !", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Please Complete all fields !", Toast.LENGTH_LONG).show();
        }


    }
}
