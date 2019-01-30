package it.uniba.di.sms.barintondo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import it.uniba.di.sms.barintondo.utils.LocalDBOpenHelper;

public class MyProfileActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private EditText username;
    private TextView textViewEmail;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        myToolbar = findViewById(R.id.myProfileToolbar);
        myToolbar.setTitle(R.string.myProfile_header);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);

        //prelevo dati dal db
        LocalDBOpenHelper dbHelper = new LocalDBOpenHelper(this, Constants.DB_NAME, null, 1);
        SQLiteDatabase myDB = dbHelper.getReadableDatabase();
        //definisco la query
        String[] columns = {Constants.COLUMN_NICKNAME, Constants.COLUMN_EMAIL, Constants.COLUMN_PASSWORD};
        Cursor myCursor;
        //ottengo il cursore
        myCursor = myDB.query(Constants.TABLE_UTENTE, columns, null, null, null, null, null, null);
        myCursor.moveToFirst();
        //popolo campi
        username = findViewById(R.id.myUsernameBox);
        username.setText(myCursor.getString(myCursor.getColumnIndex(Constants.COLUMN_NICKNAME)));
        textViewEmail = findViewById(R.id.myEmailBox);
        textViewEmail.setText(myCursor.getString(myCursor.getColumnIndex(Constants.COLUMN_EMAIL)));
        password = findViewById(R.id.myPassBox);
        password.setText(myCursor.getString(myCursor.getColumnIndex(Constants.COLUMN_PASSWORD)));
        //chiudo db e cursore
        myCursor.close();
        myDB.close();

        final ImageView imageView2 = findViewById(R.id.imageViewProfile);
        imageView2.setTag(R.drawable.closedeye);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer integer = (Integer) imageView2.getTag();
                integer = integer == null ? 0 : integer;

                //Toast.makeText(getApplicationContext(), integer.toString(), Toast.LENGTH_SHORT).show();
                switch(integer) {
                    case R.drawable.openeye:
                        imageView2.setImageResource(R.drawable.closedeye);
                        imageView2.setTag(R.drawable.closedeye);
                        password.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.setSelection(password.getText().length());
                        break;
                    case R.drawable.closedeye:
                        imageView2.setImageResource(R.drawable.openeye);
                        imageView2.setTag(R.drawable.openeye);
                        password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        password.setSelection(password.getText().length());
                        break;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_profile_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: onBackPressed();
                return true;
            case R.id.saveButton:
                saveEdits();
                //Toast.makeText(this, getResources().getString(R.string.saveBtnMessage), Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void saveEdits() {
        if(InternetConnection.isNetworkAvailable(getApplicationContext())) {
            TextView textViewError = findViewById(R.id.textViewPasswordError);
            String newUser = username.getText().toString();
            String email = textViewEmail.getText().toString();
            String newPass = password.getText().toString();
            if(newPass.length() < 8) {
                textViewError.setText(getResources().getString(R.string.str_weak_password_min_8_characters_required));
            }else {
                textViewError.setText("");
                LocalDBOpenHelper dbHelper = new LocalDBOpenHelper(this, Constants.DB_NAME, null, 1);
                SQLiteDatabase myDB = dbHelper.getWritableDatabase();
                ContentValues newValues = new ContentValues();
                newValues.put(Constants.COLUMN_NICKNAME, newUser);
                newValues.put(Constants.COLUMN_EMAIL, email);
                newValues.put(Constants.COLUMN_PASSWORD, newPass);
                myDB.update(Constants.TABLE_UTENTE, newValues,null, null);
                myDB.close();
                update(newUser, email, newPass);
            }
        }else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.str_error_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void update(final String nickname, final String email, final String password) {
        String Url = "http://barintondo.altervista.org/update.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.strModified), Toast.LENGTH_SHORT).show();
                goHome();
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

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
