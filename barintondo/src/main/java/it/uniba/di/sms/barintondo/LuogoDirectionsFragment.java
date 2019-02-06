package it.uniba.di.sms.barintondo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import it.uniba.di.sms.barintondo.utils.Constants;
import it.uniba.di.sms.barintondo.utils.MyListners;
import it.uniba.di.sms.barintondo.utils.ControllerRemoteDB;
import it.uniba.di.sms.barintondo.utils.Evento;
import it.uniba.di.sms.barintondo.utils.Luogo;
import it.uniba.di.sms.barintondo.utils.LuogoAdapter;
import it.uniba.di.sms.barintondo.utils.MyDividerItemDecoration;

public class LuogoDirectionsFragment extends Fragment implements Constants {
    private String TAG_CLASS = getClass().getSimpleName();
    Luogo myLuogo;
    RecyclerView recyclerView;
    ArrayList<Luogo> luogoList;
    LuogoAdapter mAdapter;
    MyListners.ItemsAdapterListener itemsAdapterListener;
    MyListners.LuoghiList myDBListner;
    String requestCat;
    TextView textNoLuoghiNear;

    public LuogoDirectionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        myLuogo = getArguments().getParcelable( EXTRA_LUOGO );
        requestCat = REQUEST_GET_ATTRACTIONS;

        itemsAdapterListener = new MyListners.ItemsAdapterListener() {
            @Override
            public void onItemsSelected(Luogo item) {
                Intent intent;
                if (item instanceof Evento) {
                    intent = new Intent( getContext() , EventoDetailActivity.class );
                } else {
                    intent = new Intent( getContext() , LuogoDetailActivity.class );
                }
                intent.putExtra( INTENT_LUOGO_COD , item.getCod() );
                startActivity( intent );
                getActivity().overridePendingTransition(R.anim.slide_in,  R.anim.slide_out);
            }
        };

        luogoList = new ArrayList<>();
        mAdapter = new LuogoAdapter( getContext() , luogoList , itemsAdapterListener );
        if(myLuogo.getLatitudine()!=0.0f) {
            mAdapter.setActualLuogo( myLuogo );
            mAdapter.setIsRequestFormLuogoDetail( true );
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate( R.layout.fragment_luogo_directions , container , false );
        textNoLuoghiNear = rootView.findViewById( R.id.text_view_no_luoghi_near );
        //recyclerView setup
        recyclerView = rootView.findViewById( R.id.item_list_recycler_view );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( getContext() );
        recyclerView.setLayoutManager( mLayoutManager );
        recyclerView.addItemDecoration( new MyDividerItemDecoration( getContext() , DividerItemDecoration.VERTICAL , 36 ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( mAdapter );

        myDBListner = new MyListners.LuoghiList() {
            @Override
            public void onList() {
                Collections.sort( luogoList, Luogo.getDistanceOrdering() );
                mAdapter.notifyDataSetChanged();
                if (luogoList.size()==0){
                    textNoLuoghiNear.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public void onError(String error) {
                switch (error){
                    case VOLLEY_ERROR_JSON:
                        Log.i(TAG, TAG_CLASS + ": entered luogoListnerOnError, error in pharsing the Json recieved from server");
                        break;
                    case VOLLEY_ERROR_CONNECTION:
                        Log.i(TAG, TAG_CLASS + ": entered luogoListnerOnError, error on the server");
                        break;
                }
            }
        };


        //first time populating
        //fetchItems();
        ControllerRemoteDB controller = new ControllerRemoteDB( getContext() );
        controller.getLuoghiNear( myLuogo , luogoList , myDBListner );


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}