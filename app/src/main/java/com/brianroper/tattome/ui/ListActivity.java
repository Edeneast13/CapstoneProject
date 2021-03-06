package com.brianroper.tattome.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.brianroper.tattome.R;
import com.brianroper.tattome.database.DbHandler;
import com.brianroper.tattome.util.NetworkTest;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView)findViewById(R.id.nav_view);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            mBundle = ActivityOptions.makeSceneTransitionAnimation(this)
                    .toBundle();
        }

        setUpDrawerContent(mNavigationView);

        mActionBarDrawerToggle= setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getApplicationContext()
                .getResources()
                .getString(R.string.app_title));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                //mDrawerLayout.isDrawerOpen(GravityCompat.START);
                return true;

        }

        if(mActionBarDrawerToggle.onOptionsItemSelected(item)){

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpDrawerContent(NavigationView navigationView){

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem){

        switch(menuItem.getItemId()){

            case R.id.nav_first_item:

                Intent intent = new Intent(getApplicationContext(), ListActivity.class);

                startActivity(intent);

                break;

            case R.id.nav_second_item:

                if(NetworkTest.activeNetworkCheck(getApplicationContext()) == true){

                    new MaterialDialog.Builder(this)
                        .title(R.string.categories_title)
                        .items(R.array.category_items)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {


                                return true;
                            }
                        })
                        .positiveText(R.string.box_choice)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {

                                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                                String category = "category";
                                String[] array = new String[]{
                                        "animal-tattoos",
                                        "belly-tattoos",
                                        "black-ink",
                                        "bug-tattoos",
                                        "cartoon-tattoos",
                                        "cat-tattoos",
                                        "celebrities-tattoos",
                                        "collarbone-tattoos",
                                        "cute-tattoos-2",
                                        "floral-tattoos",
                                        "geometric-tattoos-2",
                                        "henna",
                                        "historical-figures",
                                        "knee-tattoos",
                                        "leg-tattoos",
                                        "lip-tattoos",
                                        "mandala-tattoos",
                                        "minimalistic",
                                        "movie-tattoos",
                                        "music-tattoos",
                                        "neck-tattoos",
                                        "plant-tattoos",
                                        "religious-tattoos",
                                        "vintage-tattoos",
                                        "watercolor-tattoos",
                                        "words-tattoos",
                                        "wrist-tattoos"
                                };

                                switch (dialog.getSelectedIndex()){

                                    case 0:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 1:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 2:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 3:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 4:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 5:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 6:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 7:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 8:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 9:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 10:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 11:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 12:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 13:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 14:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 15:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 16:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 17:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 18:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 19:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 20:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 21:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 22:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 23:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 24:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 25:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 26:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 27:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;

                                    case 28:
                                        i.putExtra(category, array[dialog.getSelectedIndex()]);
                                        break;
                                }
                                startActivity(i, mBundle);
                            }
                        })
                        .show();

                break;

                }else{

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network),
                            Toast.LENGTH_LONG).show();
                }

            case R.id.nav_third_item:

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());

                if(activeDb() == true){

                    Intent favoritesIntent = new Intent(getApplicationContext(),FavoritesActivity.class);
                    startActivity(favoritesIntent, mBundle);
                }
                else if(sharedPreferences.getBoolean("firebaseCheckbox", false) == true){

                    try{

                        Intent favoritesIntent = new Intent(getApplicationContext(),FavoritesActivity.class);
                        startActivity(favoritesIntent, mBundle);
                    }
                    catch (Exception e){

                        Toast.makeText(getApplicationContext(), R.string.favorites_null_with_network,
                                Toast.LENGTH_LONG).show();
                    }
                 }
                else{

                    Toast.makeText(getApplicationContext(), R.string.favorites_null,
                            Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.nav_fourth_item:

                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent, mBundle);

                break;

            case R.id.nav_fifth_item:

                FirebaseAuth.getInstance().signOut();

                Intent logoutIntent = new Intent(getApplicationContext(), GetStartedActivity.class);
                startActivity(logoutIntent);

                break;
        }
    }

    public ActionBarDrawerToggle setupDrawerToggle(){
            return new ActionBarDrawerToggle(this,
                    mDrawerLayout,
                    mToolbar,
                    R.string.drawer_open,
                    R.string.drawer_closed);
    }

    public boolean activeDb(){

        boolean doesExists;

        File database = getApplicationContext().getDatabasePath("favorites.db");

        if(!database.exists()){

            doesExists = false;
        }
        else{

            doesExists = true;
        }

        return doesExists;
    }
}
