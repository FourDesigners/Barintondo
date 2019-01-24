package it.uniba.di.sms.barintondo;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.CouponLuogo;
import it.uniba.di.sms.barintondo.utils.CouponLuogoAdapter;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;
import it.uniba.di.sms.barintondo.utils.ProfileOpenHelper;

public class CouponLuogoListActivity extends AppCompatActivity implements Constants, CouponLuogoAdapter.ItemsAdapterListener {

    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;
    private RequestQueue mRequestQueue;

    private static final String TAG = CouponLuogoListActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<CouponLuogo> itemList;
    private CouponLuogoAdapter mAdapter;
    private SearchView searchView;
    String URL;
    private static CouponLuogoListActivity mInstance;
    String[] arrayRes = null;
    String[] arrayTags = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_coupon_luogo_list);
        mInstance = this;

        //toolbar setup
        myToolbar = findViewById( R.id.main_activity_toolbar );
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
        //ChipGroup chipGroup = findViewById( R.id.chipGroup );

        //first time chips creation
        /*if (items_type.equals(Constants.INTENT_ATTRACTIONS) || items_type.equals(Constants.INTENT_NEAR)) {
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
        }*/

        //list and adapter setup
        itemList = new ArrayList<>();
        mAdapter = new CouponLuogoAdapter( this , itemList , this );

        //recyclerView setup
        recyclerView = findViewById( R.id.coupon_luogo_list_recycler_view );
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

    /*private void setCounter(String[] arrayRes, String[] arrayTags, String tag) {
        SharedPreferences list = getSharedPreferences(PREFS_NAME , 0);
        SharedPreferences.Editor edit = list.edit();
        String[] order = list.getString("order", "").split(",");
        int max = list.getInt("max", 0);
        int val = list.getInt(tag, 0) + 1;
        if(val > max) {
            edit.putInt(MAX, val);
            int index = findPos(arrayTags, tag);
            int pos = findPos(order, String.valueOf(index));
            String temp = order[0];
            order[0] = order[pos];
            order[pos] = temp;
            String o = "";
            for(int i=0; i<order.length - 1; i++) {
                o += order[i] + ",";
            }
            o += order[order.length-1];
            edit.putString(ORDER, o);
        }
        edit.putInt(tag, val);
        edit.apply();
        changeChipsOrder(arrayRes, arrayTags, tag);
    }*/

    /*private int findPos(String[] arrayTags, String tag) {
        for(int i=0; i<arrayTags.length; i++) {
            if(arrayTags[i].equals(tag)) {
                return i;
            }
        }
        return -1;
    }*/

    /*private void changeChipsOrder(final String[] arrayRes, final String[] arrayTags, String tag) {
        ChipGroup chipGroup = findViewById( R.id.chipGroup );
        chipGroup.removeAllViews();
        SharedPreferences list = getSharedPreferences(PREFS_NAME , 0);
        String[] order = list.getString("order", "").split(",");
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
                    }else {
                        query = "";
                    }
                    mAdapter.getFilter().filter( query );
                    setCounter(arrayRes, arrayTags, newChip.getTag().toString());
                }
            } );
            chipGroup.addView( newChip );
        }
    }*/

    private void fetchItems() {
        //itemList.clear();

        final ProgressDialog progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( getResources().getString( R.string.loadingMessage ) );
        progressDialog.show();

        //prelevo email dal db
        ProfileOpenHelper dbHelper = new ProfileOpenHelper( mInstance , DB_NAME , null , 1 );
        SQLiteDatabase myDB = dbHelper.getReadableDatabase();
        //definisco la query
        String[] columns = {COLUMN_EMAIL};
        Cursor myCursor;
        //ottengo il cursore
        myCursor = myDB.query( TABLE_UTENTE , columns , null , null , null , null , null , null );
        myCursor.moveToFirst();

        String email = myCursor.getString( myCursor.getColumnIndex( COLUMN_EMAIL ) );

        myCursor.close();
        myDB.close();

        //creazione URL
        String URL = "http://barintondo.altervista.org/get_my_coupons.php?email=" + email;

        JsonArrayRequest request = new JsonArrayRequest( URL , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    Toast.makeText( getApplicationContext() , "Couldn't fetch the contacts! Pleas try again." , Toast.LENGTH_LONG ).show();
                    progressDialog.dismiss();
                    return;
                }

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject( i );

                        CouponLuogo item = new CouponLuogo();
                        item.setCod( jsonObject.getString( "cod" ) );
                        item.setLuogo( jsonObject.getString( "luogo" ) );
                        item.setScadenza( jsonObject.getString( "scadenza" ) );
                        item.setSottoCat( jsonObject.getString( "sottoCategoria" ) );
                        item.setDescrizione_it( jsonObject.getString( "descrizione_it" ) );
                        item.setDescrizione_en( jsonObject.getString( "descrizione_en" ) );

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
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        } );

        CouponLuogoListActivity.getInstance().addToRequestQueue( request );

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
    public void onItemsSelected(CouponLuogo item) {
        //Toast.makeText( getApplicationContext() , "Selected: " + item.getNome() , Toast.LENGTH_LONG ).show();
        Intent intent = new Intent( this , CouponDetailActivity.class );
        intent.putExtra( INTENT_COUPON , item );
        startActivity( intent );
    }

    public static synchronized CouponLuogoListActivity getInstance() {
        return mInstance;
    }
}
