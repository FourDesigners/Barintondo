package it.uniba.di.sms.barintondo;

import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ControllerPrefered;
import it.uniba.di.sms.barintondo.utils.CouponLuogo;
import it.uniba.di.sms.barintondo.utils.InternetConnection;
import it.uniba.di.sms.barintondo.utils.Luogo;

public class CouponDetailActivity extends AppCompatActivity implements Constants {

    private static final String TAG = CouponDetailActivity.class.getSimpleName();

    Toolbar myToolbar;
    ImageView myImageView;
    TextView desc;
    CouponLuogo myCoupon;
    Button useBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_coupon_detail );
        Log.i( TAG , getClass().getSimpleName() + ":entered onCreate()" );

        myCoupon = getIntent().getParcelableExtra( Constants.INTENT_COUPON);

        myToolbar = findViewById( R.id.coupon_detail_activity_toolbar );
        myToolbar.setTitle( myCoupon.getLuogo() );
        setSupportActionBar( myToolbar );
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled( true );

        myImageView = findViewById( R.id.luogoDetailImage );
        //thumbnail
        /*Glide.with( this )
                .load( imagesPath + myCoupon.getThumbnailLink() )
                .into( myImageView );*/

        desc = findViewById( R.id.couponDesc );
        desc.setText(myCoupon.getDescription());
        Log.i(TAG, "dati:" + myCoupon.getDescription() + myCoupon.descrizione_en + myCoupon.descrizione_it);

        useBtn = findViewById( R.id.useBtn );
        useBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!InternetConnection.isNetworkAvailable( CouponDetailActivity.this )) {
                    Toast.makeText( CouponDetailActivity.this , getResources().getString( R.string.str_error_not_connected ) , Toast.LENGTH_SHORT ).show();
                } else {
                    //TODO
                }
            }
        } );

    }

    @Override
    protected void onResume() {
        super.onResume();


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
}
