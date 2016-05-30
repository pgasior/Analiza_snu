package pl.gasior.analizasnu.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import pl.gasior.analizasnu.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListenRecordingFragment.OnFragmentInteractionListener, RecordFragmentAlt.OnFragmentInteractionListener {

    private Toolbar toolbar;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState==null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = RecordFragmentAlt.newInstance();
            fm.beginTransaction().replace(R.id.flContent,fragment).commit();
        }

        //czytane preferencji
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("config calibrationLevel", String.valueOf(sharedPref.getInt("calibrationLevel",0)));
    }

    public void disableDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    public void enableDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Toast.makeText(this, "ADD!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, MyPreferencesActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch(id) {
            case R.id.nav_record:
                //fragmentClass = RecordFragment.class;
                fragment = RecordFragmentAlt.newInstance();
                break;
            case R.id.nav_listen_recording:
                //fragmentClass = ListenRecordingFragment.class;
                fragment = ListenRecordingFragment.newInstance();
                break;
            case R.id.nav_calendar_view:
                //fragmentClass = DreamCalendarFragment.class;
                fragment = DreamCalendarFragment.newInstance();
                break;
            case R.id.nav_manage:
                fragment = ConfigFragment.newInstance();
                break;
            default:
                //fragmentClass = RecordFragment.class;
                fragment = DreamCalendarFragment.newInstance();
        }

//        try {
//            fragment = (Fragment)fragmentClass.newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        ft.replace(R.id.flContent,fragment).commit();
        Log.i("Main","Ustawilem fragment");
        item.setChecked(true);
        setTitle(item.getTitle());

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showDreamsForDateRange(String dateStart, String dateEnd) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = ListenRecordingFragment.newInstance(dateStart,dateEnd);
        ft.replace(R.id.flContent,fragment).addToBackStack(null).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
