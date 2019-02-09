package it.uniba.di.sms.barintondo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;


import it.uniba.di.sms.barintondo.utils.MyListeners;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.LuogoAdapter;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;
import it.uniba.di.sms.barintondo.utils.MyScrollListener;
import it.uniba.di.sms.barintondo.utils.ToolbarSwitchCategories;
import it.uniba.di.sms.barintondo.utils.UserLocation;
import it.uniba.di.sms.barintondo.utils.UserUtils;

public class LuogoListActivity extends AppCompatActivity implements Constants, MyListeners.ItemsAdapterListener {

    private String TAG_CLASS = getClass().getSimpleName();
    private Toolbar myToolbar;
    private MyNavigationDrawer myNavigationDrawer;
    private static String items_type;
    private RecyclerView recyclerView;
    private List<Luogo> luogoList;
    private LuogoAdapter mAdapter;
    private SearchView searchView;
    private ToolbarSwitchCategories mySwitchCategory;
    String[] arrayRes = null;
    String[] arrayTags = null;
    String[] order = null;
    MyListeners.LuoghiList myDBListner;
    private boolean arrow = false;
    private String requestCat;
    private Toolbar switchCategories;
    private UserLocation myUserLocation;
    MyListeners.UserLocationCallback mLocationListner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i( TAG , TAG_CLASS + ":entered onCreate()" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_luogo_list );

        //first time intent reading
        items_type = getIntent().getStringExtra( Constants.INTENT_ACTIVITY_ITEM_TYPE );

        //toolbar setup
        myToolbar = findViewById( R.id.main_activity_toolbar );

        //setup toolbar per cambiare categoria
        mySwitchCategory = new ToolbarSwitchCategories( this , items_type );

        Resources res = getResources();
        //imposta gli array per settare i chips e la variabile che identiica la categoria selezionata
        if (items_type.equals( Constants.INTENT_ATTRACTIONS )) {
            requestCat = REQUEST_GET_ATTRACTIONS;
            arrayRes = res.getStringArray( R.array.attractions );
            arrayTags = res.getStringArray( R.array.attractionsTags );
            myToolbar.setTitle( R.string.attractionsToolbarTitle );
        } else if (items_type.equals( Constants.INTENT_EATING )) {
            requestCat = REQUEST_GET_EAT;
            arrayRes = res.getStringArray( R.array.eating );
            arrayTags = res.getStringArray( R.array.eatingTags );
            myToolbar.setTitle( R.string.eating_option );
        } else if (items_type.equals( Constants.INTENT_SLEEPING )) {
            requestCat = REQUEST_GET_SLEEP;
            arrayRes = res.getStringArray( R.array.sleeping );
            arrayTags = res.getStringArray( R.array.sleepingTags );
            myToolbar.setTitle( R.string.sleeping_option );
        } else if (items_type.equals( Constants.INTENT_EVENTS )) {
            requestCat = REQUEST_GET_EVENTS;
            arrayRes = res.getStringArray( R.array.events );
            arrayTags = res.getStringArray( R.array.eventsTags );
            myToolbar.setTitle( R.string.events_option );
        } else if (items_type.equals( Constants.INTENT_NEAR )) {
            requestCat = REQUEST_GET_NEAR_BARI;
            arrayRes = res.getStringArray( R.array.near );
            arrayTags = res.getStringArray( R.array.nearTags );
            myToolbar.setTitle( R.string.near_option );
        }

        setSupportActionBar( myToolbar );
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled( true );
        actionbar.setHomeAsUpIndicator( R.drawable.ic_hamburger );

        //nav drawer setup
        myNavigationDrawer = new MyNavigationDrawer( this ,
                (NavigationView) findViewById( R.id.nav_view ) ,
                (DrawerLayout) findViewById( R.id.drawer_layout ) );
        myNavigationDrawer.build();


        //first time chip group setup
        ChipGroup chipGroup = findViewById( R.id.chipGroup );

