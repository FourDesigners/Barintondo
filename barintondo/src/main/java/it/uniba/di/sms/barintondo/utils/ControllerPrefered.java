package it.uniba.di.sms.barintondo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.uniba.di.sms.barintondo.ItemDetailActivity;
import it.uniba.di.sms.barintondo.R;

public class ControllerPrefered implements Constants {

    String email;
    Context context;
    ItemDetailActivity itemDetailActivity;

    public ControllerPrefered(Context context) {
        this.context = context;
        itemDetailActivity = (ItemDetailActivity) context;
        //prelevo dati dal db
        ProfileOpenHelper dbHelper = new ProfileOpenHelper( context , DB_NAME , null , 1 );
        SQLiteDatabase myDB = dbHelper.getReadableDatabase();
        //definisco la query
        String[] columns = {COLUMN_EMAIL};
        Cursor myCursor;
        //ottengo il cursore
        myCursor = myDB.query( TABLE_UTENTE , columns , null , null , null , null , null , null );
        myCursor.moveToFirst();

        email = myCursor.getString( myCursor.getColumnIndex( COLUMN_EMAIL ) );

        myCursor.close();
        myDB.close();
    }

    public void checkPref(String itemCod) {
        volleyCall( REQUEST_CHECK_PREF , email , itemCod );
    }

    public void addPref(String itemCod) {
        volleyCall( REQUEST_ADD_PREF , email , itemCod );
    }

    public void removePref(String itemCod) {
        volleyCall( REQUEST_REMOVE_PREF , email , itemCod );
    }

    private void volleyCall(final String requestOp , final String user , final String itemCod) {
        // Log.i( TAG , getClass().getSimpleName() + ":entered volleyCall( )");

        String Url = "http://barintondo.altervista.org/gestore_interessi.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                String[] result = response.split( "," );
                Log.i( TAG , "ControllerPrefered: entered onResponse(), request: "+result[0]+", result: "+result[1] );


                switch (result[0]) {
                    case REQUEST_CHECK_PREF:
                        itemDetailActivity.checkPrefResult( Boolean.valueOf( result[1] ) );
                        break;
                    case REQUEST_ADD_PREF:
                        boolean added = result[1].equals( REQUEST_RESULT_OK );
                        itemDetailActivity.prefAdded( added );
                        break;
                    case REQUEST_REMOVE_PREF:
                        boolean removed = result[1].equals( REQUEST_RESULT_OK );
                        itemDetailActivity.prefRemoved( removed );
                        break;
                }

            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(context, context.getResources().getString( R.string.str_fail_pref_managing ), Toast.LENGTH_SHORT).show();
            }
        } ) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , requestOp );
                MyData.put( "email" , user );
                MyData.put( "itemCod" , itemCod );
                return MyData;
            }
        };


        MyRequestQueue.add( MyStringRequest );
    }
}