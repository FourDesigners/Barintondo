package it.uniba.di.sms.barintondo;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Coupon;
import it.uniba.di.sms.barintondo.utils.CouponAdapter;
import it.uniba.di.sms.barintondo.utils.LocalDBOpenHelper;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;
import it.uniba.di.sms.barintondo.utils.MyListeners;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;
import it.uniba.di.sms.barintondo.utils.MyScrollListener;
import it.uniba.di.sms.barintondo.utils.ToolbarSwitchCategories;

public class CouponListActivity extends AppCompatActivity implements Constants, MyListeners.CouponAdapterListener {

    private String TAG_CLASS = getClass().getSimpleName();
    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;
    private RequestQueue mRequestQueue;
    private RecyclerView recyclerView;
    private List<Coupon> couponList;
    private ToolbarSwitchCategories mySwitchCategory;

    private CouponAdapter mAdapter;
    private SearchView searchView;
    private MyListeners.CouponList couponListListener;

    private static CouponListActivity mInstance;
    private boolean arrow = false;
    private TextView textViewNoCoupon;
    private String luogoName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_coupon_list );
        mInstance = this;

        luogoName = getIntent().getStringExtra( Constants.INTENT_LUOGO_NAME );

        //toolbar setup
        myToolbar = findViewById( R.id.main_activity_toolbar );
        myToolbar.setTitle( R.string.myCoupon_label );
        setSupportActionBar( myToolbar );
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled( true );
        mySwitchCategory = new ToolbarSwitchCategories( this , Constants.INTENT_INTERESES );

        Drawable drawable = getResources().getDrawable( R.drawable.ic_hamburger );
        drawable = DrawableCompat.wrap( drawable );
        DrawableCompat.setTint( drawable , getResources().getColor( R.color.colorAccent ) );

        actionbar.setHomeAsUpIndicator( drawable );

        //nav drawer setup
        myNavigationDrawer = new MyNavigationDrawer( this ,
                (NavigationView) findViewById( R.id.nav_view ) ,
                (DrawerLayout) findViewById( R.id.drawer_layout ) );
        myNavigationDrawer.build();

        //list and adapter setup
        couponList = new ArrayList<>();
        mAdapter = new CouponAdapter( this , couponList , this );

        //recyclerView setup
        recyclerView = findViewById( R.id.coupon_luogo_list_recycler_view );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.addItemDecoration( new MyDividerItemDecoration( this , DividerItemDecoration.VERTICAL , 36 ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( mAdapter );
        
        // Disabilitazione toolbar con scroll
        MyScrollListener myScrollListener = new MyScrollListener(mySwitchCategory);
        recyclerView.addOnScrollListener(myScrollListener);

        couponListListener = new MyListeners.CouponList() {
            @Override
            public void onCouponList() {
                LocalDBOpenHelper.getCouponList( getApplicationContext() , couponList );
                if (couponList.isEmpty()) {
                    textViewNoCoupon = findViewById( R.id.text_view_no_coupons );
                    textViewNoCoupon.setVisibility( View.VISIBLE );
                } else {
                    mAdapter.notifyDataSetChanged();
                    if (luogoName != null) {
                        searchView.setQuery( luogoName , true );
                    }
                }
            }

            @Override
            public void onError(String error) {
                switch (error) {
                    case VOLLEY_ERROR_JSON:
                        Log.i( TAG , TAG_CLASS + ": entered listenerOnError, error in pharsing the Json recieved from server" );
                        break;
                    case VOLLEY_ERROR_CONNECTION:
                        Log.i( TAG , TAG_CLASS + ": entered listenerOnError, error on the server" );
                        break;
                }
            }
        };

        //first time populating
        //fetchItems();
        requestList();
        final SwipeRefreshLayout swipeRefreshLayout = findViewById( R.id.swipe_refresh );
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestList();
                swipeRefreshLayout.setRefreshing( false );
            }
        } );
    }

    private void requestList() {

        couponList.clear();
        ControllerRemoteDB controllerRemoteDB = new ControllerRemoteDB( this );
        controllerRemoteDB.getCouponList( couponList , couponListListener );
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu( menu );
        getMenuInflater().inflate( R.menu.item_list_menu , menu );

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE );
        SearchView searchView = (SearchView) menu.findItem( R.id.app_bar_search )
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
    public void onItemsSelected(Coupon item) {
        //Toast.makeText( getApplicationContext() , "Selected: " + item.getNome() , Toast.LENGTH_LONG ).show();
        Intent intent = new Intent( this , CouponDetailActivity.class );
        intent.putExtra( INTENT_COUPON , item );
        startActivity( intent );
    }

    public static synchronized CouponListActivity getInstance() {
        return mInstance;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService( Activity.INPUT_METHOD_SERVICE );
        imm.toggleSoftInput( InputMethodManager.HIDE_IMPLICIT_ONLY , 0 );
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestList();
    }
}
