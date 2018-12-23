package it.uniba.di.sms.barintondo.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;
import it.uniba.di.sms.barintondo.R;

public class MyNavigationDrawer {
    private final Activity activity;
    private final NavigationView navigationView;
    private final DrawerLayout mDrawerLayout;

    /**
     * @param activity
     * @param navigationView
     * @param mDrawerLayout
     */
    public MyNavigationDrawer(Activity activity , NavigationView navigationView , DrawerLayout mDrawerLayout) {
        this.activity = activity;
        this.navigationView = navigationView;
        this.mDrawerLayout = mDrawerLayout;
    }

    public MyNavigationDrawer build(){

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        switch(menuItem.getItemId()) {
                            case R.id.profile:
                                Toast.makeText(activity, "Profilo", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.interests:
                                Toast.makeText(activity, "Interessi", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.coupon:
                                Toast.makeText(activity, "Coupon", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.settings:
                                Toast.makeText(activity, "Impostazioni", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.logout:
                                Toast.makeText(activity, "Logout", Toast.LENGTH_SHORT).show();
                                break;
                        }

                        return true;
                    }
                });
        return this;
    }

    public Activity getActivity() {
        return activity;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    public DrawerLayout getmDrawerLayout() {
        return mDrawerLayout;
    }
}
