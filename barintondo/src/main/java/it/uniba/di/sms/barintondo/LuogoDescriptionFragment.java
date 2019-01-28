package it.uniba.di.sms.barintondo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import it.uniba.di.sms.barintondo.utils.Constants;


public class LuogoDescriptionFragment extends Fragment implements Constants {

    private String itemDescription, orarioA, orarioC;
    TextView textViewDescription, textOrarioA, textOrarioC;
    TableLayout tableOrari;

    public LuogoDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        String[] ora;

        if (getArguments().containsKey( ITEM_DESCRIPTION )) {
            itemDescription = getArguments().getString( ITEM_DESCRIPTION );
            orarioA = getArguments().getString( ITEM_ORA_A );
            if(orarioA!=null) {
                ora = orarioA.split( ":" );
                orarioA = ora[0] + ":" + ora[1];
                orarioC = getArguments().getString( ITEM_ORA_C );
                ora = orarioC.split( ":" );
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
        textViewDescription.setText( itemDescription );

        tableOrari = rootView.findViewById( R.id.luogo_table_orari );
        textOrarioA = rootView.findViewById(R.id.text_orario_a);
        textOrarioC = rootView.findViewById(R.id.text_orario_c);

        if (orarioA != null) {
            textOrarioA.setText( orarioA );
            textOrarioC.setText( orarioC );
        } else tableOrari.setVisibility( View.GONE );

        return rootView;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}
