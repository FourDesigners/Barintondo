package it.uniba.di.sms.barintondo;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Objects;

import it.uniba.di.sms.barintondo.utils.MyListners;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.FrameVoteStars;
import it.uniba.di.sms.barintondo.utils.InternetConnection;
import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.UserUtils;


public class LuogoDetailActivity extends AppCompatActivity implements Constants {
    private String TAG_CLASS = getClass().getSimpleName();
    Toolbar myToolbar;
    ImageView myImageView;
    Button btnLuogoInfo, btnLuogoDirection, btnLuogoReview;
    FloatingActionButton fabPref;
    boolean isPref = false;
    ControllerRemoteDB controller;
    FrameVoteStars myFrameVoteStars;
    Luogo luogo;
    MyListners.SingleLuogo myListner;
    MyListners.Interests interestListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_luogo_detail );
        Log.i( TAG , TAG_CLASS + ":entered onCreate()" );

        myListner=new MyListners.SingleLuogo() {
            @Override
            public void onLuogo(Luogo luogo) {
                onLuogoLoaded( luogo );
            }

            @Override
            public void onEvento(Evento evento) {
                // non viene mai restituito un evento

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

        String myLuogoCod = getIntent().getStringExtra( Constants.INTENT_LUOGO_COD );
        controller = new ControllerRemoteDB( this );
        controller.getLuogo( myLuogoCod, Constants.REQUEST_GET_LUOGO, myListner );

        myToolbar = findViewById( R.id.luogoDetailToolbar );

        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);

        fabPref = findViewById( R.id.fab );

        myImageView = findViewById( R.id.luogoDetailImage );

        btnLuogoInfo = findViewById( R.id.btn_luogo_info );
        btnLuogoDirection = findViewById( R.id.btn_luogo_directions );
        btnLuogoReview = findViewById( R.id.btn_luogo_reviews );
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


        btnLuogoInfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLuogoInfo.setAlpha(0.5F);
                btnLuogoInfo.setClickable(false);
                btnLuogoDirection.setAlpha(1F);
                btnLuogoDirection.setClickable(true);
                btnLuogoReview.setAlpha(1F);
                btnLuogoReview.setClickable(true);
                attachDescription( myLuogo );
            }
        } );


        btnLuogoDirection.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLuogoInfo.setAlpha(1F);
                btnLuogoInfo.setClickable(true);
                btnLuogoDirection.setAlpha(0.5F);
                btnLuogoDirection.setClickable(false);
                btnLuogoReview.setAlpha(1F);
                btnLuogoReview.setClickable(true);
                attachDirections( myLuogo );
            }
        } );

        btnLuogoReview.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLuogoInfo.setAlpha(1F);
                btnLuogoInfo.setClickable(true);
                btnLuogoDirection.setAlpha(1F);
                btnLuogoDirection.setClickable(true);
                btnLuogoReview.setAlpha(0.5F);
                btnLuogoReview.setClickable(false);
                attachReviews( myLuogo );
            }
        } );

        //imposto la tab INFO di default
        btnLuogoInfo.setAlpha(0.5F);
        btnLuogoInfo.setClickable(false);
        attachDescription( myLuogo );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //supportFinishAfterTransition();
                onBackPressed();
                overridePendingTransition(R.anim.slide_in,  R.anim.slide_out);
                return true;
        }
        return super.onOptionsItemSelected( item );

    }

    private void attachDescription(Luogo luogo) {
        Bundle arguments = new Bundle();
        arguments.putParcelable( EXTRA_LUOGO, luogo );
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

    private void attachReviews(Luogo luogo) {
        Bundle arguments = new Bundle();
        arguments.putString( ITEM_REVIEWS , luogo.getCod() );
        LuogoReviewsFragment fragment = new LuogoReviewsFragment();
        fragment.setArguments( arguments );
        this.getSupportFragmentManager().beginTransaction()
                .replace( R.id.luogo_extra_container , fragment )
                .commit();
        itemOptionSelected( ITEM_REVIEWS );
    }

    private void itemOptionSelected(String option) {
        btnLuogoInfo.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlueVariant ) );
        btnLuogoDirection.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlueVariant ) );
        btnLuogoReview.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlueVariant ) );

        switch (option) {
            case ITEM_DESCRIPTION:
                btnLuogoInfo.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlue ) );
                myImageView.setVisibility( View.VISIBLE );
                myFrameVoteStars.showVoteFrame();
                break;
            case ITEM_DIRECTIONS:
                btnLuogoDirection.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlue ) );
                myImageView.setVisibility( View.GONE );
                myFrameVoteStars.hideVoteFrame();
                break;
            case ITEM_REVIEWS:
                btnLuogoReview.setBackgroundColor( getResources().getColor( R.color.colorSecondaryBlue ) );
                myImageView.setVisibility( View.GONE );
                myFrameVoteStars.hideVoteFrame();
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
