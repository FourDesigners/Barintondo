package it.uniba.di.sms.barintondo.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.barintondo.InterestsListActivity;
import it.uniba.di.sms.barintondo.LuogoDetailActivity;
import it.uniba.di.sms.barintondo.R;

public class ControllerPrefered implements Constants {

    String email;
    Context context;
    String Url;

    public ControllerPrefered(Context context) {
        this.context = context;

        Url = "http://barintondo.altervista.org/gestore_interessi.php";
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

    private void volleyCall(final String requestOp , final String user , final String luogoCod) {
        // Log.i( TAG , getClass().getSimpleName() + ":entered volleyCall( )");
        final LuogoDetailActivity luogoDetailActivity = (LuogoDetailActivity) context;

        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                String[] result = response.split( "," );
                Log.i( TAG , "ControllerPrefered: entered onResponse(), request: " + result[0] + ", result: " + result[1] );


                switch (result[0]) {
                    case REQUEST_CHECK_PREF:
                        luogoDetailActivity.checkPrefResult( Boolean.valueOf( result[1] ) );
                        break;
                    case REQUEST_ADD_PREF:
                        boolean added = result[1].equals( REQUEST_RESULT_OK );
                        luogoDetailActivity.prefAdded( added );
                        break;
                    case REQUEST_REMOVE_PREF:
                        boolean removed = result[1].equals( REQUEST_RESULT_OK );
                        luogoDetailActivity.prefRemoved( removed );
                        break;
                }

            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText( context , context.getResources().getString( R.string.str_fail_pref_managing ) , Toast.LENGTH_SHORT ).show();
            }
        } ) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , requestOp );
                MyData.put( "email" , user );
                MyData.put( "luogoCod" , luogoCod );
                return MyData;
            }
        };


        MyRequestQueue.add( MyStringRequest );
    }


    public void getAllPrefered() {
        Log.i( TAG ,  "ControllerPrefered: entered getAllPrefered()" );
        final ArrayList<Luogo> interestsList = new ArrayList<>();

        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.i( TAG ,  "ControllerPrefered: entered onResponse()"+response );
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                try {

                    JSONArray jsonArray = new JSONArray( response );

                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject( i );

                            Luogo luogo = new Luogo();
                            luogo.setCod( jsonObject.getString( "cod" ) );
                            luogo.setNome( jsonObject.getString( "nome" ) );
                            luogo.setCategoria( jsonObject.getString( "nomeCategoria" ));
                            luogo.setSottoCat( jsonObject.getString( "sottoCategoria" ) );
                            luogo.setOraA( jsonObject.getString( "oraA" ) );
                            luogo.setOraC( jsonObject.getString( "oraC" ) );
                            luogo.setThumbnailLink( jsonObject.getString( "thumbnail" ) );
                            luogo.setDescrizione_en( jsonObject.getString( "descrizione_en" ) );
                            luogo.setDescrizione_it( jsonObject.getString( "descrizione_it" ) );
                            luogo.setIndirizzo( jsonObject.getString( "indirizzo" ) );
                            //Log.i( TAG , "Item" + i + ": " + item.toString() + " sottocat: " + item.getSottoCat() );

                            //adding items to itemsList
                            interestsList.add( luogo );

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText( context , context.getResources().getString( R.string.str_fail_pref_managing ) , Toast.LENGTH_SHORT ).show();
                        }
                    }

                } catch (JSONException e2) {
                    e2.printStackTrace();
                    Toast.makeText( context , context.getResources().getString( R.string.str_fail_pref_managing ) , Toast.LENGTH_SHORT ).show();
                }
                InterestsListActivity interestsListActivity = (InterestsListActivity) context;
                interestsListActivity.setupRecyclerView(interestsList);

            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText( context , context.getResources().getString( R.string.str_fail_pref_managing ) , Toast.LENGTH_SHORT ).show();
            }
        } ) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , REQUEST_GET_ALL_PREF );
                MyData.put( "email" , email );
                return MyData;
            }
        };


        MyRequestQueue.add( MyStringRequest );
    }
}