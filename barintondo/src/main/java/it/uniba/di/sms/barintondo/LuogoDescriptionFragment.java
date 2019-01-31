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
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.ControllerDBListner;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.Luogo;


public class LuogoDescriptionFragment extends Fragment implements Constants {

    private String orarioA, orarioC;
    TextView textViewDescription, textOrarioA, textOrarioC, eventStart, eventEnd, luogoAdress, textViewLuogoEvento;
    ConstraintLayout frameDate, frameOrari;
    LinearLayout layoutLuogoEvento;
    ImageView iconDirection;
    Luogo myLuogo;
    ControllerRemoteDB controllerRemoteDB;
    ControllerDBListner myListner;


    public LuogoDescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        String[] ora;

        if (getArguments().containsKey( EXTRA_LUOGO )) {

            myLuogo = getArguments().getParcelable( EXTRA_LUOGO );
            if (myLuogo.getOraA() != null) {
                ora = myLuogo.getOraA().split( ":" );
                orarioA = ora[0] + ":" + ora[1];
                ora = myLuogo.getOraC().split( ":" );
                orarioC = ora[0] + ":" + ora[1];
            }
            controllerRemoteDB = new ControllerRemoteDB( this.getContext() );
            myListner = new ControllerDBListner() {
                @Override
                public void onLuogo(Luogo luogo) {
                    onLuogoLoaded( luogo );
                }
                @Override
                public void onEvento(Evento evento) { }
            };
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate( R.layout.fragment_luogo_description , container , false );

        // Show description as text in a TextView.
        textViewDescription = rootView.findViewById( R.id.text_description );
        textViewDescription.setText( myLuogo.getDescription() );

        frameOrari = rootView.findViewById( R.id.luogo_orari );
        textOrarioA = rootView.findViewById( R.id.text_orario_a );
        textOrarioC = rootView.findViewById( R.id.text_orario_c );
        eventStart = rootView.findViewById( R.id.text_event_start );
        eventEnd = rootView.findViewById( R.id.text_event_End );
        frameDate = rootView.findViewById( R.id.layout_dates );
        luogoAdress = rootView.findViewById( R.id.text_adress );
        iconDirection = rootView.findViewById( R.id.luogo_icon_direction );
        layoutLuogoEvento = rootView.findViewById( R.id.layout_evetn_luogo_ref );
        textViewLuogoEvento = rootView.findViewById( R.id.text_view_event_luogo_ref );
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
            textOrarioA.setText( getContext().getResources().getString( R.string.strOpening ) + orarioA );
            textOrarioC.setText( getContext().getResources().getString( R.string.strClosing ) + orarioC );
        } else frameOrari.setVisibility( View.GONE );

        if (myLuogo instanceof Evento) {
            Evento myEvento = (Evento) myLuogo;
            if (myEvento.getDataInizio().equals( myEvento.getDataFine() )) {
                eventStart.setText( myEvento.getDataInizio().toString() );
                eventEnd.setVisibility( View.GONE );
            } else {
                eventStart.setText( getContext().getResources().getString( R.string.str_from ) + myEvento.getDataInizio().toString() );
                eventEnd.setText( getContext().getResources().getString( R.string.str_to ) + myEvento.getDataFine().toString() );
            }
            frameDate.setVisibility( View.VISIBLE );

            if (myEvento.getCodLuogo() != null) {
                controllerRemoteDB.getLuogo( myEvento.getCodLuogo() , Constants.REQUEST_GET_LUOGO, myListner );
            }
        }

        return rootView;
    }

    public void onLuogoLoaded(Luogo luogoEvento) {
        layoutLuogoEvento.setVisibility( View.VISIBLE );
        String luogo = "";
        if (luogoEvento.getSottoCat().equals( "Teatri" )) {
            luogo += getResources().getString( R.string.strTheatre ) + " ";
        }
        luogo += luogoEvento.getNome();
        textViewLuogoEvento.setText( luogo );
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
