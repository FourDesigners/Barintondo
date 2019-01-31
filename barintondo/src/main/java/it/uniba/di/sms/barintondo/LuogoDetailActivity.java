package it.uniba.di.sms.barintondo;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import it.uniba.di.sms.barintondo.utils.ControllerDBListner;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.FrameVoteStars;
import it.uniba.di.sms.barintondo.utils.InterestsListner;
import it.uniba.di.sms.barintondo.utils.InternetConnection;
import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.UserUtils;


public class LuogoDetailActivity extends AppCompatActivity implements Constants {

    Toolbar myToolbar;
    ImageView myImageView;
    Button itemInfo, itemDirection, itemReview;
    FloatingActionButton fabPref;
    boolean isPref = false;
    ControllerRemoteDB controller;
    FrameVoteStars myFrameVoteStars;
    Luogo luogo;
    ControllerDBListner myListner;
    InterestsListner interestListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_luogo_detail );
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );

        myListner=new ControllerDBListner() {
            @Override
            public void onLuogo(Luogo luogo) {
                onLuogoLoaded( luogo );
            }
            @Override
            public void onEvento(Evento evento) {}
            @Override
            public void onList() {   }
        };

        interestListner = new InterestsListner() {
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

        String myLuogoCod = getIntent().getStringExtra( Constants.INTENT_LUOGO_COD );
        controller = new ControllerRemoteDB( this );
        controller.getLuogo( myLuogoCod, Constants.REQUEST_GET_LUOGO, myListner );

        myToolbar = findViewById( R.id.luogoDetailToolbar );

        setSupportActionBar( myToolbar );
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled( true );

        fabPref = findViewById( R.id.fab );
        myImageView = findViewById( R.id.luogoDetailImage );
        itemInfo = findViewById( R.id.btn_luogo_info );
        itemDirection = findViewById( R.id.btn_luogo_directions );
        itemReview = findViewById( R.id.btn_luogo_reviews );
        myFrameVoteStars=new FrameVoteStars( findViewById( R.id.luogoVoteLayout) );

    }


    public void onLuogoLoaded(final Luogo myLuogo) {
        this.luogo=myLuogo;

        myToolbar.setTitle( myLuogo.getNome() );

        controller.checkPref( myLuogo.getCod(), interestListner );

        fabPref.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetConnection.isNetworkAvailable( LuogoDetailActivity.this )) {
                    Toast.makeText( LuogoDetailActivity.this , getResources().getString( R.string.str_error_not_connected ) , Toast.LENGTH_SHORT ).show();
                } else {
                    if (isPref) controller.removePref( myLuogo.getCod(), interestListner );
                    else controller.addPref( myLuogo.getCod(), interestListner );
                }
            }
        } );

        myFrameVoteStars.setStars( myLuogo.getVoto() );

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

        itemReview.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachReviews( myLuogo );
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
        arguments.putParcelable( EXTRA_LUOGO, myItem );
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

    private void attachReviews(Luogo myItem) {
        Bundle arguments = new Bundle();
        arguments.putString( ITEM_REVIEWS , myItem.getCod() );
        LuogoReviewsFragment fragment = new LuogoReviewsFragment();
        fragment.setArguments( arguments );
        this.getSupportFragmentManager().beginTransaction()
                .replace( R.id.luogo_extra_container , fragment )
                .commit();
        itemOptionSelected( ITEM_REVIEWS );
    }

    private void itemOptionSelected(String option) {
        itemInfo.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlueVariant ) );
        itemDirection.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlueVariant ) );
        itemReview.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlueVariant ) );

        switch (option) {
            case ITEM_DESCRIPTION:
                itemInfo.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlue ) );
                myImageView.setVisibility( View.VISIBLE );
                break;
            case ITEM_DIRECTIONS:
                itemDirection.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlue ) );
                myImageView.setVisibility( View.VISIBLE );
                break;
            case ITEM_REVIEWS:
                itemReview.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlue ) );
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
            UserUtils.codPref.add( luogo.getCod() );
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_pref_added ) , Toast.LENGTH_SHORT ).show();
        } else
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_fail_pref_managing ) , Toast.LENGTH_SHORT ).show();
    }

    public void prefRemoved(boolean result) {
        if (result) {
            checkPrefResult( false );
            UserUtils.codPref.remove( luogo.getCod() );
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_pref_removed ) , Toast.LENGTH_SHORT ).show();
        } else
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_fail_pref_managing ) , Toast.LENGTH_SHORT ).show();
    }

}