        //first time chips creation
        if (items_type.equals( Constants.INTENT_ATTRACTIONS ) || items_type.equals( Constants.INTENT_NEAR )) {
            changeChipsOrder( arrayRes , arrayTags , null );
        } else {
            int id = 0;
            for (String s : arrayRes) {
                final Chip newChip = new Chip( this );
                newChip.setId( id );
                assert arrayTags != null;
                newChip.setTag( arrayTags[id++] );
                newChip.setChipText( s );
                newChip.setClickable( true );
                newChip.setCheckable( true );
                newChip.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String query;
                        if (newChip.isChecked())
                            query = newChip.getTag().toString();
                        else query = ""; //stringa vuota così da mostrare tutti i luoghi
                        Log.i( TAG , "Query=" + query );
                        mAdapter.getFilter().filter( query );
                        //setCounter(arrayRes, arrayTags, newChip.getTag().toString());
                    }
                } );
                chipGroup.addView( newChip );
            }
        }

        //list and adapter setup
        luogoList = new ArrayList<>();
        mAdapter = new LuogoAdapter( this , luogoList , this );

        //recyclerView setup
        recyclerView = findViewById( R.id.item_list_recycler_view );
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.addItemDecoration( new MyDividerItemDecoration( this ,
                DividerItemDecoration.VERTICAL , 36 ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( mAdapter );

        //istanzia il listner per il callback del caricamento luoghi
        myDBListner = new MyListeners.LuoghiList() {
            @Override
            public void onList() {
                if (UserUtils.myLocationIsSetted && !requestCat.equals( REQUEST_GET_EVENTS )) {
                    for (Luogo luogo : luogoList) {
                        luogo.setDistance( luogo.calculateDistanceTo( UserUtils.myLocation ) );
                    }
                    Collections.sort( luogoList , Luogo.getDistanceOrderingWhithPref() );
                    mAdapter.notifyDataSetChanged();
                } else {
                    Collections.sort( luogoList );
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {
                switch (error) {
                    case VOLLEY_ERROR_JSON:
                        Log.i( TAG , TAG_CLASS + ": entered listnerOnError, error in pharsing the Json recieved from server" );
                        break;
                    case VOLLEY_ERROR_CONNECTION:
                        Log.i( TAG , TAG_CLASS + ": entered listnerOnError, error on the server" );
                        break;
                }
                Snackbar.make( findViewById( R.id.drawer_layout ) ,
                        getResources().getString( R.string.str_fail_get_luoghi ) ,
                        Snackbar.LENGTH_LONG )
                        .setAction( "Action" , null ).show();
            }
        };
        //richiede i luoghi della categoria scelta e riceve la notifica di caricamento nel listner

        requestList();
        final SwipeRefreshLayout swipeRefreshLayout = findViewById( R.id.swipe_refresh );
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestList();
                swipeRefreshLayout.setRefreshing( false );
            }
        } );

        mLocationListner = new MyListeners.UserLocationCallback() {
            @Override
            public void onLocation(Location location) {
                if (!requestCat.equals( REQUEST_GET_EVENTS )) {
                    for (Luogo luogo : luogoList) {
                        luogo.setDistance( luogo.calculateDistanceTo( UserUtils.myLocation ) );
                    }
                    Collections.sort( luogoList , Luogo.getDistanceOrderingWhithPref() );
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        MyScrollListener myScrollListener = new MyScrollListener(mySwitchCategory);
        recyclerView.addOnScrollListener(myScrollListener);
    }

    private void requestList() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
            luogoList.clear();
            ControllerRemoteDB controller = new ControllerRemoteDB( this );
            controller.getLuoghiList( requestCat , luogoList , myDBListner );
        } else {
            Snackbar.make( findViewById( R.id.drawer_layout ) ,
                    getResources().getString( R.string.str_error_not_connected ) ,
                    Snackbar.LENGTH_LONG )
                    .setAction( "Action" , null ).show();
            //Toast.makeText( this , this.getResources().getString( R.string.str_error_not_connected ) , Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i( TAG , TAG_CLASS + ":entered onStart()" );
        myUserLocation = new UserLocation( this , mLocationListner );
        myUserLocation.startLocationUpdates();
        //ordina la lista mettendo per primi i preferiti, e poi ordinando per stelle
        for (int i = 0; i < luogoList.size(); i++) {
            int order = luogoList.get( i ).getVoto();
            if (UserUtils.codPref.contains( luogoList.get( i ).getCod() )) order = order + 10;
            luogoList.get( i ).setOrder( order );
        }
        Collections.sort( luogoList );
        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
        myUserLocation.stopLocationUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode , @Nullable Intent data) {
        super.onActivityResult( requestCode , resultCode , data );
        if (requestCode == UserLocation.REQUEST_CHECK_SETTINGS && resultCode != RESULT_OK) {
            UserUtils.myLocationIsSetted = false;
            Collections.sort( luogoList );
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setCounter(String[] arrayRes , String[] arrayTags , String tag) {
        SharedPreferences list = getSharedPreferences( PREFS_NAME , 0 );
        SharedPreferences.Editor edit = list.edit();
        order = list.getString( ORDER , "" ).split( "," );
        int max = list.getInt( MAX , 0 );
        int val = list.getInt( tag , 0 ) + 1;
        edit.putInt( tag , val );
        edit.apply();
        if (val > max) {
            edit.putInt( MAX , val );
            edit.apply();
            int index = findPos( arrayTags , tag );
            int pos = findPos( order , String.valueOf( index ) );
            boolean skip = list.getBoolean( SKIP , false );
            if (pos != 0 && !skip) {
                showDialog( edit , arrayRes , tag , pos );
            }
        }
    }

    private void showDialog(final SharedPreferences.Editor edit , final String[] arrayRes , final String tag , final int pos) {
        final AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( getResources().getString( R.string.strYouSelected ) + " " + arrayRes[Integer.valueOf( order[pos] )] + " " + getResources().getString( R.string.strThisFilter ) )
                .setTitle( getResources().getString( R.string.strFilter ) )
                .setCancelable( false );

        View view = getLayoutInflater().inflate( R.layout.dialog_app_updates , null );
        final CheckBox checkBox = view.findViewById( R.id.checkBox );
        builder.setView( view );

        AlertDialog dialog = builder.create();
        builder.setPositiveButton( getResources().getString( R.string.strYes ) , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog , int id) {
                String temp = order[0];
                order[0] = order[pos];
                order[pos] = temp;
                String o = "";
                for (int i = 0; i < order.length - 1; i++) {
                    o += order[i] + ",";
                }
                o += order[order.length - 1];
                edit.putString( ORDER , o );
                edit.apply();
                changeChipsOrder( arrayRes , arrayTags , tag );
                setSkip( edit , checkBox.isChecked() );
            }
        } );
        builder.setNeutralButton( getResources().getString( R.string.strNo ) , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog , int id) {
                setSkip( edit , checkBox.isChecked() );
            }
        } );
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setSkip(SharedPreferences.Editor edit , boolean checked) {
        edit.putBoolean( SKIP , checked );
        edit.apply();
    }

    private int findPos(String[] arrayTags , String tag) {
        for (int i = 0; i < arrayTags.length; i++) {
            if (arrayTags[i].equals( tag )) {
                return i;
            }
        }
        return -1;
    }

    private void changeChipsOrder(final String[] arrayRes , final String[] arrayTags , String tag) {
        ChipGroup chipGroup = findViewById( R.id.chipGroup );
        chipGroup.removeAllViews();
        SharedPreferences list = getSharedPreferences( PREFS_NAME , 0 );
        order = list.getString( "order" , "" ).split( "," );
        for (int id = 0; id < order.length; id++) {
            final Chip newChip = new Chip( this );
            newChip.setId( id );
            assert arrayTags != null;
            newChip.setTag( arrayTags[Integer.valueOf( order[id] )] );
            newChip.setChipText( arrayRes[Integer.valueOf( order[id] )] );
            newChip.setClickable( true );
            newChip.setCheckable( true );
            if (newChip.getTag().toString().equals( tag )) {
                newChip.setChecked( true );
            }
            newChip.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query;
                    if (newChip.isChecked()) {
                        query = newChip.getTag().toString();
                        setCounter( arrayRes , arrayTags , newChip.getTag().toString() );
                    } else {
                        query = "";
                    }
                    mAdapter.getFilter().filter( query );
                }
            } );
            chipGroup.addView( newChip );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu( menu );
        getMenuInflater().inflate( R.menu.item_list_menu , menu );

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE );
        searchView = (SearchView) menu.findItem( R.id.app_bar_search )
                .getActionView();
        searchView.setSearchableInfo( searchManager
                .getSearchableInfo( getComponentName() ) );
        searchView.setMaxWidth( Integer.MAX_VALUE );
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById( android.support.v7.appcompat.R.id.search_src_text );
        searchAutoComplete.setHintTextColor( getResources().getColor( R.color.colorAccent ) );
        searchAutoComplete.setTextColor( getResources().getColor( R.color.colorAccent ) );

        android.support.v7.widget.SearchView searchView2 = (android.support.v7.widget.SearchView) menu.findItem( R.id.app_bar_search ).getActionView();
        ImageView icon = searchView2.findViewById( android.support.v7.appcompat.R.id.search_button );
        ImageView icon2 = searchView2.findViewById( android.support.v7.appcompat.R.id.search_close_btn );
        icon.setColorFilter( Color.WHITE );
        icon2.setColorFilter( Color.WHITE );

        searchView.setOnSearchClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Open", Toast.LENGTH_SHORT).show();
                ActionBar actionbar = getSupportActionBar();
                final Drawable upArrow = getResources().getDrawable( R.drawable.abc_ic_ab_back_material );
                upArrow.setColorFilter( getResources().getColor( R.color.colorAccent ) , PorterDuff.Mode.SRC_ATOP );
                Objects.requireNonNull( getSupportActionBar() ).setHomeAsUpIndicator( upArrow );
                assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
                actionbar.setDisplayHomeAsUpEnabled( true );
                arrow = true;
            }
        } );


        searchView.setOnCloseListener( new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ActionBar actionbar = getSupportActionBar();
                assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
                actionbar.setDisplayHomeAsUpEnabled( true );
                actionbar.setHomeAsUpIndicator( R.drawable.ic_hamburger );
                invalidateOptionsMenu();
                hideKeyboard();
                arrow = false;
                return true;
            }
        } );


        // listening to search query text change
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter( query );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter( query );
                return false;
            }
        } );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!arrow) {
            Boolean open = myNavigationDrawer.openMenu( item );
            if (open) return open;

            switch (id) {
                case R.id.home:
                    myNavigationDrawer.openMenu( item );
                case R.id.app_bar_search:
                    break;

            }
            return super.onOptionsItemSelected( item );
        } else {
            arrow = false;
            ActionBar actionbar = getSupportActionBar();
            assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
            actionbar.setDisplayHomeAsUpEnabled( true );
            actionbar.setHomeAsUpIndicator( R.drawable.ic_hamburger );
            mAdapter.getFilter().filter( "" );
            invalidateOptionsMenu();
            hideKeyboard();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified( true );
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onItemsSelected(Luogo item) {
        Intent intent;
        if (item instanceof Evento) {
            intent = new Intent( this , EventoDetailActivity.class );
        } else {
            intent = new Intent( this , LuogoDetailActivity.class );
        }
        intent.putExtra( INTENT_LUOGO_COD , item.getCod() );
        startActivity( intent );
        overridePendingTransition( R.anim.slide_in , R.anim.slide_out );
    }

    public String getItems_type() {//serve nel drawe per capire se cambiare activy o bloccare l'intent
        return items_type;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService( Activity.INPUT_METHOD_SERVICE );
        imm.toggleSoftInput( InputMethodManager.HIDE_IMPLICIT_ONLY , 0 );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode ,
                                           @NonNull String permissions[] ,
                                           @NonNull int[] grantResults) {
        myUserLocation.onRequestPermissionsResult( requestCode , permissions , grantResults );
    }

}
