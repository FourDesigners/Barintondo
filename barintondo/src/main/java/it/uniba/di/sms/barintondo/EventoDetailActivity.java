package it.uniba.di.sms.barintondo;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Objects;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.MyListners;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.InternetConnection;
import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.UserUtils;

public class EventoDetailActivity extends AppCompatActivity implements Constants {
    private String TAG_CLASS = getClass().getSimpleName();
    Toolbar myToolbar;
    ImageView myImageView;
    Button btnEventInfo, btnEventDirection;
    ControllerRemoteDB controllerRemoteDB;
    Evento evento;
    FloatingActionButton fabPref;
    boolean isPref = false;
    MyListners.SingleLuogo myListner;
    MyListners.Interests interestListner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_evento_detail );
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );

        myListner = new MyListners.SingleLuogo() {
            @Override
            public void onLuogo(Luogo luogo) {
                //non viene mai restituito un luogo a questa activity
            }

            @Override
            public void onEvento(Evento evento) {
                onEventoLoaded( evento );
            }

            @Override
            public void onError(String error) {
                switch (error){
                    case VOLLEY_ERROR_JSON:
                        Log.i(TAG, TAG_CLASS + ": entered luogoListnerOnError, error in pharsing the Json recieved from server");
                        break;
                    case VOLLEY_ERROR_CONNECTION:
                        Log.i(TAG, TAG_CLASS + ": entered luogoListnerOnError, error on the server");
                        break;
                }
            }
        };


        interestListner = new MyListners.Interests() {
            @Override
            public void onAdd(Boolean result) {
                prefAdded( result );
            }

            @Override
            public void onRemove(Boolean result) {
                prefRemoved( result );
            }

            @Override
            public void onCheck(Boolean result) {
                checkPrefResult( result );
            }

            @Override
            public void onError(String error) {
                switch (error){
                    case VOLLEY_ERROR_JSON:
                        Log.i(TAG, TAG_CLASS + ": entered interestListnerOnError, error in pharsing the Json recieved from server");
                        break;
                    case VOLLEY_ERROR_CONNECTION:
                        Log.i(TAG, TAG_CLASS + ": entered interestListnerOnError, error on the server");
                        break;
                }
            }
        };

        String myEventCod = getIntent().getStringExtra( Constants.INTENT_LUOGO_COD );
        controllerRemoteDB = new ControllerRemoteDB( this );
        controllerRemoteDB.getLuogo( myEventCod, Constants.REQUEST_GET_EVENTS, myListner );

        myToolbar = findViewById( R.id.luogoDetailToolbar );

        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);

        myImageView = findViewById( R.id.luogoDetailImage );
        btnEventInfo = findViewById( R.id.btn_luogo_info );
        btnEventDirection = findViewById( R.id.btn_luogo_directions );
        fabPref = findViewById( R.id.fab );


    }

    public void onEventoLoaded(final Evento myEvent) {
        this.evento=myEvent;

        myToolbar.setTitle( myEvent.getNome() );

        controllerRemoteDB.checkPref( myEvent.getCod(), interestListner );

        fabPref.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetConnection.isNetworkAvailable( EventoDetailActivity.this )) {
                    Toast.makeText( EventoDetailActivity.this , getResources().getString( R.string.str_error_not_connected ) , Toast.LENGTH_SHORT ).show();
                } else {
                    if (isPref) controllerRemoteDB.removePref( myEvent.getCod(), interestListner );
                    else controllerRemoteDB.addPref( myEvent.getCod(), interestListner );
                }
            }
        } );

        //thumbnail
        Glide.with( this )
                .load( imagesPath + myEvent.getThumbnailLink() )
                .into( myImageView );


        btnEventInfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEventInfo.setAlpha(0.5F);
                btnEventInfo.setClickable(false);
                btnEventDirection.setAlpha(1F);
                btnEventDirection.setClickable(true);
                attachDescription( myEvent );
            }
        } );

        btnEventDirection.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEventInfo.setAlpha(1F);
                btnEventInfo.setClickable(true);
                btnEventDirection.setAlpha(0.5F);
                btnEventDirection.setClickable(false);
                attachDirections( myEvent );
            }
        } );

        //imposto la tab INFO di default
        btnEventInfo.setAlpha(0.5F);
        btnEventInfo.setClickable(false);
        attachDescription( myEvent );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected( item );

    }

    private void attachDescription(Evento myEvent) {
        Bundle arguments = new Bundle();
        arguments.putParcelable( EXTRA_LUOGO, myEvent );
        LuogoDescriptionFragment fragment = new LuogoDescriptionFragment();
        fragment.setArguments( arguments );
        this.getSupportFragmentManager().beginTransaction()
                .replace( R.id.luogo_extra_container , fragment )
                .commit();
        itemOptionSelected( ITEM_DESCRIPTION );
    }

    private void attachDirections(Luogo luogo) {
        Bundle arguments = new Bundle();
        arguments.putParcelable( EXTRA_LUOGO, luogo );
        LuogoDirectionsFragment fragment = new LuogoDirectionsFragment();
        fragment.setArguments( arguments );
        this.getSupportFragmentManager().beginTransaction()
                .replace( R.id.luogo_extra_container , fragment )
                .commit();
        itemOptionSelected( ITEM_DIRECTIONS );
    }


    private void itemOptionSelected(String option) {
        btnEventInfo.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlueVariant ) );
        btnEventDirection.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlueVariant ) );

        switch (option) {
            case ITEM_DESCRIPTION:
                btnEventInfo.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlue ) );
                myImageView.setVisibility( View.VISIBLE );
                break;
            case ITEM_DIRECTIONS:
                btnEventDirection.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlue ) );
                myImageView.setVisibility( View.GONE );
                break;
        }
    }

    public void checkPrefResult(boolean result) {
        if (result) {
            isPref = true;
            ImageViewCompat.setImageTintList(
                    fabPref ,
                    ColorStateList.valueOf( getResources().getColor( R.color.colorSecondaryBlue ) )
            );
        } else {
            isPref = false;
            ImageViewCompat.setImageTintList(
                    fabPref ,
                    ColorStateList.valueOf( getResources().getColor( R.color.colorWhite ) )
            );
        }
    }

    public void prefAdded(boolean result) {
        if (result) {
            checkPrefResult( true );
            UserUtils.codPref.add( evento.getCod() );
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_pref_added ) , Toast.LENGTH_SHORT ).show();
        } else
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_fail_pref_managing ) , Toast.LENGTH_SHORT ).show();
    }

    public void prefRemoved(boolean result) {
        if (result) {
            checkPrefResult( false );
            UserUtils.codPref.remove( evento.getCod() );
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_pref_removed ) , Toast.LENGTH_SHORT ).show();
        } else
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_fail_pref_managing ) , Toast.LENGTH_SHORT ).show();
    }

}
