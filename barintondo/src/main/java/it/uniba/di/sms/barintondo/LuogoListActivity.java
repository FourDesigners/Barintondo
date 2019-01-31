package it.uniba.di.sms.barintondo;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import it.uniba.di.sms.barintondo.utils.ControllerDBListner;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.LuogoAdapter;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;
import it.uniba.di.sms.barintondo.utils.ToolbarSwitchCategories;
import it.uniba.di.sms.barintondo.utils.UserUtils;

public class LuogoListActivity extends AppCompatActivity implements Constants, LuogoAdapter.ItemsAdapterListener {

    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;
    private static String items_type;
    private RequestQueue mRequestQueue;

    private static final String TAG = LuogoListActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Luogo> luogoList;
    private LuogoAdapter mAdapter;
    private SearchView searchView;
    private static LuogoListActivity mInstance;
    private ToolbarSwitchCategories mySwitchCategory;
    String[] arrayRes = null;
    String[] arrayTags = null;
    String[] order = null;
    ControllerDBListner myDBListner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_luogo_list);
        mInstance = this;

        //first time intent reading
        items_type = getIntent().getStringExtra( Constants.INTENT_ACTIVITY_ITEM_TYPE );

        //toolbar setup
        myToolbar = findViewById( R.id.main_activity_toolbar );
        mySwitchCategory = new ToolbarSwitchCategories( this, items_type );

        Resources res = getResources();
        String requestCat="";
        //first time URL selection
        if (items_type.equals( Constants.INTENT_ATTRACTIONS )) {
            requestCat=REQUEST_GET_ATTRACTIONS;
            arrayRes = res.getStringArray(R.array.attractions);
            arrayTags = res.getStringArray(R.array.attractionsTags);
            myToolbar.setTitle( R.string.attractionsToolbarTitle );
            //URL = "http://barintondo.altervista.org/get_all_attrazioni.php";
        } else if (items_type.equals( Constants.INTENT_EATING )) {
            requestCat=REQUEST_GET_EAT;
            arrayRes = res.getStringArray(R.array.eating);
            arrayTags = res.getStringArray(R.array.eatingTags);
            myToolbar.setTitle( R.string.eating_option );
           // URL = "http://barintondo.altervista.org/get_all_locali.php";
        } else if (items_type.equals( Constants.INTENT_SLEEPING )) {
            requestCat= REQUEST_GET_SLEEP;
            arrayRes = res.getStringArray(R.array.sleeping);
            arrayTags = res.getStringArray(R.array.sleepingTags);
            myToolbar.setTitle( R.string.sleeping_option );
            //URL = "http://barintondo.altervista.org/get_all_rifugi.php";
        } else if (items_type.equals( Constants.INTENT_EVENTS )) {
            requestCat=REQUEST_GET_EVENTS;
            arrayRes = res.getStringArray(R.array.events);
            arrayTags = res.getStringArray(R.array.eventsTags);
            myToolbar.setTitle( R.string.events_option );
            //URL = "http://barintondo.altervista.org/get_all_eventi.php";
        } else if (items_type.equals( Constants.INTENT_NEAR )) {
            requestCat=REQUEST_GET_NEAR_BARI;
            arrayRes = res.getStringArray(R.array.near);
            arrayTags = res.getStringArray(R.array.nearTags);
            myToolbar.setTitle( R.string.near_option);
            //URL = "http://barintondo.altervista.org/get_all_vicinanze.php";
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
        if (items_type.equals(Constants.INTENT_ATTRACTIONS) || items_type.equals(Constants.INTENT_NEAR)) {
            changeChipsOrder(arrayRes, arrayTags, null);
        }else {
            int id = 0;
            for (String s : arrayRes) {
                final Chip newChip = new Chip(this);
                newChip.setId(id);
                assert arrayTags != null;
                newChip.setTag(arrayTags[id++]);
                newChip.setChipText(s);
                newChip.setClickable(true);
                newChip.setCheckable(true);
                newChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String query;
                        if(newChip.isChecked())
                            query = newChip.getTag().toString();
                        else query = ""; //stringa vuota così da mostrare tutti i luoghi
                        Log.i(TAG, "Query=" + query);
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.addItemDecoration( new MyDividerItemDecoration( this , DividerItemDecoration.VERTICAL , 36 ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( mAdapter );

        // white background notification bar
        whiteNotificationBar( recyclerView );

        myDBListner = new ControllerDBListner() {
            @Override
            public void onLuogo(Luogo luogo) {}
            @Override
            public void onEvento(Evento evento) {}
            @Override
            public void onList() {
                mAdapter.notifyDataSetChanged();
            }
        };

        //first time populating
        //fetchItems();
        ControllerRemoteDB controller = new ControllerRemoteDB( this );
        controller.getLuoghiList( requestCat , luogoList, myDBListner);

    }

    @Override
    protected void onStart() {
        super.onStart();
        for(int i=0; i<luogoList.size();i++){
            int order=luogoList.get( i ).getVoto();
            if(UserUtils.codPref.contains( luogoList.get( i ).getCod() )) order=order+10;
            luogoList.get( i ).setOrder( order );
        }
        Collections.sort( luogoList );
        mAdapter.notifyDataSetChanged();
    }

    private void setCounter(String[] arrayRes, String[] arrayTags, String tag) {
        SharedPreferences list = getSharedPreferences(PREFS_NAME , 0);
        SharedPreferences.Editor edit = list.edit();
        order = list.getString(ORDER, "").split(",");
        int max = list.getInt(MAX, 0);
        int val = list.getInt(tag, 0) + 1;
        edit.putInt(tag, val);
        edit.apply();
        if(val > max) {
            edit.putInt(MAX, val);
            edit.apply();
            int index = findPos(arrayTags, tag);
            int pos = findPos(order, String.valueOf(index));
            boolean skip = list.getBoolean(SKIP, false);
            if(pos!=0 && !skip) {
                showDialog(edit, arrayRes, tag, pos);
            }
        }
    }

    private void showDialog(final SharedPreferences.Editor edit, final String[] arrayRes, final String tag, final int pos) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.strYouSelected)  + " " + arrayRes[Integer.valueOf(order[pos])] + " " + getResources().getString(R.string.strThisFilter))
                .setTitle(getResources().getString(R.string.strFilter))
                .setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_app_updates, null);
        final CheckBox checkBox = view.findViewById(R.id.checkBox);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        builder.setPositiveButton(getResources().getString(R.string.strYes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String temp = order[0];
                order[0] = order[pos];
                order[pos] = temp;
                String o = "";
                for(int i=0; i<order.length - 1; i++) {
                    o += order[i] + ",";
                }
                o += order[order.length-1];
                edit.putString(ORDER, o);
                edit.apply();
                changeChipsOrder(arrayRes, arrayTags, tag);
                setSkip(edit, checkBox.isChecked());
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.strNo), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setSkip(edit, checkBox.isChecked());
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setSkip(SharedPreferences.Editor edit, boolean checked) {
        edit.putBoolean(SKIP, checked);
        edit.apply();
    }

    private int findPos(String[] arrayTags, String tag) {
        for(int i=0; i<arrayTags.length; i++) {
            if(arrayTags[i].equals(tag)) {
                return i;
            }
        }
        return -1;
    }

    private void changeChipsOrder(final String[] arrayRes, final String[] arrayTags, String tag) {
        ChipGroup chipGroup = findViewById( R.id.chipGroup );
        chipGroup.removeAllViews();
        SharedPreferences list = getSharedPreferences(PREFS_NAME , 0);
        order = list.getString("order", "").split(",");
        for(int id=0; id<order.length; id++) {
            final Chip newChip = new Chip(this);
            newChip.setId(id);
            assert arrayTags != null;
            newChip.setTag(arrayTags[Integer.valueOf(order[id])]);
            newChip.setChipText(arrayRes[Integer.valueOf(order[id])]);
            newChip.setClickable(true);
            newChip.setCheckable(true);
            if(newChip.getTag().toString().equals(tag)) {
                newChip.setChecked(true);
            }
            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query;
                    if(newChip.isChecked()) {
                        query = newChip.getTag().toString();
                        setCounter(arrayRes, arrayTags, newChip.getTag().toString());
                    }else {
                        query = "";
                    }
                    mAdapter.getFilter().filter( query );
                }
            } );
            chipGroup.addView( newChip );
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();

        //check if intent is changed
        String new_items_type = getIntent().getStringExtra(Constants.INTENT_ACTIVITY_ITEM_TYPE );
        if(!(new_items_type.equals(items_type))) {
            items_type = new_items_type;

            //new chip group setup
            ChipGroup chipGroup = findViewById(R.id.chipGroup);
            SharedPreferences categories = getSharedPreferences(PREFS_NAME, 0);

            //new chips creation
            for(String s : categories.getString(items_type, "").split(",")) {
                final Chip newChip = new Chip(this);
                newChip.setChipText(s);
                newChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String query = newChip.getChipText().toString().substring(0, newChip.getChipText().length() - 2);
                        Log.i(TAG, "Query= " + query);
                        mAdapter.getFilter().filter(query);
                        Toast.makeText(getApplicationContext(), newChip.getChipText(), Toast.LENGTH_SHORT).show();
                    }
                });
                newChip.setClickable(true);
                newChip.setCheckable(true);
                chipGroup.addView(newChip);
            }

            //new URL selection
            if (items_type.equals(Constants.INTENT_ATTRACTIONS))
                URL = "http://barintondo.altervista.org/get_all_attrazioni.php";
            else if (items_type.equals(Constants.INTENT_EATING))
                URL = "http://barintondo.altervista.org/get_all_locali.php";
            else if (items_type.equals(Constants.INTENT_SLEEPING))
                URL = "http://barintondo.altervista.org/get_all_rifugi.php";
            else if (items_type.equals(Constants.INTENT_EVENTS))
                URL = "http://barintondo.altervista.org/get_all_eventi.php";
            else if (items_type.equals(Constants.INTENT_NEAR))
                URL = "http://barintondo.altervista.org/get_all_vicinanze.php";

            fetchItems();
        }
    }*/

    public String getItems_type() {
        return items_type;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue( getApplicationContext() );
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag( TAG );
        getRequestQueue().add( req );
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
        Boolean open = myNavigationDrawer.openMenu( item );
        if (open) return open;

        switch (id) {
            case R.id.home:
                myNavigationDrawer.openMenu( item );
            case R.id.app_bar_search:
                break;

        }

        return super.onOptionsItemSelected( item );
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

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility( flags );
            getWindow().setStatusBarColor( Color.WHITE );
        }
    }

    @Override
    public void onItemsSelected(Luogo item) {
        //Toast.makeText( getApplicationContext() , "Selected: " + item.getNome() , Toast.LENGTH_LONG ).show();
        if (item instanceof Evento){
            Intent intent = new Intent( this , EventoDetailActivity.class );
            intent.putExtra( INTENT_LUOGO_COD , item.getCod() );
            startActivity( intent );
        }else {
            Intent intent = new Intent( this , LuogoDetailActivity.class );
            intent.putExtra( INTENT_LUOGO_COD , item.getCod() );
            startActivity( intent );
        }
    }

    public static synchronized LuogoListActivity getInstance() {
        return mInstance;
    }
}
