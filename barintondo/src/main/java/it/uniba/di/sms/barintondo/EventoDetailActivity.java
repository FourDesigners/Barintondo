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

    Toolbar myToolbar;
    ImageView myImageView;
    Button itemInfo, itemDirection;
    ControllerRemoteDB controller;
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
        };

        String myEventCod = getIntent().getStringExtra( Constants.INTENT_LUOGO_COD );
        controller = new ControllerRemoteDB( this );
        controller.getLuogo( myEventCod, Constants.REQUEST_GET_EVENTS, myListner );

        myToolbar = findViewById( R.id.luogoDetailToolbar );

        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);

        myImageView = findViewById( R.id.luogoDetailImage );
        itemInfo = findViewById( R.id.btn_luogo_info );
        itemDirection = findViewById( R.id.btn_luogo_directions );
        fabPref = findViewById( R.id.fab );


    }

    public void onEventoLoaded(final Evento myEvent) {
        this.evento=myEvent;

        myToolbar.setTitle( myEvent.getNome() );

        controller.checkPref( myEvent.getCod(), interestListner );

        fabPref.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetConnection.isNetworkAvailable( EventoDetailActivity.this )) {
                    Toast.makeText( EventoDetailActivity.this , getResources().getString( R.string.str_error_not_connected ) , Toast.LENGTH_SHORT ).show();
                } else {
                    if (isPref) controller.removePref( myEvent.getCod(), interestListner );
                    else controller.addPref( myEvent.getCod(), interestListner );
                }
            }
        } );

        //thumbnail
        Glide.with( this )
                .load( imagesPath + myEvent.getThumbnailLink() )
                .into( myImageView );


        itemInfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemInfo.setAlpha(0.5F);
                itemInfo.setClickable(false);
                itemDirection.setAlpha(1F);
                itemDirection.setClickable(true);
                attachDescription( myEvent );
            }
        } );

        itemDirection.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemInfo.setAlpha(1F);
                itemInfo.setClickable(true);
                itemDirection.setAlpha(0.5F);
                itemDirection.setClickable(false);
                attachDirections( myEvent );
            }
        } );

        //imposto la tab INFO di default
        itemInfo.setAlpha(0.5F);
        itemInfo.setClickable(false);
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
        itemInfo.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlueVariant ) );
        itemDirection.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlueVariant ) );

        switch (option) {
            case ITEM_DESCRIPTION:
                itemInfo.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlue ) );
                myImageView.setVisibility( View.VISIBLE );
                break;
            case ITEM_DIRECTIONS:
                itemDirection.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlue ) );
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
