package it.uniba.di.sms.barintondo.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.barintondo.HomeActivity;

public class VolleyAccess {
    public static void registration(final Context context, final String nickname, final String email, final String password, final ProfileOpenHelper openHelper) {
        String Url = "http://barintondo.altervista.org/registration.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Registration successfull")) {
                    if(!ProfileOpenHelper.isPresent(email, openHelper)) {
                        ProfileOpenHelper.insertInto(nickname, email, password, openHelper);
                    }
                    //Toast.makeText(context, "Account creato", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                }else {
                    Toast.makeText(context, "Account già presente", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("nickname", nickname);
                MyData.put("user", email);
                MyData.put("pass", password);
                return MyData;
            }
        };


        MyRequestQueue.add(MyStringRequest);
    }

    public static void login(final Context context, final String email, final String password, final ProfileOpenHelper openHelper) {
        String Url = "http://barintondo.altervista.org/login.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                if(response.contains("login successfull")) {
                    getNickname(context, email, password, openHelper);
                }else {
                    Toast.makeText(context, "Credenziali non valide", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("user", email);
                MyData.put("pass", password);
                return MyData;
            }
        };


        MyRequestQueue.add(MyStringRequest);
    }

    public static void getNickname(final Context context, final String email, final String password, final ProfileOpenHelper openHelper) {
        String Url = "http://barintondo.altervista.org/getNickname.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ProfileOpenHelper.insertInto(response, email, password, openHelper);
                Intent intent_name = new Intent();
                intent_name.setClass(context, HomeActivity.class);
                context.startActivity(intent_name);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("email", email);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }
}
