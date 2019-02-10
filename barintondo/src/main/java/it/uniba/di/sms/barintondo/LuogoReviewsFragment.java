package it.uniba.di.sms.barintondo;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;
import it.uniba.di.sms.barintondo.utils.MyListeners;
import it.uniba.di.sms.barintondo.utils.Review;
import it.uniba.di.sms.barintondo.utils.ReviewAdapter;

public class LuogoReviewsFragment extends Fragment implements Constants {

    private String TAG_CLASS = getClass().getSimpleName();
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
    CoordinatorLayout reviewLayout;
    MyListeners.ReviewsList mReviewListListener;
    MyListeners.ReviewSave mReviewSaveListener;
    final String REVIEW_OPTION_SELECTED = "ReviewOptionSelected";
    private int REVIEW_LIST = 1;
    private int MY_REVIEW = 2;
    public int activeOpion;
    final String MY_REVIEW_VOTE_STAR = "MyReviewVoteStar";
    private int myReviewVoteStar;
    final String MY_REVIEW_WRITTEN_TEXT = "MyReviewTexe";
    private String myReviewText;

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
            reviewList = new ArrayList<>();
            yourReviewVoteStar = new ImageView[5];
            mLuogoReviewFragment = this;
            controllerRemoteDB = new ControllerRemoteDB( this.getContext() );

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater , final ViewGroup container ,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate( R.layout.fragment_luogo_reviews , container , false );

        if (savedInstanceState != null) {
            activeOpion = savedInstanceState.getInt( REVIEW_OPTION_SELECTED );
            myReviewText = savedInstanceState.getString( MY_REVIEW_WRITTEN_TEXT , "" );
            myReviewVoteStar = savedInstanceState.getInt( MY_REVIEW_VOTE_STAR , 1 );
        } else {
            myReviewVoteStar=1;
            activeOpion = 1;
        }

        Log.i( TAG , TAG_CLASS + ": entered onCreateView(), activeOption=" + activeOpion+ "myReviewVoteStar= "+myReviewVoteStar );

        mAdapter = new ReviewAdapter( rootView , reviewList );
        //recyclerView setup
        recyclerView = rootView.findViewById( R.id.luogo_reviews_list_recycler_view );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( rootView.getContext() );
        recyclerView.addItemDecoration( new MyDividerItemDecoration( rootView.getContext() , DividerItemDecoration.VERTICAL , 36 ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.setAdapter( mAdapter );

        layoutYourReview = rootView.findViewById( R.id.layout_your_review );
        layourReviewsList = rootView.findViewById( R.id.layout_reviews_list );
        fabAdReview = rootView.findViewById( R.id.fabAddReview );
        btnCancelReview = rootView.findViewById( R.id.btnCancelReview );
        btnSaveReview = rootView.findViewById( R.id.btnSaveReview );
        reviewEditText = rootView.findViewById( R.id.edit_text_review );
        reviewLayout = rootView.findViewById( R.id.layout_reviews_list );

        fabAdReview.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateSection( 2 );
            }
        } );

        btnCancelReview.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateSection( 1 );
            }
        } );

        final Resources res = rootView.getResources();
        for (int i = 0; i < 5; i++) {
            int id = res.getIdentifier( "yourReviewStar" + String.valueOf( i ) , "id" , rootView.getContext().getPackageName() );
            yourReviewVoteStar[i] = rootView.findViewById( id );
            final int j = i + 1;
            yourReviewVoteStar[i].setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    starSelected( j , res );
                    myReviewVoteStar=j;
                }
            } );
        }


        mReviewListListener = new MyListeners.ReviewsList() {
            @Override
            public void onReviewList() {
                if (activeOpion == 1) {
                    if (reviewList.size() == 0) {
                        recyclerView.setVisibility( View.GONE );
                        rootView.findViewById( R.id.text_view_no_reviews ).setVisibility( View.VISIBLE );
                    } else {
                        recyclerView.setVisibility( View.VISIBLE );
                        rootView.findViewById( R.id.text_view_no_reviews ).setVisibility( View.GONE );
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }


            @Override
            public void onError(String error) {
                switch (error) {
                    case VOLLEY_ERROR_JSON:
                        Log.i( TAG , TAG_CLASS + ": entered listenerReviewListOnError, error in pharsing the Json recieved from server" );
                        break;
                    case VOLLEY_ERROR_CONNECTION:
                        Log.i( TAG , TAG_CLASS + ": entered listenerReviewOnError, error on the server" );
                        break;
                }
            }
        };

        activateSection( activeOpion );
        starSelected( myReviewVoteStar , res );
        reviewEditText.setText( myReviewText );
        controllerRemoteDB.getReviewsList( itemCod , reviewList , mReviewListListener );

        mReviewSaveListener = new MyListeners.ReviewSave() {
            @Override
            public void onReviewAdded() {
                layourReviewsList.setVisibility( View.VISIBLE );
                layoutYourReview.setVisibility( View.GONE );
                activeOpion=1;
                controllerRemoteDB.getReviewsList( itemCod , reviewList , mReviewListListener );
                //ripristino della sezione di invio segnalazione
                reviewEditText.getText().clear();
                final Resources res = getContext().getResources();
                starSelected( 1 , res );
                reviewLayout.setVisibility( View.VISIBLE );
            }

            @Override
            public void onError(String error) {
                Snackbar.make( rootView.findViewById( R.id.drawer_layout ) ,
                        getResources().getString( R.string.str_fail_save_review ) ,
                        Snackbar.LENGTH_LONG )
                        .setAction( "Action" , null ).show();
            }
        };
        btnSaveReview.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllerRemoteDB.saveReview( reviewEditText.getText().toString() , itemCod , yourVote , mReviewSaveListener );
            }
        } );
        return rootView;
    }

    private void activateSection(int section) {
        activeOpion = section;
        switch (section) {
            case 1:
                layourReviewsList.setVisibility( View.VISIBLE );
                layoutYourReview.setVisibility( View.GONE );
                break;
            case 2:
                layourReviewsList.setVisibility( View.GONE );
                layoutYourReview.setVisibility( View.VISIBLE );
                break;
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i( TAG , TAG_CLASS + ": entered onSavedInstanceState, activeOption=" + activeOpion );
        super.onSaveInstanceState( outState );
        outState.putInt( REVIEW_OPTION_SELECTED , activeOpion );
        if (!reviewEditText.getText().toString().equals( "" )) {
            outState.putInt( MY_REVIEW_VOTE_STAR , myReviewVoteStar );
            outState.putString( MY_REVIEW_WRITTEN_TEXT , myReviewText );
        }
    }


    /*
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            activeOpion = savedInstanceState.getInt( REVIEW_OPTION_SELECTED );
            myReviewText = savedInstanceState.getString( MY_REVIEW_WRITTEN_TEXT , "" );
            myReviewVoteStar = savedInstanceState.getInt( MY_REVIEW_VOTE_STAR , 1 );
        } else {
            myReviewVoteStar=1;
            activeOpion = 1;
        }
        activateSection(activeOpion);
    }
    */

    private void starSelected(int j , Resources res) {
        yourVote = j;
        int i;
        for (i = 0; i < j; i++) {
            ImageViewCompat.setImageTintList(
                    yourReviewVoteStar[i] ,
                    ColorStateList.valueOf( res.getColor( R.color.colorOrange ) )
            );
        }
        for (; i < 5; i++) {
            ImageViewCompat.setImageTintList(
                    yourReviewVoteStar[i] ,
                    ColorStateList.valueOf( res.getColor( R.color.colorBlack ) )
            );
        }
    }

}