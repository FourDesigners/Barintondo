package it.uniba.di.sms.barintondo.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import it.uniba.di.sms.barintondo.R;
import it.uniba.di.sms.barintondo.SettingsActivity;

public class MyNavigationDrawer implements Constants{
    private final Activity activity;
    private final NavigationView navigationView;
    private final DrawerLayout mDrawerLayout;

    /**
     * @param activity: activity parent in cui apparirà il drawer
     * @param navigationView: riferimento all'elemento con id R.id.nav_view
     * @param mDrawerLayout: riferimento al layout con id R.id.drawer_layout
     */
    public MyNavigationDrawer(Activity activity , NavigationView navigationView ,
                              DrawerLayout mDrawerLayout) {
        this.activity = activity;
        this.navigationView = navigationView;
        this.mDrawerLayout = mDrawerLayout;
    }

    public MyNavigationDrawer build(){
        Log.i(TAG, getClass().getSimpleName() + ":entered build()");

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Log.i(TAG, getClass().getSimpleName() + ":entered onNavigationItemSelected()");
                        // set item as selected to persist highlight
                        //menuItem.setChecked(true);
                        Menu m =navigationView.getMenu();

                        switch(menuItem.getItemId()) {
                            case R.id.home_drawer:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Home", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.categorie:
                                boolean b=!m.findItem(R.id.dormire).isVisible();
                                m.findItem(R.id.dormire).setVisible( b );
                                m.findItem(R.id.mangiare).setVisible( b );
                                m.findItem(R.id.attrazioni).setVisible( b );
                                m.findItem(R.id.eventi).setVisible( b );
                                m.findItem(R.id.intorno).setVisible( b );
                                break;
                            case R.id.dormire:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Dormire", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.mangiare:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Mangiare", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.attrazioni:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Attrazioni", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.eventi:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Eventi", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.intorno:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Intorno a Bari", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.profile:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Profilo", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.interests:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Interessi", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.coupon:
                                Toast.makeText(activity, "Coupon", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.settings:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Impostazioni", Toast.LENGTH_SHORT).show();
                                activity.startActivity(new Intent(activity, SettingsActivity.class));
                                break;
                            case R.id.contact:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Contatti", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.logout:
                                mDrawerLayout.closeDrawers();
                                Toast.makeText(activity, "Logout", Toast.LENGTH_SHORT).show();
                                break;
                        }

                        return true;
                    }
                });
        return this;
    }

    public boolean openMenu(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.getmDrawerLayout().openDrawer(GravityCompat.START);
                return true;
        }

        return false;
    }

    public Activity getActivity() {
        return activity;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    private DrawerLayout getmDrawerLayout() {
        return mDrawerLayout;
    }
}
