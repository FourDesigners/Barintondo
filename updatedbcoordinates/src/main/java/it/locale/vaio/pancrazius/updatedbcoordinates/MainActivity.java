package it.locale.vaio.pancrazius.updatedbcoordinates;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    ArrayList<Luogo> luoghiList;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        luoghiList=new ArrayList<>(  );

        btn = findViewById( R.id.button );
        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLuoghiList();
            }
        } );
    }

    public void getLuoghiList() {
        luoghiList.clear();
        final ProgressDialog progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( "Caricamento" );
        progressDialog.show();

        String Url = "http://barintondo.altervista.org/UpdateLocation.php";
        RequestQueue MyRequestQueue = Volley.newRequestQueue( this );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Log.i( "Test" , "ControllerRemoteDB: entered onResponse() "+response );
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.

                try {

                    JSONArray jsonArray = new JSONArray( response );

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject( i );
                            String cod = jsonObject.getString( "cod" );
                            String indirizzo = jsonObject.getString( "indirizzo" );
                            double lat = 0.0d;
                            double lng = 0.0d;
                            if (!jsonObject.getString( "latitudine" ).equals( "null" )) {
                                lat = jsonObject.getDouble( "latitudine" );
                                lng = jsonObject.getDouble( "latitudine" );
                            }
                            int locS= Integer.valueOf(  jsonObject.getString( "locationSync" ));
                            boolean locationSync;
                            if (locS==1) locationSync=true;
                            else locationSync=false;
                            luoghiList.add( new Luogo( cod , indirizzo , lat , lng, locationSync ) );
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i( "Test" , "Errore nel leggere il json" );
                        }
                    }

                } catch (JSONException e2) {
                    e2.printStackTrace();
                    Log.i( "Test" , "Errore2 nel leggere il json" );
                }

                calcolaLocation();
                progressDialog.dismiss();

            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //This code is executed if there is an error.
                Log.i( "Test" , "Errore onResponse" );
            }
        } ) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , "get_all" );
                return MyData;
            }
        };


        MyRequestQueue.add( MyStringRequest );
    }

    private void calcolaLocation() {
        Geocoder geocoder = new Geocoder( this , Locale.getDefault() );
        for (Luogo l : luoghiList) {
            if (!l.locationSyc) {
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName( l.indirizzo , 1 );

                    Address address = addresses.get( 0 );
                    if (addresses.size() > 0) {
                        double latitude = addresses.get( 0 ).getLatitude();
                        double longitude = addresses.get( 0 ).getLongitude();

                        Log.i( "Test" , "Aggiorno " + l.cod + "(" + l.indirizzo + ") " );
                        setLocation( l.cod , latitude , longitude );

                    } else
                        Log.i( "Test" , "Non caricato Luogo " + l.cod + "(" + l.indirizzo + ") " );

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setLocation(final String cod , final double lat , final double lng) {


        String Url = "http://barintondo.altervista.org/UpdateLocation.php";
        RequestQueue MyRequestQueue = Volley.newRequestQueue( this );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Log.i( "Test" , "ControllerRemoteDB: entered onResponse() "+response );
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.

            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {

                //This code is executed if there is an error.
                Log.i( "Test" , "Errore onResponse" );
            }
        } ) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , "set_location" );
                MyData.put( "cod" , cod );
                MyData.put( "lat" , String.valueOf( lat ) );
                MyData.put( "lng" , String.valueOf( lng ) );
                return MyData;
            }
        };


        MyRequestQueue.add( MyStringRequest );
    }

    class Luogo {
        String cod;
        String indirizzo;
        boolean locationSyc;
        double lat;
        double lng;

        public Luogo(String cod , String indirizzo , double lat , double lng, boolean locationSync) {
            this.cod = cod;
            this.indirizzo = indirizzo;
            this.lat = lat;
            this.lng = lng;
            this.locationSyc=locationSync;
        }
    }
}
