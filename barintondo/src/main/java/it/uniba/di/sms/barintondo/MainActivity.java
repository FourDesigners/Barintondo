package it.uniba.di.sms.barintondo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.Group;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;

public class MainActivity extends AppCompatActivity implements Constants {


    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //popolamento db
        /*ProfileOpenHelper dbHelper = new ProfileOpenHelper(this, MyProfileActivity.DB_NAME, null, 1);
        SQLiteDatabase myDB = dbHelper.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put(MyProfileActivity.COLUMN_USERNAME, "prova1");
        user.put(MyProfileActivity.COLUMN_EMAIL, "pinco@prova.it");
        user.put(MyProfileActivity.COLUMN_PASSWORD, "prova1");
        long newID = myDB.insert(MyProfileActivity.TABLE_UTENTE, null, user);*/

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onOptionsItemSelected()");
        Boolean open = myNavigationDrawer.openMenu( item );
        if(open) return open;
        else return super.onOptionsItemSelected(item);
    }
}
