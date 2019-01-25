package it.uniba.di.sms.barintondo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import it.uniba.di.sms.barintondo.EventsActivity;
import it.uniba.di.sms.barintondo.CouponLuogoListActivity;
import it.uniba.di.sms.barintondo.HomeActivity;
import it.uniba.di.sms.barintondo.InterestsListActivity;
import it.uniba.di.sms.barintondo.LuogoListActivity;
import it.uniba.di.sms.barintondo.LoginActivity;
import it.uniba.di.sms.barintondo.MyProfileActivity;
import it.uniba.di.sms.barintondo.R;
import it.uniba.di.sms.barintondo.SettingsActivity;

public class MyNavigationDrawer implements Constants {

    private final Activity activity;
    private final NavigationView navigationView;
    private final DrawerLayout mDrawerLayout;

    private TextView nickname;

    /**
     * @param activity:       activity parent in cui apparirà il drawer
     * @param navigationView: riferimento all'elemento con id R.id.nav_view
     * @param mDrawerLayout:  riferimento al layout con id R.id.drawer_layout
     */
    public MyNavigationDrawer(Activity activity , NavigationView navigationView ,
                              DrawerLayout mDrawerLayout) {
        this.activity = activity;
        this.navigationView = navigationView;
        this.mDrawerLayout = mDrawerLayout;
    }

