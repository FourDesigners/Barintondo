package it.uniba.di.sms.barintondo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ProfileOpenHelper;
import it.uniba.di.sms.barintondo.utils.VerifyString;

import static java.security.AccessController.getContext;

public class RegistrationActivity extends AppCompatActivity {
    TextView textViewNicknameError, textViewEmailError, textViewPasswordError, textViewRepeatPasswordError;
    EditText editTextNickname, editTextEmail, editTextPassword, editTextRepeatPassword;
    ImageView imageView, imageView2;
    Button reset, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        textViewNicknameError = findViewById(R.id.textViewNicknameError);
        textViewEmailError = findViewById(R.id.textViewEmailError);
        textViewPasswordError = findViewById(R.id.textViewPasswordError);
        textViewRepeatPasswordError = findViewById(R.id.textViewRepeatPasswordtError);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.str_register);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);

        /////

        ProfileOpenHelper dbHelper = new ProfileOpenHelper(this, Constants.DB_NAME, null, 1);
        SQLiteDatabase myDB = dbHelper.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put(Constants.COLUMN_USERNAME, "prova2");
        user.put(Constants.COLUMN_EMAIL, "pinco@prova.it");
        user.put(Constants.COLUMN_PASSWORD, "prova1");
        long newID = myDB.insert(Constants.TABLE_UTENTE, null, user);

        /////


        editTextNickname = findViewById(R.id.editTextNickname);

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

                //Toast.makeText(getApplicationContext(), integer.toString(), Toast.LENGTH_SHORT).show();
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

        editTextRepeatPassword = findViewById(R.id.editTextRepeatPassword);
        imageView2 = findViewById(R.id.imageView2);
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
                        editTextRepeatPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editTextRepeatPassword.setSelection(editTextRepeatPassword.getText().length());
                        break;
                    case R.drawable.closedeye:
                        imageView2.setImageResource(R.drawable.openeye);
                        imageView2.setTag(R.drawable.openeye);
                        editTextRepeatPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editTextRepeatPassword.setSelection(editTextRepeatPassword.getText().length());
                        break;
                }
            }
        });

        reset = findViewById(R.id.buttonReset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNickname.setText("");
                editTextEmail.setText("");
                editTextPassword.setText("");
                editTextRepeatPassword.setText("");
            }
        });

        register = findViewById(R.id.buttonRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = editTextNickname.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String repeatPassword = editTextRepeatPassword.getText().toString();
                boolean correct = isCorrect(nickname, email, password, repeatPassword);
                if(!isPresent(email)) {
                    insertInto(nickname, email, password);
                }
            }
        });

    }

    private boolean isPresent(String email) {

        return false;
    }

    private void insertInto(String nickname, String email, String password) {
        /*
        ProfileOpenHelper openHelper = new ProfileOpenHelper(getApplicationContext(), Constants.DB_NAME, null, 1);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_NICKNAME, nickname);
        values.put(Constants.COLUMN_EMAIL, email);
        values.put(Constants.COLUMN_PASSWORD, password);
        db.insert(Constants.TABLE_UTENTE, null, values);
        db.close();
        */
        ProfileOpenHelper dbHelper = new ProfileOpenHelper(this, Constants.DB_NAME, null, 1);
        SQLiteDatabase myDB = dbHelper.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put(Constants.COLUMN_USERNAME, "prova2");
        user.put(Constants.COLUMN_EMAIL, "pinco@prova.it");
        user.put(Constants.COLUMN_PASSWORD, "prova1");
        long newID = myDB.insert(Constants.TABLE_UTENTE, null, user);
        Toast.makeText(getApplicationContext(), "Account creato", Toast.LENGTH_SHORT).show();
    }

    private boolean isCorrect(String nickname, String email, String password, String repeatPassword) {
        boolean correct = true;

        if(VerifyString.isNicknameNotValid(nickname)) {
            textViewNicknameError.setText(R.string.str_nickname_is_empty);
            correct = false;
        }else {
            textViewNicknameError.setText("");
        }
        if(VerifyString.isEmailNotValid(email)) {
            textViewEmailError.setText(R.string.str_email_not_valid);
            correct = false;
        }else {
            textViewEmailError.setText("");
        }
        if(VerifyString.isPasswordNotValid(password)) {
            textViewPasswordError.setText(R.string.str_weak_password_min_8_characters_required);
            correct = false;
        }else {
            textViewPasswordError.setText("");
        }
        if(VerifyString.notMatchingPasswords(password, repeatPassword)) {
            textViewRepeatPasswordError.setText(R.string.str_passwords_don_t_match);
            correct = false;
        }else {
            textViewRepeatPasswordError.setText("");
        }
        return correct;
    }
}
