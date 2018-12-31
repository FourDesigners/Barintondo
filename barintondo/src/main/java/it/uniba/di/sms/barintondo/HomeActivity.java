package it.uniba.di.sms.barintondo;

import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kwabenaberko.openweathermaplib.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;

import java.util.Locale;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;

public class HomeActivity extends AppCompatActivity implements Constants {

    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;
    OpenWeatherMapHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home );


        fillChips();            // Inserimento filtri

        /*
        //popolamento db
        ProfileOpenHelper dbHelper = new ProfileOpenHelper(this, Constants.DB_NAME, null, 1);
        SQLiteDatabase myDB = dbHelper.getWritableDatabase();
        ContentValues user = new ContentValues();
        user.put(Constants.COLUMN_NICKNAME, "prova1");
        user.put(Constants.COLUMN_EMAIL, "pinco@prova.it");
        user.put(Constants.COLUMN_PASSWORD, "prova1");
        long newID = myDB.insert(Constants.TABLE_UTENTE, null, user);
        */

        myNavigationDrawer = new MyNavigationDrawer(this,
                (NavigationView) findViewById(R.id.nav_view),
                (DrawerLayout )findViewById(R.id.drawer_layout));
        myNavigationDrawer.build();

        myToolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_hamburger);


    }

    private void fillChips() {
        SharedPreferences categories = getSharedPreferences(PREFS_NAME, 0);
        if(!categories.contains(INSERTED)) {
            SharedPreferences.Editor edit = categories.edit();
            edit.putBoolean(INSERTED, true);
            edit.putString(INTENT_ATTRACTIONS, CHIPS_ATTRACTIONS);
            edit.putString(INTENT_EATING, CHIPS_EATING);
            edit.putString(INTENT_SLEEPING, CHIPS_SLEEPING);
            edit.putString(INTENT_EVENTS, CHIPS_EVENTS);
            edit.putString(INTENT_NEAR, CHIPS_ATTRACTIONS);
            edit.apply();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onOptionsItemSelected()");
        Boolean open = myNavigationDrawer.openMenu( item );
        if(open) return open;
        else return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //METEO
        helper = new OpenWeatherMapHelper(); //creo oggetto helper
        helper.setApiKey(getString(R.string.OPEN_WEATHER_MAP_API_KEY)); //imposto l'API KEY
        helper.setUnits(Units.METRIC); //imposto l'unità in modo che mostri i gradi in CELSIUS
        Log.i(TAG, "lang= " + Locale.getDefault().getLanguage());
        helper.setLang(Locale.getDefault().getLanguage()); //imposto lingua delle info in base alla lingua del s.o.
        //ottengo info
        helper.getCurrentWeatherByGeoCoordinates(Double.parseDouble(getResources().getString(R.string.bari_latitude)), Double.parseDouble(getResources().getString(R.string.bari_longitude)), new OpenWeatherMapHelper.CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                Log.i(TAG,
                        "Weather Description: " + currentWeather.getWeatherArray().get(0).getDescription() + "\n"
                                +"Temp: "+currentWeather.getMain().getTemp() +"\n"
                                +"Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
                );

                //imposto temperatura
                TextView temp = findViewById(R.id.temp);
                temp.setText(String.valueOf(Math.round(currentWeather.getMain().getTemp())));
                //ottengo url icona
                String iconurl = "http://openweathermap.org/img/w/" + currentWeather.getWeatherArray().get(0).getIcon() + ".png";
                //imposto icona
                Glide.with(HomeActivity.this)
                        .load(iconurl)
                        .apply(new RequestOptions().override(192, 192))
                        .into( (ImageView) findViewById(R.id.weatherIcon));
                //imposto descrizione
                TextView desc = findViewById(R.id.weatherDesc);
                desc.setText(String.valueOf(currentWeather.getWeatherArray().get(0).getDescription()));
                //imposto scala
                TextView tempScale = findViewById(R.id.tempScale);
                tempScale.setText("°C");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, throwable.getMessage());
            }
        });
    }
}
