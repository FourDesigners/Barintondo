package it.uniba.di.sms.barintondo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.Luogo;


public class LuogoDescriptionFragment extends Fragment implements Constants {

    private String orarioA, orarioC;
    TextView textViewDescription, textOrarioA, textOrarioC, eventStart, eventEnd, luogoAdress;
    ConstraintLayout frameDate, frameOrari;
    ImageView iconDirection;
    Luogo myLuogo;

    public LuogoDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        String[] ora;

        if (getArguments().containsKey( EXTRA_LUOGO )) {

            myLuogo = getArguments().getParcelable( EXTRA_LUOGO );
            if(myLuogo.getOraA()!=null) {
                ora = myLuogo.getOraA().split( ":" );
                orarioA = ora[0] + ":" + ora[1];
                ora = myLuogo.getOraC().split( ":" );
                orarioC = ora[0] + ":" + ora[1];
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate( R.layout.fragment_luogo_description , container , false );

        // Show description as text in a TextView.
        textViewDescription = rootView.findViewById(R.id.text_description);
        textViewDescription.setText( myLuogo.getDescription() );

        frameOrari = rootView.findViewById( R.id.luogo_orari );
        textOrarioA = rootView.findViewById(R.id.text_orario_a);
        textOrarioC = rootView.findViewById(R.id.text_orario_c);
        eventStart = rootView.findViewById( R.id.text_event_start );
        eventEnd = rootView.findViewById( R.id.text_event_End );
        frameDate = rootView.findViewById( R.id.layout_dates );
        luogoAdress = rootView.findViewById( R.id.text_adress );
        iconDirection = rootView.findViewById( R.id.luogo_icon_direction );

        luogoAdress.setText( myLuogo.getIndirizzo() );
        iconDirection.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse( "geo:0,0?q=" + myLuogo.getIndirizzo() );
                Intent mapIntent = new Intent( Intent.ACTION_VIEW , gmmIntentUri );
                mapIntent.setPackage( "com.google.android.apps.maps" );
                startActivity( mapIntent );
            }
        } );

        if (orarioA != null) {
            textOrarioA.setText( getContext().getResources().getString( R.string.strOpening )+orarioA );
            textOrarioC.setText( getContext().getResources().getString( R.string.strClosing )+orarioC );
        } else frameOrari.setVisibility( View.GONE );

        if(myLuogo instanceof Evento){
            Evento myEvento= (Evento) myLuogo ;
            eventStart.setText( getContext().getResources().getString( R.string.str_from )+myEvento.getDataInizio().toString() );
            eventEnd.setText( getContext().getResources().getString( R.string.str_to )+myEvento.getDataFine().toString() );
            frameDate.setVisibility( View.VISIBLE );
        }

        return rootView;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}
