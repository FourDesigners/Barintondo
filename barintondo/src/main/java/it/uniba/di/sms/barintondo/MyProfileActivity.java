package it.uniba.di.sms.barintondo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MyProfileActivity extends AppCompatActivity {

    public static final String DB_NAME = "datiUtente.db";
    public static final String TABLE_UTENTE = "utenti";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    private Toolbar myToolbar;
    private EditText username;
    private EditText email;
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

        //popolamento db
        /*ProfileOpenHelper dbHelper = new ProfileOpenHelper(this, DB_NAME, null, 1);
        SQLiteDatabase myDB = dbHelper.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put(COLUMN_USERNAME, "prova1");
        user.put(COLUMN_EMAIL, "pinco@prova.it");
        user.put(COLUMN_PASSWORD, "prova1");
        long newID = myDB.insert(TABLE_UTENTE, null, user);*/

        //prelevo dati dal db
        ProfileOpenHelper dbHelper = new ProfileOpenHelper(this, DB_NAME, null, 1);
        SQLiteDatabase myDB = dbHelper.getReadableDatabase();
        //definisco la query
        String[] columns = {COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_PASSWORD};
        Cursor myCursor;
        //ottengo il cursore
        myCursor = myDB.query(TABLE_UTENTE, columns, null, null, null, null, null, null);
        myCursor.moveToFirst();
        //popolo campi
        username = findViewById(R.id.myUsernameBox);
        username.setText(myCursor.getString(myCursor.getColumnIndex(COLUMN_USERNAME)));
        email = findViewById(R.id.myEmailBox);
        email.setText(myCursor.getString(myCursor.getColumnIndex(COLUMN_EMAIL)));
        password = findViewById(R.id.myPassBox);
        password.setText(myCursor.getString(myCursor.getColumnIndex(COLUMN_PASSWORD)));
        //chiudo db e cursore
        myCursor.close();
        myDB.close();

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
                Toast.makeText(this, getResources().getString(R.string.saveBtnMessage), Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void saveEdits() {
        String newUser = username.getText().toString();
        String newEmail = email.getText().toString();
        String newPass = password.getText().toString();

        ProfileOpenHelper dbHelper = new ProfileOpenHelper(this, DB_NAME, null, 1);
        SQLiteDatabase myDB = dbHelper.getWritableDatabase();

        ContentValues newValues= new ContentValues();
        newValues.put(COLUMN_USERNAME, newUser);
        newValues.put(COLUMN_EMAIL, newEmail);
        newValues.put(COLUMN_PASSWORD, newPass);
        myDB.update(TABLE_UTENTE, newValues,null, null);
        myDB.close();
    }
}