    public MyNavigationDrawer build() {
        Log.i( TAG , getClass().getSimpleName() + ":entered build()" );

        //imposto il nome dell'utente
        /*con support library >= 23.1.1, bisogna fare riferimento all'header navigation view e, da questo, ottenere il riferimento
        alla textView che mostra il nickname dell'utente attuale*/
        View header = navigationView.getHeaderView( 0 );
        nickname = header.findViewById( R.id.header_nickname );
        Log.i( TAG , getClass().getSimpleName() + ": nickname = " + nickname );
        //prelevo dati dal db
        ProfileOpenHelper dbHelper = new ProfileOpenHelper( activity , DB_NAME , null , 1 );
        SQLiteDatabase myDB = dbHelper.getReadableDatabase();
        //definisco la query
        String[] columns = {COLUMN_NICKNAME};
        Cursor myCursor;
        //ottengo il cursore
        myCursor = myDB.query( TABLE_UTENTE , columns , null , null , null , null , null , null );
        myCursor.moveToFirst();

        nickname.setText( myCursor.getString( myCursor.getColumnIndex( COLUMN_NICKNAME ) ) );

        myCursor.close();
        myDB.close();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Log.i( TAG , getClass().getSimpleName() + ":entered onNavigationItemSelected()" );
                        // set item as selected to persist highlight
                        menuItem.setChecked( true );
                        Menu m = navigationView.getMenu();

                        switch (menuItem.getItemId()) {
                            case R.id.home_drawer:
                                mDrawerLayout.closeDrawers();
                                if (activity.getClass() != HomeActivity.class ) {
                                    Intent intent = new Intent( activity , HomeActivity.class );
                                    activity.startActivity( intent );
                                }
                                //Toast.makeText( activity , "Home" , Toast.LENGTH_SHORT ).show();
                                break;
                            case R.id.dormire:
                                mDrawerLayout.closeDrawers();
                                //Toast.makeText( activity , "Dormire" , Toast.LENGTH_SHORT ).show();
                                if (activity.getClass()!=LuogoListActivity.class ||
                                        !((LuogoListActivity) activity).getItems_type().equals( Constants.INTENT_SLEEPING )) {
                                    Intent intent = new Intent(activity, LuogoListActivity.class);
                                    intent.putExtra(Constants.INTENT_ACTIVITY_ITEM_TYPE, Constants.INTENT_SLEEPING);
                                    activity.startActivity(intent);
                                    if(activity.getClass() == LuogoListActivity.class) {
                                        activity.finish();
                                    }
                                }
                                break;
                            case R.id.mangiare:
                                mDrawerLayout.closeDrawers();
                                //Toast.makeText( activity , "Mangiare" , Toast.LENGTH_SHORT ).show();
                                if (activity.getClass()!=LuogoListActivity.class ||
                                        !((LuogoListActivity) activity).getItems_type().equals( Constants.INTENT_EATING )) {
                                    Intent intent = new Intent(activity, LuogoListActivity.class);
                                    intent.putExtra(Constants.INTENT_ACTIVITY_ITEM_TYPE, Constants.INTENT_EATING);
                                    activity.startActivity(intent);
                                    if(activity.getClass() == LuogoListActivity.class) {
                                        activity.finish();
                                    }
                                }
                                break;
                            case R.id.attrazioni:
                                mDrawerLayout.closeDrawers();
                                if (activity.getClass()!=LuogoListActivity.class ||
                                        !((LuogoListActivity) activity).getItems_type().equals( Constants.INTENT_ATTRACTIONS )) {
                                    Intent intent = new Intent( activity , LuogoListActivity.class );
                                    intent.putExtra( Constants.INTENT_ACTIVITY_ITEM_TYPE , Constants.INTENT_ATTRACTIONS );
                                    activity.startActivity( intent );
                                    if(activity.getClass() == LuogoListActivity.class) {
                                        activity.finish();
                                    }
                                }
                                //Toast.makeText(activity, "Attrazioni", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.eventi:
                                mDrawerLayout.closeDrawers();
                                //Toast.makeText( activity , "Eventi" , Toast.LENGTH_SHORT ).show();
                                if (activity.getClass()!=LuogoListActivity.class ||
                                        !((LuogoListActivity) activity).getItems_type().equals( Constants.INTENT_EVENTS )) {
                                    Intent intent = new Intent(activity, LuogoListActivity.class);
                                    intent.putExtra(Constants.INTENT_ACTIVITY_ITEM_TYPE, Constants.INTENT_EVENTS);
                                    activity.startActivity(intent);
                                    if(activity.getClass() == LuogoListActivity.class) {
                                        activity.finish();
                                    }
                                }
                                break;
                            case R.id.intorno:
                                mDrawerLayout.closeDrawers();
                                //Toast.makeText( activity , "Intorno a Bari" , Toast.LENGTH_SHORT ).show();
                                if (activity.getClass()!=LuogoListActivity.class ||
                                        !((LuogoListActivity) activity).getItems_type().equals( Constants.INTENT_NEAR )) {
                                    Intent intent = new Intent(activity, LuogoListActivity.class);
                                    intent.putExtra(Constants.INTENT_ACTIVITY_ITEM_TYPE, Constants.INTENT_NEAR);
                                    activity.startActivity(intent);
                                    if(activity.getClass() == LuogoListActivity.class) {
                                        activity.finish();
                                    }
                                }
                                break;
                            case R.id.profile:
                                mDrawerLayout.closeDrawers();
                                activity.startActivity( new Intent( activity , MyProfileActivity.class ) );
                                break;
                            case R.id.interests:
                                mDrawerLayout.closeDrawers();
                                if (activity.getClass()!=InterestsListActivity.class) {
                                    Intent intent = new Intent(activity, InterestsListActivity.class);
                                    activity.startActivity(intent);
                                }
                                break;
                            case R.id.coupon:
                                mDrawerLayout.closeDrawers();
                                activity.startActivity( new Intent( activity , CouponLuogoListActivity.class ) );
                                break;
                            case R.id.contact:
                                mDrawerLayout.closeDrawers();
                                Intent intent = new Intent(activity, EventsActivity.class);
                                activity.startActivity(intent);
                                break;
                            case R.id.feedback:
                                sendFeedback( activity );
                                break;
                            case R.id.settings:
                                mDrawerLayout.closeDrawers();
                                activity.startActivity( new Intent( activity , SettingsActivity.class ) );
                                break;
                            case R.id.logout:
                                mDrawerLayout.closeDrawers();
                                printDialog();
                                break;
                        }

                        return true;
                    }
                } );
        return this;
    }

    private void printDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getResources().getString(R.string.strDisconnect))
                .setTitle(getActivity().getResources().getString(R.string.strLogout));

        AlertDialog dialog = builder.create();
        builder.setPositiveButton(getActivity().getResources().getString(R.string.strConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ProfileOpenHelper openHelper = new ProfileOpenHelper(activity, Constants.DB_NAME, null, 1);
                goLogin();
                ProfileOpenHelper.delete(openHelper);
            }
        });
        builder.setNegativeButton(getActivity().getResources().getString(R.string.strCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void goLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public boolean openMenu(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.getmDrawerLayout().openDrawer( GravityCompat.START );
                return true;
        }

        return false;
    }

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    private static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo( context.getPackageName() , 0 ).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e( TAG , "Package not found" );
        }
        Intent intent = new Intent( Intent.ACTION_SEND );
        intent.setType( "message/rfc822" );
        intent.putExtra( Intent.EXTRA_EMAIL , new String[]{"fourdesigners937@gmail.com"} );
        intent.putExtra( Intent.EXTRA_SUBJECT , "Feedback" );
        intent.putExtra( Intent.EXTRA_TEXT , body );
        context.startActivity( Intent.createChooser( intent , context.getString( R.string.choose_email_client ) ) );
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
