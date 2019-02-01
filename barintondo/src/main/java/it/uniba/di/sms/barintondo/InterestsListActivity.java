package it.uniba.di.sms.barintondo;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.LuogoAdapter;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;
import it.uniba.di.sms.barintondo.utils.ToolbarSwitchCategories;
import it.uniba.di.sms.barintondo.utils.UserUtils;

public class InterestsListActivity extends AppCompatActivity implements Constants, LuogoAdapter.ItemsAdapterListener {

    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;
    TextView noInterests;
    private RecyclerView recyclerView;
    private LuogoAdapter mAdapter;
    private SearchView searchView;
    private ProgressDialog progressDialog;
    private ToolbarSwitchCategories myToolbarSwitchCategories;
    private ArrayList<Luogo> interestsList;
    InterestsListner myInterestsListner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i( TAG , getClass().getSimpleName() + ": entered onCreate()" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_interests_list );

        //toolbar setup
        myToolbar = findViewById( R.id.main_activity_toolbar );
        myToolbar.setTitle( R.string.myInterests_label );
        setSupportActionBar( myToolbar );
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled( true );
        actionbar.setHomeAsUpIndicator( R.drawable.ic_hamburger );

        myToolbarSwitchCategories = new ToolbarSwitchCategories( this , Constants.INTENT_INTERESES );

        //nav drawer setup
        myNavigationDrawer = new MyNavigationDrawer( this ,
                (NavigationView) findViewById( R.id.nav_view ) ,
                (DrawerLayout) findViewById( R.id.drawer_layout ) );
        myNavigationDrawer.build();

        noInterests = findViewById( R.id.text_view_no_interests );
        progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( getResources().getString( R.string.loadingMessage ) );
        progressDialog.show();


        interestsList = new ArrayList<>();
        mAdapter = new LuogoAdapter( this , interestsList , this );
        //recyclerView setup
        recyclerView = findViewById( R.id.item_list_recycler_view );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.addItemDecoration( new MyDividerItemDecoration( this , DividerItemDecoration.VERTICAL , 36 ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( mAdapter );
        myInterestsListner = new InterestsListner() {
            @Override
            public void onInterestsLoaded() {
                mAdapter.notifyDataSetChanged();
                setupView();
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
            ControllerRemoteDB controller = new ControllerRemoteDB( this );
            controller.getAllInterests( interestsList , myInterestsListner );
        }else {
            Toast.makeText( this , this.getResources().getString( R.string.str_error_not_connected ) , Toast.LENGTH_SHORT ).show();
            progressDialog.dismiss();
        }
    }

    private void setupView() {
        if (interestsList.size() > 0) {
            noInterests.setVisibility( View.GONE );
            for (int i = 0; i < interestsList.size(); i++) {
                if (interestsList.get( i ).getVoto() != 0) { //se il voto è zero si tratta di un evento
                    interestsList.get( i ).setOrder( interestsList.get( i ).getVoto() );
                } else
                    interestsList.get( i ).setOrder( 20 ); //serve per visualizzare per primi gli eventi
            }
            Collections.sort( interestsList );
        } else noInterests.setVisibility( View.VISIBLE );
        progressDialog.dismiss();
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
        Intent intent = new Intent( this , LuogoDetailActivity.class );
        intent.putExtra( INTENT_LUOGO_COD , item.getCod() );
        startActivity( intent );
    }

    public interface InterestsListner {
        public void onInterestsLoaded();
    }
}
