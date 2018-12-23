package it.uniba.di.sms.barintondo;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;

public class MainActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        switch (item.getItemId()) {
            case android.R.id.home:
                myNavigationDrawer.getmDrawerLayout().openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
