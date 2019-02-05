package it.uniba.di.sms.barintondo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.di.sms.barintondo.utils.Contact;
import it.uniba.di.sms.barintondo.utils.ContactAdapter;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;

public class ContactsActivity extends AppCompatActivity implements ContactAdapter.ItemsAdapterListener{
    private RecyclerView recyclerView;
    private ContactAdapter mAdapter;
    private List<Contact> numbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar myToolbar = findViewById( R.id.contacts_toolbar );
        myToolbar.setTitle(R.string.contacts);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        assert actionbar != null; //serve per non far apparire il warning che dice che actionbar potrebbe essere null
        actionbar.setDisplayHomeAsUpEnabled(true);


        numbers = new ArrayList<>();
        Context activity = getApplicationContext();

        String[] contacts = activity.getResources().getStringArray(R.array.contacts);
        String[] contactsNumbers = activity.getResources().getStringArray(R.array.contactsNumbers);
        for(int i=0; i<contacts.length; i++) {
            numbers.add(new Contact(contacts[i], contactsNumbers[i]));
        }

        mAdapter = new ContactAdapter( this, numbers, this);

        //recyclerView setup
        recyclerView = findViewById( R.id.contacts_recycler_view );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.addItemDecoration( new MyDividerItemDecoration( this , DividerItemDecoration.VERTICAL , 36 ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( mAdapter );

        // white background notification bar
        //whiteNotificationBar( recyclerView );
    }

    @Override
    public void onItemsSelected(Contact item) {
        String phone = item.getNumero();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
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
