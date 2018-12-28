package it.uniba.di.sms.barintondo;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.json.*;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import it.uniba.di.sms.barintondo.utils.BarintondoContent;
import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.JSONParser;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;

import static it.uniba.di.sms.barintondo.utils.BarintondoContent.BITEMS;

public class ItemListActivity extends AppCompatActivity implements Constants {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_item_list );

        myToolbar = findViewById( R.id.main_activity_toolbar );
        setSupportActionBar( myToolbar );
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled( true );
        actionbar.setHomeAsUpIndicator( R.drawable.ic_hamburger );

        myNavigationDrawer =new MyNavigationDrawer(this,
                (NavigationView) findViewById(R.id.nav_view),
                (DrawerLayout)findViewById(R.id.drawer_layout));
        myNavigationDrawer.build();

        String items_type = getIntent().getStringExtra(Constants.INTENT_ACTIVITY_RISULTATO);
        String urlToLoad = "";
        String tag = "";
        if(items_type.equals(Constants.INTENT_ATTRAZIONI)) {
            urlToLoad = "http://barintondo.altervista.org/get_all_attrazioni.php";
            tag = Constants.INTENT_ATTRAZIONI;
        }

        // Loading attractions in Background Thread
        new LoadBarintondoItem(urlToLoad, tag).execute();

    }




    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate( R.menu.my_profile_menu , menu );

        return (super.onCreateOptionsMenu( menu ));
    }*/

    public void setRecyclerView() {
        Log.i( TAG , getClass().getSimpleName() + ":entered setRecyclerView()" );
        mRecyclerView = (RecyclerView) findViewById( R.id.item_list_recycler_view );

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize( true );

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager( this );
        mRecyclerView.setLayoutManager( mLayoutManager );

        // specify an adapter
        mAdapter = new MyAdapter( BITEMS );
        mRecyclerView.setAdapter( mAdapter );
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<BarintondoContent.BarintondoItem> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mItemId;
            public TextView mItemName;

            public MyViewHolder(View v) {
                super( v );
                // Log.i(TAG, getClass().getSimpleName() + ":entered MyViewHolder()");
                mItemId = v.findViewById( R.id.item_id );
                mItemName = v.findViewById( R.id.item_name );
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<BarintondoContent.BarintondoItem> myDataset) {
            //Log.i(TAG, getClass().getSimpleName() + ":entered MyAdpter()");
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {
            //Log.i( TAG , getClass().getSimpleName() + ":entered onCreateViewHolder()" );
            // create a new view
            View v = (View) LayoutInflater.from( parent.getContext() )
                    .inflate( R.layout.item_list_content , parent , false );
            MyViewHolder vh = new MyViewHolder( v );
            return vh;
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder , int position) {
            //Log.i( TAG , getClass().getSimpleName() + ":entered onBindViewHolder()" );
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            BarintondoContent.BarintondoItem item = mDataset.get( position );
            holder.mItemId.setText( String.valueOf( item.getId() ) );
            holder.mItemName.setText( item.getName() );
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            //Log.i(TAG, getClass().getSimpleName() + ":entered getItemCount()");
            return mDataset.size();
        }
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     */

    class LoadBarintondoItem extends AsyncTask<String, String, String> {

        // Progress Dialog
        private ProgressDialog pDialog;

        // Creating JSON Parser object
        JSONParser jParser = new JSONParser();

        // url to get all attractions list
        private  String url_all_attractions;

        // JSON Node names
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_id = "id";
        private static final String TAG_NAME = "nome";

        //JSON Node items TAG
        private String TAG_items;

        // attractions JSONArray
        JSONArray attractions = null;

        LoadBarintondoItem(String url, String tag) {
            url_all_attractions = url;
            TAG_items = tag;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            BITEMS.clear();
            pDialog = new ProgressDialog( ItemListActivity.this );
            pDialog.setMessage( "Loading attractions. Please wait..." );
            pDialog.setIndeterminate( false );
            pDialog.setCancelable( false );
            pDialog.show();
        }

        /**
         * getting All attractions from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            // getting JSON string from URL
            Log.i(TAG, "URL= " + url_all_attractions);
            JSONObject json = jParser.makeHttpRequest( url_all_attractions , "GET" , params );

            // Check your log cat for JSON reponse
            Log.d( "All attractions: " , json.toString() );

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt( TAG_SUCCESS );

                if (success == 1) {
                    // attractions found
                    // Getting Array of attractions
                    attractions = json.getJSONArray(TAG_items);

                    // looping through All attractions
                    for (int i = 0; i < attractions.length(); i++) {
                        JSONObject c = attractions.getJSONObject( i );

                        // Storing each json item in variable
                        int id = Integer.parseInt( c.getString( TAG_id ) );
                        String name = c.getString( TAG_NAME );


                        // add a new BarintondoItem to the list

                        BITEMS.add( new BarintondoContent.BarintondoItem( id , name ) );
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all attractions
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread( new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into RecyclerView
                     * */
                    setRecyclerView();
                }
            } );

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onOptionsItemSelected()");
        Boolean open = myNavigationDrawer.openMenu( item );
        if(open) return open;
        else return super.onOptionsItemSelected(item);
    }
}
