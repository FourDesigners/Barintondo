package it.uniba.di.sms.barintondo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kwabenaberko.openweathermaplib.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;

import java.util.Locale;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.InternetConnection;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;

public class HomeActivity extends AppCompatActivity implements Constants {

    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;
    OpenWeatherMapHelper helper;
    Button moreBtn, goAttractionBtn, goFoodBtn, goSleepBtn, goNearBariBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //openPreferences(); // Inserimento filtri

        myNavigationDrawer = new MyNavigationDrawer(this,
                (NavigationView) findViewById(R.id.nav_view),
                (DrawerLayout) findViewById(R.id.drawer_layout));
        myNavigationDrawer.build();

        myToolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_hamburger);

        moreBtn = findViewById(R.id.moreBtn);
        moreBtn.setOnClickListener(moreBtnListener);
        goAttractionBtn=findViewById( R.id.btnHomeGoAttraction );
        goAttractionBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeStartActivity(Constants.INTENT_ATTRACTIONS);
            }
        } );

        goFoodBtn=findViewById( R.id.btnHomeGoFood ) ;
        goFoodBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeStartActivity( Constants.INTENT_EATING );
            }
        } );
        goSleepBtn= findViewById( R.id.btnHomeGoSleep );
        goSleepBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeStartActivity( Constants.INTENT_SLEEPING );
            }
        } );
        goNearBariBtn=findViewById( R.id.btnHomeGoNearBari );
        goNearBariBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeStartActivity( Constants.INTENT_NEAR );
            }
        } );

    }

    private void homeStartActivity(String constCall){
        Intent intent = new Intent( this , LuogoListActivity.class );
        intent.putExtra( Constants.INTENT_ACTIVITY_ITEM_TYPE , constCall );
        startActivity( intent );
    }

    private void openPreferences() {
        SharedPreferences list = getSharedPreferences(PREFS_NAME, 0);
        if(!list.contains(INSERTED)) {
            SharedPreferences.Editor edit = list.edit();
            edit.putString(ORDER, "0,1,2,3,4,5");
            edit.putInt(CHIESE, 0);
            edit.putInt(MONUMENTI, 0);
            edit.putInt(TEATRI, 0);
            edit.putInt(LIDI, 0);
            edit.putInt(DISCOTECHE, 0);
            edit.putInt(FAMIGLIA, 0);
            edit.putInt(MAX, 0);
            edit.putBoolean(INSERTED, true);
            edit.apply();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onOptionsItemSelected()");
        Boolean open = myNavigationDrawer.openMenu(item);
        if (open) return open;
        else return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //METEO
        boolean connected = InternetConnection.isNetworkAvailable(this);
        if (connected) { //controllo che sia disponibile la connessione internet
            helper = new OpenWeatherMapHelper(); //creo oggetto helper
            helper.setApiKey(getString(R.string.OPEN_WEATHER_MAP_API_KEY)); //imposto l'API KEY
            helper.setUnits(Units.METRIC); //imposto l'unità in modo che mostri i gradi in CELSIUS
            Log.i(TAG, "lang= " + Locale.getDefault().getLanguage());
            helper.setLang(Locale.getDefault().getLanguage()); //imposto lingua delle info in base alla lingua del s.o.
            //ottengo info tramite API
            helper.getCurrentWeatherByGeoCoordinates(Double.parseDouble(getResources().getString(R.string.bari_latitude)), Double.parseDouble(getResources().getString(R.string.bari_longitude)), new OpenWeatherMapHelper.CurrentWeatherCallback() {
                @Override
                public void onSuccess(CurrentWeather currentWeather) {
                    Log.i(TAG,
                            "Weather Description: " + currentWeather.getWeatherArray().get(0).getDescription() + "\n"
                                    + "Temp: " + currentWeather.getMain().getTemp() + "\n"
                                    + "Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
                    );

                    //imposto temperatura
                    TextView temp = findViewById(R.id.temp);
                    temp.setText(String.valueOf(Math.round(currentWeather.getMain().getTemp())).concat(getResources().getString(R.string.tempUnit)));
                    //ottengo url icona
                    String iconurl = "http://openweathermap.org/img/w/" + currentWeather.getWeatherArray().get(0).getIcon() + ".png";
                    //imposto icona
                    Glide.with(HomeActivity.this)
                            .load(iconurl)
                            .apply(new RequestOptions().override(144, 144)) //resize immagine altrimenti troppo piccola
                            .into((ImageView) findViewById(R.id.weatherIcon));
                    //imposto descrizione
                    TextView desc = findViewById(R.id.weatherDesc);
                    String formattedString = capitalizeFirstLetter(String.valueOf(currentWeather.getWeatherArray().get(0).getDescription()));
                    desc.setText(formattedString);
                    //mostro btn
                    Button moreInfoBtn = findViewById(R.id.moreBtn);
                    moreInfoBtn.setVisibility(View.VISIBLE);
                }


                @Override
                public void onFailure(Throwable throwable) {
                    Log.i(TAG, throwable.getMessage());
                }
            });
        }
        else { //internet non disponibile
            TextView desc = findViewById(R.id.weatherDesc);
            desc.setText(getResources().getString(R.string.weatherNotAvailableMsg));
            Button moreInfoBtn = findViewById(R.id.moreBtn);
            moreInfoBtn.setVisibility(View.GONE);
        }
    }

    //metodo necessario in quanto le API utilizzate restituiscono una string senza lettere maiuscole
    public String capitalizeFirstLetter(String originalString) {
        if (originalString == null || originalString.length() == 0) {
            return originalString;
        }
        return originalString.trim().substring(0, 1).toUpperCase() + originalString.substring(1);
    }

    private View.OnClickListener moreBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent webWeatherIntent = new Intent(Intent.ACTION_VIEW);
            webWeatherIntent.setData(Uri.parse(getResources().getString(R.string.webUrl)));
            startActivity(webWeatherIntent);
        }
    };
}
