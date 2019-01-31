package it.uniba.di.sms.barintondo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.barintondo.InterestsListActivity;
import it.uniba.di.sms.barintondo.R;

import static it.uniba.di.sms.barintondo.utils.Constants.imagesPath;

public class LuogoAdapter extends RecyclerView.Adapter<LuogoAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Luogo> itemList;
    private List<Luogo> itemListFiltered;
    private ItemsAdapterListener listener;

    private static final String TAG = LuogoAdapter.class.getSimpleName();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nome, sottoCat, stato, startDays;
        public ImageView thumbnail, categIcon;
        public FrameVoteStars mVoteStars;

        public MyViewHolder(View view) {
            super( view );

            nome = view.findViewById( R.id.nome );
            sottoCat = view.findViewById( R.id.sottoCat );
            stato = view.findViewById( R.id.stato );
            thumbnail = view.findViewById( R.id.thumbnail );
            categIcon = view.findViewById( R.id.icon_categoria );
            View frameVote = view.findViewById( R.id.luogoVoteLayout );
            mVoteStars = new FrameVoteStars( frameVote );
            startDays = view.findViewById( R.id.start_days );

            view.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected Luogo in callback
                    listener.onItemsSelected( itemListFiltered.get( getAdapterPosition() ) );
                }
            } );
        }
    }


    public LuogoAdapter(Context context , List<Luogo> itemList , ItemsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.itemList = itemList;
        this.itemListFiltered = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {
        View itemView = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.luogo_list_content , parent , false );

        return new MyViewHolder( itemView );
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder , final int position) {

        Luogo luogo = itemListFiltered.get( position );

        //thumbnail
        Glide.with( context )
                .load( imagesPath + luogo.getThumbnailLink() )
                .apply( RequestOptions.circleCropTransform() )
                .into( holder.thumbnail );

        holder.nome.setText( luogo.getNome() );

        //scelgo stringa per sottoCat
        String sottoCat = "";
        Log.i( TAG , "sottoCatItem=" + luogo.getSottoCat() + "-" );
        switch (luogo.getSottoCat()) {
            case "Teatri":
                sottoCat += context.getResources().getString( R.string.strTheatre );
                break;
            case "Monumenti":
                sottoCat += context.getResources().getString( R.string.strMonument );
                break;
            case "Musei":
                sottoCat += context.getResources().getString( R.string.strMuseum );
                break;
            case "Chiese":
                sottoCat += context.getResources().getString( R.string.strChurch );
                break;
            case "Lidi":
                sottoCat += context.getResources().getString( R.string.strBeach );
                break;
            case "Discoteche":
                sottoCat += context.getResources().getString( R.string.strDisco );
                break;
            case "Famiglia":
                sottoCat +=context.getResources().getString( R.string.strFamiglia );
                break;
            case "Bar":
                sottoCat += context.getResources().getString( R.string.strBar );
                break;
            case "Pizzerie":
                sottoCat += context.getResources().getString( R.string.strPizzaHouse );
                break;
            case "Ristoranti":
                sottoCat += context.getResources().getString( R.string.strRestaurant );
                break;
            case "Hotel":
                sottoCat += context.getResources().getString( R.string.strHotel );
                break;
            case "B&B":
                sottoCat += context.getResources().getString( R.string.strBB );
                break;
            case "Bari":
                sottoCat += context.getResources().getString( R.string.strBari );
                break;
            case "Fuori":
                sottoCat += luogo.getCitta();
                break;
            default:
                sottoCat += "Default";
                break;
        }
        Log.i( TAG , "final sottoCat:" + sottoCat );
        holder.sottoCat.setText( sottoCat );


        if (luogo.getOraA() != null && !(luogo instanceof Evento)) {
            //controllo se il luogo è "aperto" o "chiuso"
            String oraA = luogo.getOraA();
            String oraC = luogo.getOraC();

            //ottengo ora attuale
            Date date = new Date();
            String strDateFormat = "HH:mm:ss";
            DateFormat dateFormat = new SimpleDateFormat( strDateFormat );
            String formattedDate = dateFormat.format( date );

            //confronto i dati
            if (formattedDate.compareTo( oraA ) < 0 || formattedDate.compareTo( oraC ) > 0)
                holder.stato.setText( context.getResources().getString( R.string.closedState ) );
            else holder.stato.setText( context.getResources().getString( R.string.openState ) );
        } else {
            holder.stato.setVisibility( View.GONE );
        }
        //Codice per assegnare l'icona dela corrispondente categoria, attivato solo dentro l'activity degli interessi
        int icon = R.drawable.ic_fiber_smart_record; //icona di default assegnata nel caso andasse storto qualcosa
        if (context instanceof InterestsListActivity) {
            switch (luogo.getCategoria()) {
                case "Attrazione":
                    if(luogo.getCitta().equals( "Bari" )) {
                        icon = R.drawable.ic_attraction;
                    } else icon = R.drawable.ic_surroundings;
                    break;
                case "Dormire":
                    icon = R.drawable.ic_stay;
                    break;
                case "Mangiare":
                    icon = R.drawable.ic_food;
                    break;
                case "Evento":
                    icon = R.drawable.ic_events;
                    break;
            }
            holder.categIcon.setImageDrawable( context.getDrawable( icon ) );
        } else {//se non siamo nella lista dei preferidi, cioè siamo in una normale lista di luoghi
            //Se il luogo è tra i preferiti aggiunge una stella

            if (UserUtils.codPref.contains( luogo.getCod() )) {
                holder.categIcon.setImageDrawable( context.getDrawable( R.drawable.ic_star ) );
                holder.categIcon.setColorFilter( context.getColor( R.color.colorSecondaryBlue ) );
            }
            else holder.categIcon.setImageDrawable( null );
            //serve per eliminare la stella perchè la recycler view la ricicla a un certo punto dello scorrimento
        }

        if(luogo instanceof Evento){
            Evento evento = (Evento) luogo;
            holder.mVoteStars.hideVoteFrame();

            holder.startDays.setText(evento.getDaysToEventString( context ));

        }else holder.mVoteStars.setStars( luogo.getVoto() );


    }

    @Override
    public int getItemCount() {
        return itemListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemListFiltered = itemList;
                } else {
                    List<Luogo> filteredList = new ArrayList<>();
                    for (Luogo row : itemList) {

                        // name match condition
                            /*Log.i(TAG, "Compare: Nome=" + row.getNome() + " sottocat=" + row.getSottoCat() + " query=" + charString
                            + " esito=" + row.getSottoCat().toLowerCase().contains(charString.toLowerCase()));*/
                        if (row.getNome().toLowerCase().contains( charString.toLowerCase() ) ||
                                row.getSottoCat().toLowerCase().contains( charString.toLowerCase() )) {
                            filteredList.add( row );
                        }
                    }

                    itemListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence , FilterResults filterResults) {
                itemListFiltered = (ArrayList<Luogo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemsAdapterListener {
        void onItemsSelected(Luogo item);
    }


}
