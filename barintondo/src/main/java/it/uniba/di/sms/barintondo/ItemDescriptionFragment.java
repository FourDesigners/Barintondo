package it.uniba.di.sms.barintondo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.uniba.di.sms.barintondo.utils.Constants;


public class ItemDescriptionFragment extends Fragment implements Constants {

    private String itemDescription, orarioA, orarioC;
    TextView textViewDescription, textOrarioA, textOrarioC;



    public ItemDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        String[] ora;

        if (getArguments().containsKey( ITEM_DESCRIPTION )) {
            itemDescription = getArguments().getString( ITEM_DESCRIPTION );
            orarioA = getArguments().getString( ITEM_ORA_A );
            ora=orarioA.split( ":" );
            orarioA=ora[0]+":"+ora[1];
            orarioC = getArguments().getString( ITEM_ORA_C );
            ora=orarioC.split( ":" );
            orarioC=ora[0]+":"+ora[1];


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate( R.layout.fragment_item_description , container , false );

        // Show description as text in a TextView.
        textViewDescription = rootView.findViewById(R.id.text_description);
        textViewDescription.setText( itemDescription );

        textOrarioA = rootView.findViewById(R.id.text_orario_a);
        textOrarioA.setText( orarioA );

        textOrarioC = rootView.findViewById(R.id.text_orario_c);
        textOrarioC.setText( orarioC );

        return rootView;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}
