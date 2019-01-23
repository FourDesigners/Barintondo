package it.uniba.di.sms.barintondo;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import it.uniba.di.sms.barintondo.utils.ControllerPrefered;
import it.uniba.di.sms.barintondo.utils.InternetConnection;
import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.Constants;


public class ItemDetailActivity extends AppCompatActivity implements Constants {

    Toolbar myToolbar;
    ImageView myImageView;
    Button itemInfo, itemDirection, itemReview;
    FloatingActionButton fabPref;
    boolean isPref = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_item_detail );
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );

        final Luogo myItem = getIntent().getParcelableExtra( Constants.INTENT_ITEM );

        myToolbar = findViewById( R.id.itemDetailToolbar );
        myToolbar.setTitle( myItem.getNome() );
        setSupportActionBar( myToolbar );
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled( true );

        fabPref = findViewById( R.id.fab );

        final ControllerPrefered controllerPrefered = new ControllerPrefered( this );
        controllerPrefered.checkPref( myItem.getCod() );

        fabPref.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetConnection.isNetworkAvailable( ItemDetailActivity.this )) {
                    Toast.makeText( ItemDetailActivity.this , getResources().getString( R.string.str_error_not_connected ) , Toast.LENGTH_SHORT ).show();
                } else {
                    if (isPref) controllerPrefered.removePref( myItem.getCod() );
                    else controllerPrefered.addPref( myItem.getCod() );
                }
            }
        } );

        myImageView = findViewById( R.id.itemDetailImage );
        //thumbnail
        Glide.with( this )
                .load( imagesPath + myItem.getThumbnailLink() )
                .into( myImageView );


        itemInfo = findViewById( R.id.item_info );
        itemInfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachDescription( myItem );
            }
        } );

        itemDirection = findViewById( R.id.item_directions );
        itemDirection.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachDirections( myItem );
            }
        } );
        itemReview = findViewById( R.id.item_reviews );
        itemReview.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachReviews( myItem );
            }
        } );

        attachDescription( myItem );
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
        ItemDescriptionFragment fragment = new ItemDescriptionFragment();
        fragment.setArguments( arguments );
        this.getSupportFragmentManager().beginTransaction()
                .replace( R.id.item_extra_container , fragment )
                .commit();
        itemOptionSelected( ITEM_DESCRIPTION );
    }

    private void attachDirections(Luogo myItem) {
        Bundle arguments = new Bundle();
        arguments.putString( ITEM_DIRECTIONS , myItem.getIndirizzo() );
        ItemDirectionsFragment fragment = new ItemDirectionsFragment();
        fragment.setArguments( arguments );
        this.getSupportFragmentManager().beginTransaction()
                .replace( R.id.item_extra_container , fragment )
                .commit();
        itemOptionSelected( ITEM_DIRECTIONS );
    }

    private void attachReviews(Luogo myItem) {
        Bundle arguments = new Bundle();
        arguments.putString( ITEM_REVIEWS , myItem.getCod() );
        ItemReviewsFragment fragment = new ItemReviewsFragment();
        fragment.setArguments( arguments );
        this.getSupportFragmentManager().beginTransaction()
                .replace( R.id.item_extra_container , fragment )
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
                    ColorStateList.valueOf( getResources().getColor( R.color.colorOrange ) )
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
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_pref_added ) , Toast.LENGTH_SHORT ).show();
        } else
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_fail_pref_managing ) , Toast.LENGTH_SHORT ).show();
    }

    public void prefRemoved(boolean result) {
        if (result) {
            checkPrefResult( false );
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_pref_removed ) , Toast.LENGTH_SHORT ).show();
        } else
            Toast.makeText( getApplicationContext() , getResources().getString( R.string.str_fail_pref_managing ) , Toast.LENGTH_SHORT ).show();
    }

}
