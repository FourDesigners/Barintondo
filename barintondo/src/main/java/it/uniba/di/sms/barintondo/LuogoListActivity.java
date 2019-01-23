package it.uniba.di.sms.barintondo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.LuogoAdapter;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;

public class LuogoListActivity extends AppCompatActivity implements Constants, LuogoAdapter.ItemsAdapterListener {

    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;
    private static String items_type;
    private RequestQueue mRequestQueue;

    private static final String TAG = LuogoListActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Luogo> itemList;
    private LuogoAdapter mAdapter;
    private SearchView searchView;
    String URL;
    private static LuogoListActivity mInstance;
    String[] arrayRes = null;
    String[] arrayTags = null;
    String[] order = null;

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

        Resources res = getResources();

        //first time URL selection
        if (items_type.equals( Constants.INTENT_ATTRACTIONS )) {
            arrayRes = res.getStringArray(R.array.attractions);
            arrayTags = res.getStringArray(R.array.attractionsTags);
            myToolbar.setTitle( R.string.attractionsToolbarTitle );
            URL = "http://barintondo.altervista.org/get_all_attrazioni.php";
        } else if (items_type.equals( Constants.INTENT_EATING )) {
            arrayRes = res.getStringArray(R.array.eating);
            arrayTags = res.getStringArray(R.array.eatingTags);
            myToolbar.setTitle( R.string.eating_option );
            URL = "http://barintondo.altervista.org/get_all_locali.php";
        } else if (items_type.equals( Constants.INTENT_SLEEPING )) {
            arrayRes = res.getStringArray(R.array.sleeping);
            arrayTags = res.getStringArray(R.array.sleepingTags);
            myToolbar.setTitle( R.string.sleeping_option );
            URL = "http://barintondo.altervista.org/get_all_rifugi.php";
        } else if (items_type.equals( Constants.INTENT_EVENTS )) {
            arrayRes = res.getStringArray(R.array.events);
            arrayTags = res.getStringArray(R.array.eventsTags);
            myToolbar.setTitle( R.string.events_option );
            URL = "http://barintondo.altervista.org/get_all_eventi.php";
        } else if (items_type.equals( Constants.INTENT_NEAR )) {
            arrayRes = res.getStringArray(R.array.near);
            arrayTags = res.getStringArray(R.array.nearTags);
            myToolbar.setTitle( R.string.near_option);
            URL = "http://barintondo.altervista.org/get_all_vicinanze.php";
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
        itemList = new ArrayList<>();
        mAdapter = new LuogoAdapter( this , itemList , this );

        //recyclerView setup
        recyclerView = findViewById( R.id.item_list_recycler_view );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.addItemDecoration( new MyDividerItemDecoration( this , DividerItemDecoration.VERTICAL , 36 ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( mAdapter );

        // white background notification bar
        whiteNotificationBar( recyclerView );

        //first time populating
        fetchItems();

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
                showDialog(edit, tag, pos);
            }
        }
    }

    private void showDialog(final SharedPreferences.Editor edit, final String tag, final int pos) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.strChangeOrder))
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

    private void fetchItems() {
        //itemList.clear();

        final ProgressDialog progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( getResources().getString( R.string.loadingMessage ) );
        progressDialog.show();

        JsonArrayRequest request = new JsonArrayRequest( URL , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    Toast.makeText( getApplicationContext() , "Couldn't fetch the contacts! Pleas try again." , Toast.LENGTH_LONG ).show();
                    progressDialog.dismiss();
                    return;
                }

                //Log.e("NUM", String.valueOf(response.length()));
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject( i );

                        Luogo item = new Luogo();
                        item.setCod( jsonObject.getString( "cod" ) );
                        item.setNome( jsonObject.getString( "nome" ) );
                        item.setSottoCat( jsonObject.getString( "sottoCategoria" ) );
                        item.setOraA( jsonObject.getString( "oraA" ) );
                        item.setOraC( jsonObject.getString( "oraC" ) );
                        item.setThumbnailLink( jsonObject.getString( "thumbnail" ) );
                        item.setDescrizione_en( jsonObject.getString( "descrizione_en" ) );
                        item.setDescrizione_it( jsonObject.getString( "descrizione_it" ) );
                        item.setIndirizzo( jsonObject.getString( "indirizzo" ) );
                        Log.i( TAG , "Item" + i + ": " + item.toString() + " sottocat: " + item.getSottoCat() );

                        //adding items to itemsList
                        itemList.add( item );
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }
        } , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e( TAG , "Volley Error: " + error.getMessage() );
            }
        } );

        LuogoListActivity.getInstance().addToRequestQueue( request );

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
        Intent intent = new Intent( this , ItemDetailActivity.class );
        intent.putExtra( INTENT_ITEM , item );
        startActivity( intent );
    }

    public static synchronized LuogoListActivity getInstance() {
        return mInstance;
    }
}
