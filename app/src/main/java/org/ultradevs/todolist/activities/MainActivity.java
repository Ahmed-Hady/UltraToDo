package org.ultradevs.todolist.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.ultradevs.todolist.R;
import org.ultradevs.todolist.framgments.LoginFragment;
import org.ultradevs.todolist.framgments.MainFragment;
import org.ultradevs.todolist.framgments.RegisterFragment;
import org.ultradevs.todolist.helpers.db_helper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static public String LOG_TAG = "UltraToDo";

    public RegisterFragment mRegister;
    public LoginFragment mLogin;
    public MainFragment mMain;

    public Toolbar toolbar;
    public DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRegister = new RegisterFragment();
        mLogin = new LoginFragment();
        mMain = new MainFragment();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(CheckDB() == false) {
            updateFragment(this.mRegister);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            if(CheckLogin() == false){
                updateFragment(mLogin);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }else{
                updateFragment(mMain);
                SetDrawers();
            }
        }
    }

    public void SetDrawers(){ ;
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    /**
     * Update Content control with new fragment
     */
    public void updateFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.content, fragment);
        ft.commit();
    }
    
}
