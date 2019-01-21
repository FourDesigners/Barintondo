package it.uniba.di.sms.barintondo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.uniba.di.sms.barintondo.utils.Constants;

public class ItemDirectionsFragment extends Fragment implements Constants {

    private String itemDirections;
    TextView textViewDirections;

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
        textViewDirections = rootView.findViewById(R.id.text_item_directions);
        textViewDirections.setText( itemDirections );

        return rootView;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}