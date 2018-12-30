package it.uniba.di.sms.barintondo;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;
import it.uniba.di.sms.barintondo.utils.ProfileOpenHelper;

public class HomeActivity extends AppCompatActivity implements Constants {

    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home );


        fillChips();
        //popolamento db
        ProfileOpenHelper dbHelper = new ProfileOpenHelper(this, Constants.DB_NAME, null, 1);
        SQLiteDatabase myDB = dbHelper.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put(Constants.COLUMN_USERNAME, "prova1");
        user.put(Constants.COLUMN_EMAIL, "pinco@prova.it");
        user.put(Constants.COLUMN_PASSWORD, "prova1");
        long newID = myDB.insert(Constants.TABLE_UTENTE, null, user);


        myNavigationDrawer =new MyNavigationDrawer(this,
                (NavigationView) findViewById(R.id.nav_view),
                (DrawerLayout )findViewById(R.id.drawer_layout));
        myNavigationDrawer.build();




        myToolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_hamburger);


    }

    private void fillChips() {
        SharedPreferences categories = getSharedPreferences(PREFS_NAME, 0);
        if(!categories.contains(INSERTED)) {
            SharedPreferences.Editor edit = categories.edit();
            edit.putBoolean(INSERTED, true);
            edit.putString(INTENT_ATTRACTIONS, CHIPS_ATTRACTIONS);
            edit.putString(INTENT_EATING, CHIPS_EATING);
            edit.putString(INTENT_SLEEPING, CHIPS_SLEEPING);
            edit.putString(INTENT_EVENTS, CHIPS_EVENTS);
            edit.putString(INTENT_NEAR, CHIPS_ATTRACTIONS);
            edit.apply();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onOptionsItemSelected()");
        Boolean open = myNavigationDrawer.openMenu( item );
        if(open) return open;
        else return super.onOptionsItemSelected(item);
    }
}
