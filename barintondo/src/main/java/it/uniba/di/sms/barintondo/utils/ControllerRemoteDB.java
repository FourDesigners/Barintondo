package it.uniba.di.sms.barintondo.utils;

import android.app.ProgressDialog;
import android.content.Context;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.di.sms.barintondo.LuogoDetailActivity;
import it.uniba.di.sms.barintondo.LuogoReviewsFragment;
import it.uniba.di.sms.barintondo.R;

public class ControllerRemoteDB implements Constants {

    private String TAG_CLASS = getClass().getSimpleName();
    String email;
    Context context;

    public ControllerRemoteDB(Context context) {
        this.context = context;
        email = LocalDBOpenHelper.getEmail( context );
    }


    public void checkPref(String itemCod , MyListners.Interests listner) {
        manageInterests( REQUEST_CHECK_PREF , email , itemCod , listner );
    }

    public void addPref(String itemCod , MyListners.Interests listner) {
        manageInterests( REQUEST_ADD_PREF , email , itemCod , listner );
    }

    public void removePref(String itemCod , MyListners.Interests listner) {
        manageInterests( REQUEST_REMOVE_PREF , email , itemCod , listner );
    }

    private void manageInterests(final String requestOp , final String user , final String luogoCod , final MyListners.Interests listner) {
        // Log.i( TAG , getClass().getSimpleName() + ":entered manageInterests( )");
        final LuogoDetailActivity luogoDetailActivity = (LuogoDetailActivity) context;

        String Url = "http://barintondo.altervista.org/gestore_interessi.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                String[] result = response.split( "," );
                Log.i( TAG , "ControllerRemoteDB: entered onResponse(), request: " + result[0] + ", result: " + result[1] );

                switch (result[0]) {
                    case REQUEST_CHECK_PREF:
                        listner.onCheck( Boolean.valueOf( result[1] ) );
                        break;
                    case REQUEST_ADD_PREF:
                        boolean added = result[1].equals( REQUEST_RESULT_OK );
                        listner.onAdd( added );
                        break;
                    case REQUEST_REMOVE_PREF:
                        boolean removed = result[1].equals( REQUEST_RESULT_OK );
                        listner.onRemove( removed );
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


    public void getAllInterests(final ArrayList<Luogo> interestsList , final MyListners.InterestsList interestsListner) {
        Log.i( TAG , TAG_CLASS + ": entered getAllInterests()" );
        interestsList.clear();
        String Url = "http://barintondo.altervista.org/gestore_interessi.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.i( "Test" ,  "ControllerRemoteDB: entered onResponse()"+response );
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                try {

                    JSONArray jsonArray = new JSONArray( response );

                    for (int i = 0; i < jsonArray.length(); i++) {


                        JSONObject jsonObject = jsonArray.getJSONObject( i );

                        Luogo luogo = new Luogo();
                        luogo.setCod( jsonObject.getString( "cod" ) );
                        luogo.setNome( jsonObject.getString( "nome" ) );
                        luogo.setCitta( jsonObject.getString( "citta" ) );
                        luogo.setCategoria( jsonObject.getString( "nomeCategoria" ) );
                        luogo.setSottoCat( jsonObject.getString( "sottoCategoria" ) );
                        if (jsonObject.getString( "oraA" ).equals( "null" )) {
                            luogo.setOraA( null );
                            luogo.setOraC( null );
                        } else {
                            luogo.setOraA( jsonObject.getString( "oraA" ) );
                            luogo.setOraC( jsonObject.getString( "oraC" ) );
                        }
                        luogo.setThumbnailLink( jsonObject.getString( "thumbnail" ) );
                        if (jsonObject.getString( "voto" ).equals( "null" )) {
                            luogo.setVoto( 0 );
                        } else luogo.setVoto( jsonObject.getInt( "voto" ) );
                        //Log.i( TAG , "Item" + i + ": " + item.toString() + " sottocat: " + item.getSottoCat() );

                        if (!jsonObject.getString( "codEvento" ).equals( "null" )) {
                            Evento evento = new Evento( luogo );

                            if (jsonObject.getString( "codLuogo" ).equals( "null" ))
                                evento.setCodLuogo( null );
                            else evento.setCodLuogo( jsonObject.getString( "codLuogo" ) );

                            if (jsonObject.getString( "dataInizio" ).equals( "null" )) {
                                evento.setDataInizio( null );
                                evento.setDataFine( null );
                            } else {
                                evento.setDataInizio( jsonObject.getString( "dataInizio" ) );
                                evento.setDataFine( jsonObject.getString( "dataFine" ) );
                            }
                            //adding items to itemsList
                            interestsList.add( evento );
                        } else interestsList.add( luogo );
                    }
                    interestsListner.onInterestsList();

                } catch (JSONException e2) {
                    e2.printStackTrace();
                    interestsListner.onError( VOLLEY_ERROR_JSON );
                    Log.i( TAG , TAG_CLASS + ": entered getAllInterests(), error in pharsing Json" );
                }


            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                interestsListner.onError( VOLLEY_ERROR_CONNECTION );
                Log.i( TAG , TAG_CLASS + ": entered getAllInterests(), error on server" );
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

    public void populateInterestsCod() {
        Log.i( TAG , TAG_CLASS + ": entered getInterestsCod()" );
        UserUtils.codPref.clear();

        String Url = "http://barintondo.altervista.org/gestore_interessi.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.i( TAG ,  "ControllerRemoteDB: entered onResponse()"+response );
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                try {

                    JSONArray jsonArray = new JSONArray( response );

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject( i );
                        UserUtils.codPref.add( jsonObject.getString( "luogo" ) );
                    }
                } catch (JSONException e2) {
                    e2.printStackTrace();
                    Log.i( TAG , TAG_CLASS + ": entered getInterestsCod(), error in pharsing Json" );
                }
            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Log.i( TAG , TAG_CLASS + ": entered getInterestsCod(), error on server" );
            }
        } ) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , REQUEST_GET_PREF_COD );
                MyData.put( "email" , email );
                return MyData;
            }
        };


        MyRequestQueue.add( MyStringRequest );
    }

    public void getLuoghiList(final String requestCat , final List<Luogo> luogoList , final MyListners.LuoghiList mListner) {
        Log.i( TAG , TAG_CLASS + ": entered getLuoghiList()" );
        String Url = "http://barintondo.altervista.org/get_luoghi.php";
        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Log.i( TAG, TAG_CLASS + ": entered onResponse() "+response );
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.

                try {

                    JSONArray jsonArray = new JSONArray( response );

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject( i );

                        Luogo luogo = new Luogo();
                        luogo.setCod( jsonObject.getString( "cod" ) );
                        luogo.setNome( jsonObject.getString( "nome" ) );
                        luogo.setCitta( jsonObject.getString( "citta" ) );
                        luogo.setCategoria( jsonObject.getString( "nomeCategoria" ) );
                        luogo.setSottoCat( jsonObject.getString( "sottoCategoria" ) );
                        if (!jsonObject.getString( "latitudine" ).equals( "null" )) {
                            luogo.setLatitudine( Float.valueOf( jsonObject.getString( "latitudine" ) ) );
                            luogo.setLongitudine( Float.valueOf( jsonObject.getString( "longitudine" ) ) );
                        }
                        if (jsonObject.getString( "oraA" ).equals( "null" )) {
                            luogo.setOraA( null );
                            luogo.setOraC( null );
                        } else {
                            luogo.setOraA( jsonObject.getString( "oraA" ) );
                            luogo.setOraC( jsonObject.getString( "oraC" ) );
                        }

                        luogo.setThumbnailLink( jsonObject.getString( "thumbnail" ) );
                        luogo.setIndirizzo( jsonObject.getString( "indirizzo" ) );

                        if (!jsonObject.getString( "voto" ).equals( "null" )) {
                            luogo.setVoto( jsonObject.getInt( "voto" ) );
                            int order = jsonObject.getInt( "voto" );
                            if (UserUtils.codPref.contains( luogo.getCod() )) order = order + 10;
                            luogo.setOrder( order );
                        }

                        //se la richiesta è per un evento, ne crea uno e aggiunge le informazioni in più e lo aggiunge alla lista
                        if (requestCat.equals( REQUEST_GET_EVENTS )) {
                            Evento evento = new Evento( luogo );

                            if (jsonObject.getString( "codLuogo" ).equals( "null" ))
                                evento.setCodLuogo( null );
                            else evento.setCodLuogo( jsonObject.getString( "codLuogo" ) );

                            if (jsonObject.getString( "dataInizio" ).equals( "null" )) {
                                evento.setDataInizio( null );
                                evento.setDataFine( null );
                            } else {
                                evento.setDataInizio( jsonObject.getString( "dataInizio" ) );
                                evento.setDataFine( jsonObject.getString( "dataFine" ) );
                            }
                            //adding items to itemsList
                            luogoList.add( evento );
                        } else luogoList.add( luogo );

                    }

                } catch (JSONException e2) {
                    e2.printStackTrace();
                    mListner.onError( VOLLEY_ERROR_JSON );
                    Log.i( TAG , TAG_CLASS + ": entered getLuoghiList(), error pharsing Json" );
                }
                Collections.sort( luogoList );
                mListner.onList();

            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                mListner.onError( VOLLEY_ERROR_CONNECTION );
                Log.i( TAG , TAG_CLASS + ": entered getLuoghiList(), error on server" );
            }
        } ) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , REQUEST_GET_CATEGORY );
                MyData.put( "request_cat" , requestCat );
                return MyData;
            }
        };
        MyRequestQueue.add( MyStringRequest );
    }


    public void getLuogo(final String codLuogo , final String requestLuogoType , final MyListners.SingleLuogo listner) {
        final ProgressDialog progressDialog = new ProgressDialog( context );
        progressDialog.setMessage( context.getResources().getString( R.string.loadingMessage ) );
        progressDialog.show();


        String Url = "http://barintondo.altervista.org/get_luoghi.php";
        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Log.i( "Test" , requestLuogoType+"ControllerRemoteDB: entered onResponse()"+response );
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Luogo luogo = new Luogo();
                Evento evento = new Evento();
                try {

                    JSONArray jsonArray = new JSONArray( response );

                    if (jsonArray.length() == 0) {
                        Toast.makeText( context , context.getResources().getString( R.string.str_fail_get_luoghi ) , Toast.LENGTH_LONG ).show();
                        progressDialog.dismiss();
                        return;
                    }

                    if (jsonArray.length() == 1) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject( 0 );


                            luogo.setCod( jsonObject.getString( "cod" ) );
                            luogo.setNome( jsonObject.getString( "nome" ) );
                            luogo.setSottoCat( jsonObject.getString( "sottoCategoria" ) );
                            if (jsonObject.getString( "oraA" ).equals( "null" )) {
                                luogo.setOraA( null );
                                luogo.setOraC( null );
                            } else {
                                luogo.setOraA( jsonObject.getString( "oraA" ) );
                                luogo.setOraC( jsonObject.getString( "oraC" ) );
                            }
                            if (!jsonObject.getString( "latitudine" ).equals( "null" )) {
                                luogo.setLatitudine( Float.valueOf( jsonObject.getString( "latitudine" ) ) );
                                luogo.setLongitudine( Float.valueOf( jsonObject.getString( "longitudine" ) ) );
                            }
                            luogo.setThumbnailLink( jsonObject.getString( "thumbnail" ) );
                            luogo.setDescrizione_en( jsonObject.getString( "descrizione_en" ) );
                            luogo.setDescrizione_it( jsonObject.getString( "descrizione_it" ) );
                            luogo.setIndirizzo( jsonObject.getString( "indirizzo" ) );
                            if (!jsonObject.getString( "voto" ).equals( "null" )) {
                                luogo.setVoto( jsonObject.getInt( "voto" ) );
                            }

                            if (requestLuogoType.equals( REQUEST_GET_EVENTS )) {
                                evento = new Evento( luogo );

                                if (jsonObject.getString( "codLuogo" ).equals( "null" ))
                                    evento.setCodLuogo( null );
                                else evento.setCodLuogo( jsonObject.getString( "codLuogo" ) );

                                if (jsonObject.getString( "dataInizio" ).equals( "null" )) {
                                    evento.setDataInizio( null );
                                    evento.setDataFine( null );
                                } else {
                                    evento.setDataInizio( jsonObject.getString( "dataInizio" ) );
                                    evento.setDataFine( jsonObject.getString( "dataFine" ) );
                                }

                                listner.onEvento( evento );
                            } else listner.onLuogo( luogo );

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText( context , context.getResources().getString( R.string.str_fail_get_luoghi ) , Toast.LENGTH_SHORT ).show();
                        }
                    }

                } catch (JSONException e2) {
                    e2.printStackTrace();
                    Toast.makeText( context , context.getResources().getString( R.string.str_fail_get_luoghi ) , Toast.LENGTH_SHORT ).show();
                }


                progressDialog.dismiss();

            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //This code is executed if there is an error.
                Toast.makeText( context , context.getResources().getString( R.string.str_fail_get_luoghi ) , Toast.LENGTH_SHORT ).show();
            }
        } ) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , REQUEST_GET_LUOGO );
                MyData.put( "request_luogo" , codLuogo );
                MyData.put( "request_luogo_type" , requestLuogoType );
                return MyData;
            }
        };


        MyRequestQueue.add( MyStringRequest );
    }

    public void getLuoghiNear(final Luogo myLuogo , final List<Luogo> luogoList , final MyListners.LuoghiList mListner) {
        final ProgressDialog progressDialog = new ProgressDialog( context );
        progressDialog.setMessage( context.getResources().getString( R.string.loadingMessage ) );
        progressDialog.show();

        String Url = "http://barintondo.altervista.org/get_luoghi_near.php";
        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Log.i( "Test" , "getLuoghiNear onResponse() "+response );
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
                            luogo.setCategoria( jsonObject.getString( "nomeCategoria" ) );
                            luogo.setSottoCat( jsonObject.getString( "sottoCategoria" ) );
                            luogo.setCitta( jsonObject.getString( "citta" ) );
                            if (jsonObject.getString( "oraA" ).equals( "null" )) {
                                luogo.setOraA( null );
                                luogo.setOraC( null );
                            } else {
                                luogo.setOraA( jsonObject.getString( "oraA" ) );
                                luogo.setOraC( jsonObject.getString( "oraC" ) );
                            }
                            if (!jsonObject.getString( "latitudine" ).equals( "null" )) {
                                luogo.setLatitudine( Float.valueOf( jsonObject.getString( "latitudine" ) ) );
                                luogo.setLongitudine( Float.valueOf( jsonObject.getString( "longitudine" ) ) );
                            }
                            luogo.setThumbnailLink( jsonObject.getString( "thumbnail" ) );
                            luogo.setDescrizione_en( jsonObject.getString( "descrizione_en" ) );
                            luogo.setDescrizione_it( jsonObject.getString( "descrizione_it" ) );
                            luogo.setIndirizzo( jsonObject.getString( "indirizzo" ) );
                            if (!jsonObject.getString( "voto" ).equals( "null" )) {
                                luogo.setVoto( jsonObject.getInt( "voto" ) );
                            }

                            luogo.setDistance( luogo.calculateDistanceTo( myLuogo ) );
                            luogoList.add( luogo );

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText( context , context.getResources().getString( R.string.str_fail_get_luoghi ) , Toast.LENGTH_SHORT ).show();
                        }
                    }

                } catch (JSONException e2) {
                    e2.printStackTrace();
                    Toast.makeText( context , context.getResources().getString( R.string.str_fail_get_luoghi ) , Toast.LENGTH_SHORT ).show();
                }
                mListner.onList();
                progressDialog.dismiss();

            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //This code is executed if there is an error.
                Toast.makeText( context , context.getResources().getString( R.string.str_fail_get_luoghi ) , Toast.LENGTH_SHORT ).show();
            }
        } ) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "latitude" , String.valueOf( myLuogo.getLatitudine() ) );
                MyData.put( "longitude" , String.valueOf( myLuogo.getLongitudine() ) );
                MyData.put( "luogo_cod" , myLuogo.getCod() );
                return MyData;
            }
        };


        MyRequestQueue.add( MyStringRequest );
    }

    public void getCouponList(final List<Coupon> couponList , final MyListners.CouponList couponListListner) {
        //couponList.clear();


        if (InternetConnection.isNetworkAvailable( context )) {
            //prelievo informazioni aggiornate dal server e aggiornamento DB locale

            final String email = LocalDBOpenHelper.getEmail( context );

            //definizione URL
            String Url = "http://barintondo.altervista.org/get_my_coupons.php";

            RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
            StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.i( TAG ,  "VolleyGetCoupon: entered onResponse()"+response );
                    //This code is executed if the server responds, whether or not the response contains data.
                    //The String 'response' contains the server's response.
                    try {

                        List<Coupon> tempCouponList = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray( response );

                        for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject( i );

                                Coupon coupon = new Coupon();
                                coupon.setCod( jsonObject.getString( "codCoupon" ) );
                                coupon.setCodLuogo( jsonObject.getString( "cod" ) );
                                coupon.setLuogo( jsonObject.getString( "nome" ) );
                                coupon.setScadenza( jsonObject.getString( "scadenza" ) );
                                coupon.setSottoCat( jsonObject.getString( "sottoCategoria" ) );
                                coupon.setDescrizione_it( jsonObject.getString( "descrizioneIt" ) );
                                coupon.setDescrizione_en( jsonObject.getString( "descrizioneEn" ) );

                                //adding items to itemsList
                                tempCouponList.add( coupon );


                        }
                        LocalDBOpenHelper couponOpenHelper = new LocalDBOpenHelper( context , Constants.DB_NAME , null , 1 );
                        LocalDBOpenHelper.deleteCoupon( couponOpenHelper );
                        for (Coupon c : tempCouponList) {
                            LocalDBOpenHelper.insertCoupon( c , couponOpenHelper );
                        }
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                        Log.i( TAG , TAG_CLASS + ": entered getCouponList(), error pharsing Json" );
                        couponListListner.onError( VOLLEY_ERROR_JSON );
                    }
                    couponList.clear();

                    couponListListner.onCouponList();
                }
            } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    //This code is executed if there is an error.
                    Log.i( TAG , TAG_CLASS + ": entered getLuoghiList(), error on server" );
                    couponListListner.onError( VOLLEY_ERROR_CONNECTION );
                }
            } ) {

                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put( "email" , email );
                    return MyData;
                }
            };

            MyRequestQueue.add( MyStringRequest );
        } else {
            Toast.makeText( context , context.getResources().getString( R.string.offlineCoupons ) , Toast.LENGTH_SHORT ).show();
            couponList.clear();
            //anche se non sono stati caricati i coupon dal server da il callback per caricare quelli dal db locale
            couponListListner.onCouponList();
        }

    }

    public void getReviewsList(final String codLuogo , final List<Review> reviewsList , final ReviewAdapter mAdapter) {
        final ProgressDialog progressDialog = new ProgressDialog( context );
        progressDialog.setMessage( context.getResources().getString( R.string.loadingMessage ) );
        progressDialog.show();

        reviewsList.clear();

        String Url = "http://barintondo.altervista.org/manager_review.php";
        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.i( TAG ,  "ControllerRemoteDB getReviewsList: entered onResponse()"+response);
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.

                try {
                    JSONArray jsonArray = new JSONArray( response );

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject( i );

                            String userName = jsonObject.getString( "nickname" );
                            String textReview = jsonObject.getString( "commento" );
                            int vote = jsonObject.getInt( "voto" );
                            String date = jsonObject.getString( "data" );
                            Review review = new Review( userName , textReview , vote , date );
                            //Log.i(TAG, "TEST: "+review.getUserName()+", "+review.getDate()+", "+review.getReviewText()+", "+review.getVote());
                            //adding items to itemsList
                            reviewsList.add( review );
                            Log.i( TAG , "Test" );

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i( TAG , "ControllerRemoteDB getReviewsList: entered first catch" );
                            Toast.makeText( context , context.getResources().getString( R.string.str_fail_get_review ) , Toast.LENGTH_SHORT ).show();
                        }
                    }

                } catch (JSONException e2) {
                    e2.printStackTrace();
                    Log.i( TAG , "ControllerRemoteDB getReviewsList: entered second catch" );
                    Toast.makeText( context , context.getResources().getString( R.string.str_fail_get_review ) , Toast.LENGTH_SHORT ).show();
                }

                mAdapter.notifyDataSetChanged();
                mAdapter.onReviewLoaded( reviewsList.size() );
                progressDialog.dismiss();


            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //This code is executed if there is an error.
                Log.i( TAG , "ControllerRemoteDB getReviewsList: entered onErrorResponse()" );
                Toast.makeText( context , context.getResources().getString( R.string.str_fail_get_review ) , Toast.LENGTH_SHORT ).show();
            }
        } ) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , REQUEST_GET_REVIEWS );
                MyData.put( "cod_luogo" , codLuogo );
                return MyData;
            }
        };


        MyRequestQueue.add( MyStringRequest );
    }

    public void saveReview(final String reviewText , final String codLuogo , final int voto , final LuogoReviewsFragment myReviewFragment) {
        // Log.i( TAG , getClass().getSimpleName() + ":entered manageInterests( )");

        String Url = "http://barintondo.altervista.org/manager_review.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue( context );
        StringRequest MyStringRequest = new StringRequest( Request.Method.POST , Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Log.i( "Review" , getClass().getSimpleName() + ":entered saveReview( )"+response);

                if (response.equals( REQUEST_RESULT_OK )) {
                    myReviewFragment.onSaveReviewResult();
                } else
                    Toast.makeText( context , context.getResources().getString( R.string.str_fail_save_review ) , Toast.LENGTH_SHORT ).show();

            }
        } , new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText( context , context.getResources().getString( R.string.str_fail_save_review ) , Toast.LENGTH_SHORT ).show();
            }
        } ) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put( "request_op" , REQUEST_SAVE_REVIEW );
                MyData.put( "email" , email );
                MyData.put( "cod_luogo" , codLuogo );
                MyData.put( "review_text" , reviewText );
                MyData.put( "voto" , String.valueOf( voto ) );
                return MyData;
            }
        };
        MyRequestQueue.add( MyStringRequest );
    }
}