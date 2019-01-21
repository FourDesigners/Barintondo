package it.uniba.di.sms.barintondo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.uniba.di.sms.barintondo.utils.Constants;

public class ItemReviewsFragment  extends Fragment implements Constants {

    private String itemCod;
    TextView textViewItemCod;

    public ItemReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        if (getArguments().containsKey( ITEM_REVIEWS )) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            itemCod = getArguments().getString( ITEM_REVIEWS );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate( R.layout.fragment_item_reviews , container , false );

        // Show description as text in a TextView.
        textViewItemCod = rootView.findViewById(R.id.text_item_cod);
        textViewItemCod.setText( itemCod );

        return rootView;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}