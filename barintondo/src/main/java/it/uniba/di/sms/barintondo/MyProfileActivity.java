package it.uniba.di.sms.barintondo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ProfileOpenHelper;

public class MyProfileActivity extends AppCompatActivity {

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

        //prelevo dati dal db
        ProfileOpenHelper dbHelper = new ProfileOpenHelper(this, Constants.DB_NAME, null, 1);
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
        email = findViewById(R.id.myEmailBox);
        email.setText(myCursor.getString(myCursor.getColumnIndex(Constants.COLUMN_EMAIL)));
        password = findViewById(R.id.myPassBox);
        password.setText(myCursor.getString(myCursor.getColumnIndex(Constants.COLUMN_PASSWORD)));
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

        ProfileOpenHelper dbHelper = new ProfileOpenHelper(this, Constants.DB_NAME, null, 1);
        SQLiteDatabase myDB = dbHelper.getWritableDatabase();

        ContentValues newValues= new ContentValues();
        newValues.put(Constants.COLUMN_NICKNAME, newUser);
        newValues.put(Constants.COLUMN_EMAIL, newEmail);
        newValues.put(Constants.COLUMN_PASSWORD, newPass);
        myDB.update(Constants.TABLE_UTENTE, newValues,null, null);
        myDB.close();
    }
}
