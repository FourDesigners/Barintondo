package it.uniba.di.sms.barintondo;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.CouponLuogo;
import it.uniba.di.sms.barintondo.utils.CouponLuogoAdapter;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;

public class CouponLuogoListActivity extends AppCompatActivity implements Constants, CouponLuogoAdapter.ItemsAdapterListener {

    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;
    private RequestQueue mRequestQueue;

    private static final String TAG = CouponLuogoListActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<CouponLuogo> couponList;

    private CouponLuogoAdapter mAdapter;
    private SearchView searchView;

    private static CouponLuogoListActivity mInstance;
    private boolean arrow = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_coupon_luogo_list);
        mInstance = this;

        //toolbar setup
        myToolbar = findViewById( R.id.main_activity_toolbar );
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_hamburger);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorAccent));

        actionbar.setHomeAsUpIndicator(drawable);

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
        couponList = new ArrayList<>();
        mAdapter = new CouponLuogoAdapter( this , couponList , this );

        //recyclerView setup
        recyclerView = findViewById( R.id.coupon_luogo_list_recycler_view );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.addItemDecoration( new MyDividerItemDecoration( this , DividerItemDecoration.VERTICAL , 36 ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( mAdapter );

        //first time populating
        //fetchItems();
        ControllerRemoteDB controller = new ControllerRemoteDB( this );
        controller.getCouponList( couponList, mAdapter );
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
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.colorAccent));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.colorAccent));

        android.support.v7.widget.SearchView searchView2 = (android.support.v7.widget.SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        ImageView icon = searchView2.findViewById(android.support.v7.appcompat.R.id.search_button);
        ImageView icon2 = searchView2.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        icon.setColorFilter(Color.WHITE);
        icon2.setColorFilter(Color.WHITE);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Open", Toast.LENGTH_SHORT).show();
                ActionBar actionbar = getSupportActionBar();
                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
                upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
                assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
                actionbar.setDisplayHomeAsUpEnabled(true);
                arrow = true;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
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
        });

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
        if(!arrow) {
            Boolean open = myNavigationDrawer.openMenu( item );
            if (open) return open;

            switch (id) {
                case R.id.home:
                    myNavigationDrawer.openMenu( item );
                case R.id.app_bar_search:
                    break;

            }
            return super.onOptionsItemSelected( item );
        }else {
            arrow = false;
            ActionBar actionbar = getSupportActionBar();
            assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
            actionbar.setDisplayHomeAsUpEnabled( true );
            actionbar.setHomeAsUpIndicator( R.drawable.ic_hamburger );
            mAdapter.getFilter().filter("");
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
    public void onItemsSelected(CouponLuogo item) {
        //Toast.makeText( getApplicationContext() , "Selected: " + item.getNome() , Toast.LENGTH_LONG ).show();
        Intent intent = new Intent( this , CouponDetailActivity.class );
        intent.putExtra( INTENT_COUPON , item );
        startActivity( intent );
    }

    public static synchronized CouponLuogoListActivity getInstance() {
        return mInstance;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
