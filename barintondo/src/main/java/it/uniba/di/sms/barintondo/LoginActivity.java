package it.uniba.di.sms.barintondo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.InternetConnection;
import it.uniba.di.sms.barintondo.utils.ProfileOpenHelper;


public class LoginActivity extends AppCompatActivity implements Constants{
    TextView textViewForgotPassword;
    EditText editTextEmail, editTextPassword;
    ImageView imageView;
    Button reset, login;
    TextView register;
    ProfileOpenHelper openHelper;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openHelper = new ProfileOpenHelper(getApplicationContext(), Constants.DB_NAME, null, 1);
        goHomeIfAccount(openHelper);

        setContentView(R.layout.activity_login);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.str_login);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null

        if(!InternetConnection.isNetworkAvailable(LoginActivity.this)) {
            Toast.makeText(this, getResources().getString(R.string.notConnected), Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(this, "Connesso alla rete", Toast.LENGTH_SHORT).show();
            if(ProfileOpenHelper.getLocalAccount(openHelper) != null) {
                String nickname = ProfileOpenHelper.getLocalAccount(openHelper)[0];
                String email = ProfileOpenHelper.getLocalAccount(openHelper)[1];
                String password = ProfileOpenHelper.getLocalAccount(openHelper)[2];
                //registration(getApplicationContext(), nickname, email, password, openHelper);
            }
        }

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setInputType(InputType.TYPE_CLASS_TEXT);

        editTextPassword = findViewById(R.id.editTextPassword);

        imageView = findViewById(R.id.imageView);
        imageView.setTag(R.drawable.closedeye);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer integer = (Integer) imageView.getTag();
                integer = integer == null ? 0 : integer;

                switch(integer) {
                    case R.drawable.openeye:
                        imageView.setImageResource(R.drawable.closedeye);
                        imageView.setTag(R.drawable.closedeye);
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editTextPassword.setSelection(editTextPassword.getText().length());
                        break;
                    case R.drawable.closedeye:
                        imageView.setImageResource(R.drawable.openeye);
                        imageView.setTag(R.drawable.openeye);
                        editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editTextPassword.setSelection(editTextPassword.getText().length());
                        break;
                }
            }
        });

        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForgotPassword();
            }
        });

        reset = findViewById(R.id.buttonReset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextEmail.setText("");
                editTextPassword.setText("");
            }
        });

        login = findViewById(R.id.buttonLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                boolean connected = InternetConnection.isNetworkAvailable(LoginActivity.this);
                if(connected) {
                    login(email, password);
                }else {
                    if(ProfileOpenHelper.isPresent(email, password, openHelper)) {
                        goHome();
                    }else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalidCredentials), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        register = findViewById(R.id.textViewRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRegistration();
            }
        });
    }

    private void resetChips() {
        SharedPreferences list = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor edit = list.edit();
        edit.putString(ORDER, "0,1,2,3,4,5");
        edit.putInt(CHIESE, 0);
        edit.putInt(MONUMENTI, 0);
        edit.putInt(TEATRI, 0);
        edit.putInt(LIDI, 0);
        edit.putInt(DISCOTECHE, 0);
        edit.putInt(FAMIGLIA, 0);
        edit.putInt(MAX, 0);
        //edit.putBoolean(INSERTED, true);
        edit.apply();
    }

    private void goHomeIfAccount(ProfileOpenHelper openHelper) {
        if(ProfileOpenHelper.isPresent(openHelper)) {
            goHome();
        }else {
            resetChips();
        }
    }

    private void goForgotPassword() {

    }

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        finish();
        startActivity(intent);
    }

    private void goRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void login(final String email, final String password) {
        String Url = "http://barintondo.altervista.org/login.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                if(response.contains("login successfull")) {
                    getNickname(email, password);
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalidCredentials), Toast.LENGTH_SHORT).show();
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

    private void getNickname(final String email, final String password) {
        String Url = "http://barintondo.altervista.org/getNickname.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ProfileOpenHelper.insertInto(response, email, password, openHelper);
                Intent intent_name = new Intent();
                intent_name.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(intent_name);
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
