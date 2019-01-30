package it.uniba.di.sms.barintondo;

import android.content.res.ColorStateList;
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

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.FrameVoteStars;
import it.uniba.di.sms.barintondo.utils.InternetConnection;
import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.UserUtils;

public class EventoDetailActivity extends AppCompatActivity implements Constants {

    Toolbar myToolbar;
    ImageView myImageView;
    Button itemInfo, itemDirection;
    ControllerRemoteDB controller;
    Evento evento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_evento_detail );
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );

        String myEventCod = getIntent().getStringExtra( Constants.INTENT_LUOGO_COD );
        controller = new ControllerRemoteDB( this );
        controller.getLuogo( myEventCod );

        myToolbar = findViewById( R.id.luogoDetailToolbar );

        setSupportActionBar( myToolbar );
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled( true );

        myImageView = findViewById( R.id.luogoDetailImage );
        itemInfo = findViewById( R.id.btn_luogo_info );
        itemDirection = findViewById( R.id.btn_luogo_directions );

    }

    public void onEventoLoaded(final Evento myLuogo) {
        this.evento=new Evento(  myLuogo);

        myToolbar.setTitle( myLuogo.getNome() );

        //thumbnail
        Glide.with( this )
                .load( imagesPath + myLuogo.getThumbnailLink() )
                .into( myImageView );


        itemInfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachDescription( myLuogo );
            }
        } );


        itemDirection.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachDirections( myLuogo );
            }
        } );

        attachDescription( myLuogo );

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

    private void attachDescription(Luogo myItem) {
        Bundle arguments = new Bundle();
        arguments.putString( ITEM_DESCRIPTION , myItem.getDescription() );
        arguments.putString( ITEM_ORA_A , myItem.getOraA() );
        arguments.putString( ITEM_ORA_C , myItem.getOraC() );
        LuogoDescriptionFragment fragment = new LuogoDescriptionFragment();
        fragment.setArguments( arguments );
        this.getSupportFragmentManager().beginTransaction()
                .replace( R.id.luogo_extra_container , fragment )
                .commit();
        itemOptionSelected( ITEM_DESCRIPTION );
    }

    private void attachDirections(Luogo myItem) {
        Bundle arguments = new Bundle();
        arguments.putString( ITEM_DIRECTIONS , myItem.getIndirizzo() );
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
                myImageView.setVisibility( View.VISIBLE );
                break;
        }
    }



}
