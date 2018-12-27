package it.uniba.di.sms.barintondo;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.*;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import it.uniba.di.sms.barintondo.utils.JSONParser;

public class ItemShower extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> attractionsList;

    // url to get all attractions list
    private static String url_all_attractions = "http://barintondo.altervista.org/get_all_attrazioni.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_attractions = "attrazioni";
    private static final String TAG_id = "id";
    private static final String TAG_NAME = "nome";

    // attractions JSONArray
    JSONArray attractions = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.all_attractions);
        setContentView(R.layout.activity_item_shower);

        // Hashmap for ListView
        attractionsList = new ArrayList<HashMap<String, String>>();

        // Loading attractions in Background Thread
        new LoadAllattractions().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String newId = ((TextView) view.findViewById(R.id.id)).getText()
                        .toString();

                // Starting new intent
                /*Intent in = new Intent(getApplicationContext(),
                        EditProductActivity.class);
                // sending id to next activity
                in.putExtra(TAG_id, id);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);*/
            }
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllattractions extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ItemShower.this);
            pDialog.setMessage("Loading attractions. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All attractions from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_attractions, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All attractions: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // attractions found
                    // Getting Array of attractions
                    attractions = json.getJSONArray(TAG_attractions);

                    // looping through All attractions
                    for (int i = 0; i < attractions.length(); i++) {
                        JSONObject c = attractions.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_id);
                        String name = c.getString(TAG_NAME);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_id, id);
                        map.put(TAG_NAME, name);

                        // adding HashList to ArrayList
                        attractionsList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all attractions
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            ItemShower.this, attractionsList,
                            R.layout.list_item, new String[] { TAG_id,
                            TAG_NAME},
                            new int[] { R.id.id, R.id.name });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_profile_menu, menu);

        return (super.onCreateOptionsMenu(menu));
    }
}
