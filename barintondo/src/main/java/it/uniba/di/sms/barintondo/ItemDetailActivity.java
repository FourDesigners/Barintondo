package it.uniba.di.sms.barintondo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import it.uniba.di.sms.barintondo.utils.BarintondoItem;
import it.uniba.di.sms.barintondo.utils.Constants;

import static it.uniba.di.sms.barintondo.utils.Constants.imagesPath;

public class ItemDetailActivity extends AppCompatActivity {

    Toolbar myToolbar;
    ImageView myImageView;
    TextView itemDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_item_detail );

        final BarintondoItem myItem = getIntent().getParcelableExtra( Constants.INTENT_ITEM );

        myToolbar = findViewById(R.id.itemDetailToolbar);
        myToolbar.setTitle(myItem.getNome());
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make( view , "Replace with your own action" , Snackbar.LENGTH_LONG )
                        .setAction( "Action" , null ).show();
            }
        } );

        myImageView = findViewById( R.id.itemDetailImage );
        //thumbnail
        Glide.with(this)
                .load(imagesPath + myItem.getThumbnailLink())
                .into(myImageView);

        itemDesc = findViewById( R.id.itemDetailDesc );
        itemDesc.setText( myItem.getDescription() );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
