package it.uniba.di.sms.barintondo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import it.uniba.di.sms.barintondo.utils.Constants;

public class ItemDirectionsFragment extends Fragment implements Constants {

    private String itemDirections;
    TextView textViewDirections;
    Button openGMaps;

    public ItemDirectionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        if (getArguments().containsKey( ITEM_DIRECTIONS )) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            itemDirections = getArguments().getString( ITEM_DIRECTIONS );
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate( R.layout.fragment_item_directions , container , false );

        // Show description as text in a TextView.
        textViewDirections = rootView.findViewById( R.id.text_item_directions );
        String indicazione = rootView.getResources().getString( R.string.str_located_in )+"\n"+itemDirections;
        textViewDirections.setText( indicazione );

        openGMaps = rootView.findViewById( R.id.button_open_gmaps );
        openGMaps.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse( "geo:0,0?q=" + itemDirections );
                Intent mapIntent = new Intent( Intent.ACTION_VIEW , gmmIntentUri );
                mapIntent.setPackage( "com.google.android.apps.maps" );
                startActivity( mapIntent );
            }
        } );

        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}