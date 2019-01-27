package it.uniba.di.sms.barintondo;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    LinearLayout layoutYourReview;
    CoordinatorLayout layourReviewsList;
    public ImageView[] yourReviewVoteStar;
    FloatingActionButton fabAdReview;
    ImageButton btnCancelReview, btnSaveReview;
    private int yourVote;
    EditText reviewEditText;
    LuogoReviewsFragment mLuogoReviewFragment;
    ControllerRemoteDB controllerRemoteDB;

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
            yourReviewVoteStar = new ImageView[5];
            mLuogoReviewFragment=this;
            controllerRemoteDB = new ControllerRemoteDB( this.getContext() );

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater , final ViewGroup container ,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate( R.layout.fragment_luogo_reviews , container , false );

        mAdapter = new ReviewAdapter( rootView.getContext(), reviewList );
        //recyclerView setup
        recyclerView = rootView.findViewById( R.id.luogo_reviews_list_recycler_view );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( rootView.getContext() );
        recyclerView.addItemDecoration( new MyDividerItemDecoration( rootView.getContext() , DividerItemDecoration.VERTICAL , 36 ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.setAdapter( mAdapter );

        layoutYourReview = rootView.findViewById(R.id.layout_your_review);
        layourReviewsList = rootView.findViewById( R.id.layout_reviews_list );
        fabAdReview = rootView.findViewById( R.id.fabAddReview );
        btnCancelReview = rootView.findViewById( R.id.btnCancelReview );
        btnSaveReview = rootView.findViewById( R.id.btnSaveReview );
        reviewEditText = rootView.findViewById( R.id.edit_text_review );

        fabAdReview.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layourReviewsList.setVisibility( View.GONE );
                layoutYourReview.setVisibility( View.VISIBLE );
            }
        } );

        btnCancelReview.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layourReviewsList.setVisibility( View.VISIBLE );
                layoutYourReview.setVisibility( View.GONE );
            }
        } );

        final Resources res = rootView.getResources();
        for(int i=0; i<5; i++){
            int id = res.getIdentifier("yourReviewStar"+String.valueOf( i ), "id", rootView.getContext().getPackageName());
            yourReviewVoteStar[i]= rootView.findViewById( id );
            final int j=i+1;
            yourReviewVoteStar[i].setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    starSelected(j, res);
                }
            } );
        }
        starSelected(1, res);


        controllerRemoteDB.getReviewsList( itemCod, reviewList, mAdapter );

        btnSaveReview.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllerRemoteDB.saveReview( reviewEditText.getText().toString(), itemCod, yourVote, mLuogoReviewFragment  );
            }
        } );

        return rootView;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void starSelected(int j, Resources res){
        yourVote=j;
        int i;
        for(i=0; i<j;i++){
            ImageViewCompat.setImageTintList(
                    yourReviewVoteStar[i] ,
                    ColorStateList.valueOf( res.getColor( R.color.colorOrange ) )
            );
        }
        for(;i<5;i++){
            ImageViewCompat.setImageTintList(
                    yourReviewVoteStar[i] ,
                    ColorStateList.valueOf( res.getColor( R.color.colorBlack ) )
            );
        }
    }

    public void onSaveReviewResult(){
        layourReviewsList.setVisibility( View.VISIBLE );
        layoutYourReview.setVisibility( View.GONE );
        controllerRemoteDB.getReviewsList( itemCod, reviewList, mAdapter );
    }

}