package it.uniba.di.sms.barintondo.utils;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
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

import it.uniba.di.sms.barintondo.EventoDetailActivity;
import it.uniba.di.sms.barintondo.InterestsListActivity;
import it.uniba.di.sms.barintondo.LuogoDetailActivity;
import it.uniba.di.sms.barintondo.R;

public class LuogoAdapter extends RecyclerView.Adapter<LuogoAdapter.MyViewHolder>
        implements Filterable, Constants {
    private String TAG_CLASS = getClass().getSimpleName();
    private Context context;
    private List<Luogo> itemList;
    private List<Luogo> itemListFiltered;
    private MyListeners.ItemsAdapterListener listener;
    private Location sourceLuogo;
    private boolean isRequestFormLuogoDetail;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nome, sottoCat, stato, startDays, distance;
        public ImageView thumbnail, categIcon;
        public FrameVoteStars mVoteStars;

        MyViewHolder(View view) {
            super( view );

            nome = view.findViewById( R.id.nome );
            sottoCat = view.findViewById( R.id.sottoCat );
            stato = view.findViewById( R.id.stato );
            thumbnail = view.findViewById( R.id.thumbnail );
            categIcon = view.findViewById( R.id.icon_categoria );
            View frameVote = view.findViewById( R.id.luogoVoteLayout );
            mVoteStars = new FrameVoteStars( frameVote );
            startDays = view.findViewById( R.id.start_days );
            distance = view.findViewById( R.id.text_distance );

            view.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected Luogo in callback
                    listener.onItemsSelected( itemListFiltered.get( getAdapterPosition() ) );
                }
            } );
        }
    }


    public LuogoAdapter(Context context , List<Luogo> itemList , MyListeners.ItemsAdapterListener listener) {
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

        ViewCompat.setTransitionName(holder.thumbnail, "transition"+position);

        //scelgo stringa per sottoCat
        String sottoCat = "";
        //Log.i( TAG , TAG_CLASS+ "sottoCatItem=" + luogo.getSottoCat() + "-" );
        switch (luogo.getSottoCat()) {
            case "Teatri":
                sottoCat = context.getResources().getString( R.string.strTheatre );
                break;
            case "Monumenti":
                sottoCat = context.getResources().getString( R.string.strMonument );
                break;
            case "Musei":
                sottoCat = context.getResources().getString( R.string.strMuseum );
                break;
            case "Chiese":
                sottoCat = context.getResources().getString( R.string.strChurch );
                break;
            case "Lidi":
                sottoCat = context.getResources().getString( R.string.strBeach );
                break;
            case "Discoteche":
                sottoCat = context.getResources().getString( R.string.strDisco );
                break;
            case "Famiglia":
                sottoCat = context.getResources().getString( R.string.strFamiglia );
                break;
            case "Bar":
                sottoCat = context.getResources().getString( R.string.strBar );
                break;
            case "Pizzerie":
                sottoCat = context.getResources().getString( R.string.strPizzaHouse );
                break;
            case "Ristoranti":
                sottoCat = context.getResources().getString( R.string.strRestaurant );
                break;
            case "Hotel":
                sottoCat = context.getResources().getString( R.string.strHotel );
                break;
            case "B&B":
                sottoCat = context.getResources().getString( R.string.strBB );
                break;
            case "Bari":
                sottoCat = context.getResources().getString( R.string.strBari );
                break;
            case "Fuori":
                sottoCat = luogo.getCitta();
                break;
            default:
                sottoCat = "Default";
                break;
        }
        //Log.i( TAG , TAG_CLASS+" final sottoCat:" + sottoCat );
        if (!luogo.getCitta().equals( "Bari" ) && !(luogo instanceof Evento)) {
            // se la cittè non è bari allora inserisci anche il nome della città
            sottoCat = context.getResources().getString( R.string.placeholderCommaSeparator , luogo.getCitta() , sottoCat );
        }
        holder.sottoCat.setText( sottoCat );


        if (!isRequestFormLuogoDetail && luogo.getOraA() != null && !(luogo instanceof Evento)) {
            // la prima condizione serve a capire se siamo nell'activity LuogoList o nella sezione "Vicino" del dettaglio luogo
            //la seconda condizione serve per non fare il calcolo qualora l'informazione dell'orario non sia disponibile
            //la terza condizione serve perchè per gli eventi viene considerata l'informazione sulla data e non sull'ora
            holder.sottoCat.setText( context.getResources().getString( R.string.placeholderCommaSeparator , sottoCat , "" ) );
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
            holder.stato.setVisibility( View.VISIBLE );
        } else {
            //impostare la visibilità serve perchè la vista ricicla le righe, quindi se non messa a visibility gone
            //si potrebbero portare dietro informazioni di un'altro luogo
            holder.stato.setVisibility( View.GONE );
        }
        //Codice per assegnare l'icona dela corrispondente categoria,
        //attivato solo dentro l'activity degli interessi e nella sezione vicino del dettaglio Luogo-Evento
        int icon = R.drawable.ic_fiber_smart_record; //icona di default assegnata nel caso andasse storto qualcosa
        if (context instanceof InterestsListActivity || context instanceof LuogoDetailActivity || context instanceof EventoDetailActivity) {
            switch (luogo.getCategoria()) {
                case "Attrazione":
                    icon = R.drawable.ic_attraction;
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
            holder.categIcon.setColorFilter( context.getColor( R.color.colorSecondaryVariant ) );
        } else {//se non siamo nelle activity trattate ptima, cioè siamo in una normale lista di luoghi
            //Se il luogo è tra i preferiti aggiunge una stella

            if (UserUtils.codPref.contains( luogo.getCod() )) {
                holder.categIcon.setImageDrawable( context.getDrawable( R.drawable.ic_star ) );
                holder.categIcon.setColorFilter( context.getColor( R.color.colorSecondaryVariant ) );
            } else holder.categIcon.setImageDrawable( null );
            //serve per eliminare la stella perchè la recycler view la ricicla a un certo punto dello scorrimento
        }
        // se l'elemento trattato è un evento, esegue il cast e imposta le informazioni aggiuntive
        if (luogo instanceof Evento) {
            Evento evento = (Evento) luogo;
            holder.mVoteStars.hideVoteFrame();
            holder.startDays.setVisibility( View.VISIBLE );
            holder.startDays.setText( evento.getDaysToEventString( context ) );

        } else {
            holder.startDays.setVisibility( View.INVISIBLE ); //usare invisible perchè usando gone modifica i constraint
            holder.mVoteStars.setStars( luogo.getVoto() );
            holder.mVoteStars.showVoteFrame();

            if (UserUtils.myLocationIsSetted && luogo.getLatitudine() != 0.0f && !isRequestFormLuogoDetail) {
                //se non siamo nella sezione "vicino" di un dettaglio, e se la posizone dell'utente è stata caricata
                // calcola la distanza dal luogo
                holder.distance.setText( calculateDistance( luogo , UserUtils.myLocation ) );
                holder.distance.setVisibility( View.VISIBLE );
            } else if (isRequestFormLuogoDetail && luogo.getLatitudine() != 0.0f) {
                // se siamo nella sezione "Vicino" di un dettaglio, calcola la distanza tra il luogo attualmente analizzato,
                // e il luogo di cui si sta visualizzando il dettaglio
                holder.distance.setText( calculateDistance( luogo , sourceLuogo ) );
                holder.distance.setVisibility( View.VISIBLE );
            } else holder.distance.setVisibility( View.GONE );
            // se nessuna delle condizioni è vera non si può ottenere informazioni sulla distanza, quindi disattiva la tetxView
        }


    }


    private String calculateDistance(Luogo actualLuogo , Location startLocation) {
        // calcola la distanza tra il luogo e una posizione
        String distanceText;
        int distanceMeters = actualLuogo.calculateDistanceTo( startLocation );
        if ((distanceMeters / 1000) != 0) {
            // se la distanza è >1000 la converte in chilometri
            float distanceKm = (float) distanceMeters / 1000;
            distanceText = context.getString( R.string.strDistanceKilometers , distanceKm );
        } else distanceText = context.getString( R.string.strDistanceMeters , distanceMeters );
        // altrimenti la mostra in metri

        return distanceText;
    }


    @Override
    public int getItemCount() {
        return itemListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        //filtri per sottocategorie
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
                            /*Log.i(TAG, TAG_CLASS+" Compare: Nome=" + row.getNome() + " sottocat=" + row.getSottoCat() + " query=" + charString
                            + " esito=" + row.getSottoCat().toLowerCase().contains(charString.toLowerCase()));*/
                        if (row.getNome().toLowerCase().contains( charString.toLowerCase() ) ||
                                row.getSottoCat().toLowerCase().contains( charString.toLowerCase() ) ||
                                row.getCitta().toLowerCase().contains( charString.toLowerCase() )) {
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

    public Filter getFilterCategories() {
        //filtri per categorie
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemListFiltered = itemList;
                } else {
                    List<Luogo> filteredList = new ArrayList<>();
                    for (Luogo row : itemList) {
                        //Attrazione e Vicino trattati in maniera particolare perchè hanno le stesse sottocategorie
                        if (charSequence.equals( "Attrazione" )) { //attrazioni con città uguale a bari
                            if (row.getCategoria().toLowerCase().contains( "attrazione" ) && row.getCitta().equals( "Bari" )) {
                                filteredList.add( row );
                            }
                        } else if (charSequence.equals( "Vicino" )) { //vicino trattato in particolare perchè sono attrazioni con città diversa da bari
                            if (row.getCategoria().toLowerCase().contains( "attrazione" ) && !row.getCitta().equals( "Bari" )) {
                                filteredList.add( row );
                            }
                        } else if (row.getCategoria().toLowerCase().contains( charString.toLowerCase() )) {
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

    public void setActualLuogo(Luogo luogo) {
        // imposta il luogo di cui si sta attualmente visualizzando un dettaglio per calcolarne la distanza dopo
        Location location = new Location( "" );
        location.setLatitude( luogo.getLatitudine() );
        location.setLongitude( luogo.getLongitudine() );
        this.sourceLuogo = location;
    }

    public void setIsRequestFormLuogoDetail(boolean requestFormLuogoDetail) {
        // la variabile conterrà vero solo se siamo dentro la sezione "vicino" di un dettaglio luogo
        isRequestFormLuogoDetail = requestFormLuogoDetail;
    }
}
