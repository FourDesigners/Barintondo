package it.uniba.di.sms.barintondo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;
import it.uniba.di.sms.barintondo.utils.Review;
import it.uniba.di.sms.barintondo.utils.ReviewAdapter;

public class LuogoReviewsFragment extends Fragment implements Constants {

    private String itemCod;
    List<Review> reviewList;
    ReviewAdapter mAdapter;
    RecyclerView recyclerView;

    public LuogoReviewsFragment() {
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
            reviewList = new ArrayList<>(  );

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate( R.layout.fragment_luogo_reviews , container , false );

        mAdapter = new ReviewAdapter( rootView.getContext(), reviewList );
        //recyclerView setup
        recyclerView = rootView.findViewById( R.id.luogo_reviews_list_recycler_view );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( rootView.getContext() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.setAdapter( mAdapter );





        ControllerRemoteDB controller = new ControllerRemoteDB( rootView.getContext() );
        controller.getReviewsList( itemCod, reviewList, mAdapter );

        return rootView;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}