package org.ultradevs.mytodolist.activities;

import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.ultradevs.mytodolist.R;
import org.ultradevs.mytodolist.adapter.DrawerAdapter;
import org.ultradevs.mytodolist.framgments.LoginFragment;
import org.ultradevs.mytodolist.framgments.MainFragment;
import org.ultradevs.mytodolist.framgments.RegisterFragment;
import org.ultradevs.mytodolist.helpers.db_helper;
import org.ultradevs.mytodolist.utils.DrawerItem;
import org.ultradevs.mytodolist.utils.SimpleItem;
import org.ultradevs.mytodolist.utils.SpaceItem;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements DrawerAdapter.OnItemSelectedListener {

    final static public String LOG_TAG = "UltraToDo";

    private SlidingRootNav slidingRootNav;

    private String[] screenTitles;
    private Drawable[] screenIcons;


    private static final int POS_TODAY_LIST = 0;
    private static final int POS_TODAY_COMPLETED = 1;
    private static final int POS_OLD_TASKS = 2;
    private static final int POS_SETTINGS = 3;
    private static final int POS_LOGOUT = 5;

    public RegisterFragment mRegister;
    public LoginFragment mLogin;
    public MainFragment mMain;

    public Toolbar toolbar;

    private TextView user_name;
    private TextView user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRegister = new RegisterFragment();
        mLogin = new LoginFragment();
        mMain = new MainFragment();

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_TODAY_LIST).setChecked(true),
                createItemFor(POS_TODAY_COMPLETED),
                createItemFor(POS_OLD_TASKS),
                createItemFor(POS_SETTINGS),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_TODAY_LIST);

        user_name = (TextView) findViewById(R.id.top_txt_uname);
        user_email = (TextView) findViewById(R.id.top_txt_uemail);

        if(CheckDB() == false) {
            updateFragment(this.mRegister);
            slidingRootNav.setMenuLocked(true);
        } else {
            if(CheckLogin() == false){
                updateFragment(mLogin);
                slidingRootNav.setMenuLocked(true);
            }else{
                updateFragment(mMain);
                SetUserInfo();
                slidingRootNav.setMenuLocked(false);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void SetDrawer(boolean x){
        if(x == true){
            slidingRootNav.setMenuLocked(false);
        } else if(x == false){
            slidingRootNav.setMenuLocked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check DB
     */
    private db_helper mUsersDbHelper;
    public boolean CheckDB(){
            mUsersDbHelper = new db_helper(getBaseContext());
            SQLiteDatabase db = mUsersDbHelper.getReadableDatabase();
            if (db.isOpen()) {
                Log.d(LOG_TAG, "DB Works Well !");
                if(mUsersDbHelper.getUsersCount() > 0) {
                    return true;
                } else{
                    return false;
                }
            } else {
                Log.d(LOG_TAG, "Error .. DB Not Found !");
                return false;
            }
    }

    public boolean CheckLogin(){
        mUsersDbHelper = new db_helper(getBaseContext());
        if(mUsersDbHelper.getNumOfLog() == 1) {
            return true;
        } else{
            return false;
        }
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    /**
     * Update Content control with new fragment
     */
    public void updateFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.container, fragment);
        ft.commit();
    }
    @Override
    public void onItemSelected(int position) {
        switch(position){
            case 0:
                updateFragment(mMain);
                break;
            case 1:
                //updateFragment();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                finish();
                break;
        }
        slidingRootNav.closeMenu();
    }

    public void SetUserInfo(){
        mUsersDbHelper = new db_helper(getBaseContext());
        user_name.setText(mUsersDbHelper.getUserName());
        user_email.setText(mUsersDbHelper.getUserEmail());
    }
}
