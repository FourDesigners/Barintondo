package it.uniba.di.sms.barintondo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.chip.Chip;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.MyListners;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.InternetConnection;
import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.MyNavigationDrawer;
import it.uniba.di.sms.barintondo.utils.SliderAdapter;
import me.relex.circleindicator.CircleIndicator;

public class HomeActivity extends AppCompatActivity implements Constants {

    private Toolbar myToolbar;
    MyNavigationDrawer myNavigationDrawer;
    OpenWeatherMapHelper helper;
    Button /*moreBtn, */goInterests, goAttractionBtn, goFoodBtn, goSleepBtn, goNearBariBtn, goEvents;
    Chip moreBtn;
    ControllerRemoteDB controllerRemoteDB;
    MyListners.LuoghiList myDBListner;

    //elementi per lo slider
    private static ViewPager mPager;
    private static int currentPage = 0;
    private ArrayList<Luogo> luogoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        myNavigationDrawer = new MyNavigationDrawer(this,
                (NavigationView) findViewById(R.id.nav_view),
                (DrawerLayout) findViewById(R.id.drawer_layout));
        myNavigationDrawer.build();

        myToolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_hamburger);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorAccent));

        actionbar.setHomeAsUpIndicator(drawable);

        moreBtn = findViewById(R.id.moreBtn);
        moreBtn.setOnClickListener(moreBtnListener);

        goInterests = findViewById( R.id.btnHomeGoInterests );
        goInterests.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( v.getContext(), InterestsListActivity.class );
                startActivity( intent );
            }
        } );
        goAttractionBtn=findViewById( R.id.btnHomeGoAttraction );
        goAttractionBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeStartActivity(INTENT_ATTRACTIONS);
            }
        } );

        goFoodBtn=findViewById( R.id.btnHomeGoFood ) ;
        goFoodBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeStartActivity( INTENT_EATING );
            }
        } );
        goSleepBtn= findViewById( R.id.btnHomeGoSleep );
        goSleepBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeStartActivity( INTENT_SLEEPING );
            }
        } );
        goNearBariBtn=findViewById( R.id.btnHomeGoNearBari );
        goNearBariBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeStartActivity( INTENT_NEAR );
            }
        } );
        goEvents=findViewById( R.id.btnHomeGoEvents );
        goEvents.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeStartActivity( INTENT_EVENTS );
            }
        } );


        controllerRemoteDB = new ControllerRemoteDB( this );




    }

    private void homeStartActivity(String constCall){
        Intent intent = new Intent( this , LuogoListActivity.class );
        intent.putExtra( Constants.INTENT_ACTIVITY_ITEM_TYPE , constCall );
        startActivity( intent );
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

        //slider eventi
        myDBListner=new MyListners.LuoghiList() {
            @Override
            public void onList() {
                final ArrayList<Evento> listEventi= new ArrayList<>(  );
                for(Luogo l: luogoList){
                    Evento evento = (Evento) l;
                    if(evento.getDaysToEvent( )<=30) {
                        listEventi.add( (Evento) l );
                    }
                }
                setSlider(listEventi);
            }
        };

        controllerRemoteDB.populateInterestsCod();
        controllerRemoteDB.getLuoghiList( Constants.REQUEST_GET_EVENTS, luogoList , myDBListner);
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
            /*
            Intent webWeatherIntent = new Intent(Intent.ACTION_VIEW);
            webWeatherIntent.setData(Uri.parse(getResources().getString(R.string.webUrl)));
            startActivity(webWeatherIntent);
            */
            Intent intent  = new Intent(getApplicationContext(), MeteoActivity.class);
            startActivity(intent);

        }
    };

    private void setSlider(final ArrayList<Evento> listEventi) {


        mPager = findViewById(R.id.pager);
        mPager.setAdapter(new SliderAdapter(this,  listEventi));
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == listEventi.size()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 5000, 4000);
    }
}
