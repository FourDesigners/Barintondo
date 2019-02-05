package it.uniba.di.sms.barintondo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.InternetConnection;
import it.uniba.di.sms.barintondo.utils.LocalDBOpenHelper;
import it.uniba.di.sms.barintondo.utils.VerifyString;


public class LoginActivity extends AppCompatActivity implements Constants{
    TextView textViewForgotPassword;
    EditText editTextEmail, editTextPassword;
    //ImageView imageView;
    Button reset, login;
    TextView register;
    LocalDBOpenHelper openHelper;
    int animationCounter = 1;

    @SuppressLint({"NewApi", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openHelper = new LocalDBOpenHelper(getApplicationContext(), Constants.DB_NAME, null, 1);
        goHomeIfAccount(openHelper);

        setContentView(R.layout.activity_login);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setLogo(R.mipmap.ic_launcher);
        myToolbar.setTitle(R.string.app_name);
        ActionBar actionbar = getSupportActionBar();
        setSupportActionBar(myToolbar);
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null

        if(!InternetConnection.isNetworkAvailable(LoginActivity.this)) {
            Toast.makeText(this, getResources().getString(R.string.notConnected), Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(this, "Connesso alla rete", Toast.LENGTH_SHORT).show();
            if(LocalDBOpenHelper.getLocalAccount(openHelper) != null) {
                String nickname = LocalDBOpenHelper.getLocalAccount(openHelper)[0];
                String email = LocalDBOpenHelper.getLocalAccount(openHelper)[1];
                String password = LocalDBOpenHelper.getLocalAccount(openHelper)[2];
                //registration(getApplicationContext(), nickname, email, password, openHelper);
            }
        }

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setInputType(InputType.TYPE_CLASS_TEXT);

        editTextPassword = findViewById(R.id.editTextPassword);

        final Animation in  = AnimationUtils.loadAnimation(this, R.anim.left_to_right_in);
        final Animation out = AnimationUtils.loadAnimation(this, R.anim.left_to_right_out);
        final ImageSwitcher imageSwitcher;
        imageSwitcher = findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView view = new ImageView(getApplicationContext());
                return view;
            }
        });
        imageSwitcher.setImageResource(R.drawable.closedeye);
        imageSwitcher.setTag(R.drawable.closedeye);
        imageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer integer = (Integer) imageSwitcher.getTag();
                integer = integer == null ? 0 : integer;
                imageSwitcher.setInAnimation(in);
                imageSwitcher.setOutAnimation(out);
                switch(integer) {
                    case R.drawable.openeye:
                        imageSwitcher.setImageResource(R.drawable.closedeye);
                        imageSwitcher.setTag(R.drawable.closedeye);
                        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editTextPassword.setSelection(editTextPassword.getText().length());
                        break;
                    case R.drawable.closedeye:
                        imageSwitcher.setImageResource(R.drawable.openeye);
                        imageSwitcher.setTag(R.drawable.openeye);
                        editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editTextPassword.setSelection(editTextPassword.getText().length());
                        break;
                }
            }
        });


        /*
        final Handler imageSwitcherHandler;
        imageSwitcherHandler = new Handler(Looper.getMainLooper());
        imageSwitcherHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (animationCounter) {
                    case 0:
                        imageSwitcher.setImageResource(R.drawable.closedeye);
                        break;
                    case 1:
                        imageSwitcher.setImageResource(R.drawable.openeye);
                        break;
                }
                animationCounter++;
                animationCounter %= 2;
                imageSwitcherHandler.postDelayed(this, 3000);
            }
        });
        */

        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(InternetConnection.isNetworkAvailable(getApplicationContext())) {
                    showDialog();
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_error_not_connected), Toast.LENGTH_SHORT).show();
                }
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
                    if(LocalDBOpenHelper.isPresent(email, password, openHelper)) {
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
        edit.putInt(MAX, 3);
        edit.putBoolean(SKIP, false);
        edit.apply();
    }

    private void goHomeIfAccount(LocalDBOpenHelper openHelper) {
        if(LocalDBOpenHelper.isPresent(openHelper)) {
            goHome();
        }else {
            resetChips();
        }
    }

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.strInsert))
                .setTitle(getResources().getString(R.string.strForgotPassword))
                .setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        builder.setView(view);

        final EditText editEmail = view.findViewById(R.id.editEmailForgot);

        AlertDialog dialog = builder.create();
        builder.setPositiveButton(getResources().getString(R.string.strConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String email = editEmail.getText().toString();
                if(isCorrect(email)) {
                    resetPassword(email);
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.strCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void resetPassword(final String email) {
        String Url = "http://barintondo.altervista.org/recuperaPassword.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("ok")) {
                    showNotification();
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.strNoAccount), Toast.LENGTH_SHORT).show();
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
                return MyData;
            }
        };


        MyRequestQueue.add(MyStringRequest);
    }

    private void showNotification() {
        final Intent intent = new Intent(this, LoginActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_profile)
                .setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.strInfo))
                .build();
        notification.defaults = notification.defaults | Notification.DEFAULT_SOUND;
        notificationManager.notify(0, notification);
    }

    private boolean isCorrect(String email) {
        boolean correct = true;

        if(VerifyString.isEmailNotValid(email)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_email_is_not_valid), Toast.LENGTH_SHORT).show();
            correct = false;
        }

        return correct;
    }


    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,  R.anim.slide_out);
        finish();
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
                    getNickname(email, password, 1);
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

    private void getNickname(final String email, final String password, final int flag) {
        String Url = "http://barintondo.altervista.org/getNickname.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LocalDBOpenHelper.insertInto(response, email, password, openHelper);
                if(flag!=0) goHome();
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
