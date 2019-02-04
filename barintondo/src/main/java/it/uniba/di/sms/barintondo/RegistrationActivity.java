package it.uniba.di.sms.barintondo;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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


public class RegistrationActivity extends AppCompatActivity {
    TextView textViewNicknameError, textViewEmailError, textViewPasswordError, textViewRepeatPasswordError;
    EditText editTextNickname, editTextEmail, editTextPassword, editTextRepeatPassword;
    ImageSwitcher imageSwitcher, imageSwitcher2;
    Button reset, register;
    LocalDBOpenHelper openHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        openHelper = new LocalDBOpenHelper(getApplicationContext(), Constants.DB_NAME, null, 1);
        textViewNicknameError = findViewById(R.id.textViewNicknameError);
        textViewEmailError = findViewById(R.id.textViewEmailError);
        textViewPasswordError = findViewById(R.id.textViewPasswordError);
        textViewRepeatPasswordError = findViewById(R.id.textViewRepeatPasswordError);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setLogo(R.mipmap.ic_launcher);
        myToolbar.setTitle(R.string.str_register);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);

        editTextNickname = findViewById(R.id.editTextNickname);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setInputType(InputType.TYPE_CLASS_TEXT);

        editTextPassword = findViewById(R.id.editTextPassword);

        imageSwitcher = findViewById(R.id.imageSwitcher);
        final Animation in  = AnimationUtils.loadAnimation(this, R.anim.left_to_right_in);
        final Animation out = AnimationUtils.loadAnimation(this, R.anim.left_to_right_out);
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

        editTextRepeatPassword = findViewById(R.id.editTextRepeatPassword);
        imageSwitcher2 = findViewById(R.id.imageSwitcher2);
        imageSwitcher2.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView view = new ImageView(getApplicationContext());
                return view;
            }
        });
        imageSwitcher2.setImageResource(R.drawable.closedeye);
        imageSwitcher2.setTag(R.drawable.closedeye);
        imageSwitcher2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer integer = (Integer) imageSwitcher2.getTag();
                integer = integer == null ? 0 : integer;
                imageSwitcher2.setInAnimation(in);
                imageSwitcher2.setOutAnimation(out);
                switch(integer) {
                    case R.drawable.openeye:
                        imageSwitcher2.setImageResource(R.drawable.closedeye);
                        imageSwitcher2.setTag(R.drawable.closedeye);
                        editTextRepeatPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editTextRepeatPassword.setSelection(editTextRepeatPassword.getText().length());
                        break;
                    case R.drawable.closedeye:
                        imageSwitcher2.setImageResource(R.drawable.openeye);
                        imageSwitcher2.setTag(R.drawable.openeye);
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
                if(correct) {
                    boolean connected = InternetConnection.isNetworkAvailable(RegistrationActivity.this);
                    if(connected) {
                        registration(nickname, email, password);
                    }else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.notConnected), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void registration(final String nickname, final String email, final String password) {
        String Url = "http://barintondo.altervista.org/registration.php";

        RequestQueue MyRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("Registration successfull")) {
                    if(!LocalDBOpenHelper.isPresent(email, openHelper)) {
                        LocalDBOpenHelper.insertInto(nickname, email, password, openHelper);
                    }
                    //Toast.makeText(context, "Account creato", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.duplicateAccount), Toast.LENGTH_SHORT).show();
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
    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
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
            textViewEmailError.setText(R.string.str_email_is_not_valid);
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
