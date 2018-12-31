package it.uniba.di.sms.barintondo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ProfileOpenHelper;

public class LoginActivity extends AppCompatActivity {
    TextView textViewForgotPassword;
    EditText editTextEmail, editTextPassword;
    ImageView imageView;
    Button reset, login;
    TextView register;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ProfileOpenHelper openHelper = new ProfileOpenHelper(getApplicationContext(), Constants.DB_NAME, null, 1);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(R.string.str_login);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null

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
                if(ProfileOpenHelper.isPresent(email, password, openHelper)) {
                    goHome();
                }else {
                    Toast.makeText(getApplicationContext(), "Credenziali errate", Toast.LENGTH_SHORT).show();
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

    private void goForgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void goRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}
