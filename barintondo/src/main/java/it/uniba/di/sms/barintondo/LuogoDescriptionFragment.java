package it.uniba.di.sms.barintondo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Coupon;
import it.uniba.di.sms.barintondo.utils.LocalDBOpenHelper;
import it.uniba.di.sms.barintondo.utils.MyListeners;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.Luogo;


public class LuogoDescriptionFragment extends Fragment implements Constants {
    private String TAG_CLASS = getClass().getSimpleName();
    private String orarioA, orarioC;
    TextView textViewDescription, textOrarioA, textOrarioC, eventStart, eventEnd, luogoAdress, textViewLuogoEvento, textLuogoCoupon;
    ConstraintLayout frameDate, frameOrari;
    LinearLayout layoutLuogoEvento, layoutLuogoIndirizzo, layoutCoupon, layoutCouponSeparator;
    ImageView iconDirection, iconCoupon;
    Luogo myLuogo;
    ControllerRemoteDB controllerRemoteDB;
    MyListeners.SingleLuogo myListener;


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
            myListener = new MyListeners.SingleLuogo() {
                @Override
                public void onLuogo(Luogo luogo) {
                    Log.i("Test", "Entered onLuogo");
                    onLuogoLoaded( luogo );
                }

                @Override
                public void onEvento(Evento evento) {
                    //non viene mai restituito un evento
                }

                @Override
                public void onError(String error) {
                    switch (error){
                        case VOLLEY_ERROR_JSON:
                            Log.i(TAG, TAG_CLASS + ": entered luogoListenerOnError, error in pharsing the Json recieved from server");
                            break;
                        case VOLLEY_ERROR_CONNECTION:
                            Log.i(TAG, TAG_CLASS + ": entered luogoListenerOnError, error on the server");
                            break;
                    }
                }
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
        layoutLuogoIndirizzo = rootView.findViewById( R.id.layout_luogo_indirizzo );
        textViewLuogoEvento = rootView.findViewById( R.id.text_view_event_luogo_ref );
        layoutCoupon = rootView.findViewById( R.id.layout_your_luogo_coupon );
        layoutCouponSeparator = rootView.findViewById( R.id.layout_coupon_separator );
        luogoAdress.setText( myLuogo.getIndirizzo() );
        textLuogoCoupon = rootView.findViewById( R.id.text_possessed_coupon );
        iconCoupon = rootView.findViewById( R.id.luogo_icon_coupon );
        layoutLuogoIndirizzo.setOnClickListener( new View.OnClickListener() {
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
                layoutLuogoEvento.setVisibility( View.VISIBLE );
                controllerRemoteDB.getLuogo( myEvento.getCodLuogo() , Constants.REQUEST_GET_LUOGO, myListener );
            }
        }
        List<Coupon> couponList=new ArrayList<>(  );

        int couponNumber = LocalDBOpenHelper.getNumberCouponLuogo( getContext(), myLuogo.getCod() );

        if(couponNumber>0){
            textLuogoCoupon.setText(getContext().getResources().getQuantityString(R.plurals.possessedCouponPlace, couponNumber, couponNumber));
            layoutCoupon.setVisibility( View.VISIBLE );
            layoutCouponSeparator.setVisibility( View.VISIBLE );
            iconCoupon.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( getContext(), CouponListActivity.class );
                    intent.putExtra( INTENT_LUOGO_NAME, myLuogo.getNome() );
                    startActivity( intent );
                }
            } );
        }

        return rootView;
    }

    public void onLuogoLoaded(final Luogo luogoEvento) {

        String luogo = "";
        if (luogoEvento.getSottoCat().equals( "Teatri" )) {
            luogo += getResources().getString( R.string.strTheatre ) + " ";
        }
        luogo += luogoEvento.getNome();
        textViewLuogoEvento.setText( luogo );
        layoutLuogoEvento.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getContext(), LuogoDetailActivity.class );
                intent.putExtra( INTENT_LUOGO_COD, luogoEvento.getCod() );
                startActivity( intent );
            }
        } );
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
