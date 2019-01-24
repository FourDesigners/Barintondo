package it.uniba.di.sms.barintondo.utils;

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
            public TextView cod, nome, sottoCat, stato, valutazione;
            public ImageView thumbnail, categIcon;

            public MyViewHolder(View view) {
                super(view);

                cod = view.findViewById(R.id.cod);
                nome = view.findViewById(R.id.nome);
                sottoCat = view.findViewById(R.id.sottoCat);
                stato = view.findViewById(R.id.stato);
                valutazione = view.findViewById(R.id.valutazione);
                thumbnail = view.findViewById(R.id.thumbnail);
                categIcon = view.findViewById( R.id.icon_categoria );

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // send selected Luogo in callback
                        listener.onItemsSelected(itemListFiltered.get(getAdapterPosition()));
                    }
                });
            }
        }


        public LuogoAdapter(Context context, List<Luogo> itemList, ItemsAdapterListener listener) {
            this.context = context;
            this.listener = listener;
            this.itemList = itemList;
            this.itemListFiltered = itemList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.luogo_list_content, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            Luogo luogo = itemListFiltered.get(position);

            //thumbnail
            Glide.with(context)
                    .load(imagesPath + luogo.getThumbnailLink())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.thumbnail);

            holder.cod.setText(luogo.getCod());
            holder.nome.setText(luogo.getNome());

            //scelgo stringa per sottoCat
            String sottoCat = "";
            Log.i(TAG, "sottoCatItem=" + luogo.getSottoCat() + "-");
            switch (luogo.getSottoCat()) {
                case "Teatri":
                    sottoCat += context.getResources().getString(R.string.strTheatre);
                    break;
                case "Monumenti":
                    sottoCat += context.getResources().getString(R.string.strMonument);
                    break;
                case "Musei":
                    sottoCat += context.getResources().getString(R.string.strMuseum);
                    break;
                case "Chiese":
                    sottoCat += context.getResources().getString(R.string.strChurch);
                    break;
                case "Lidi":
                    sottoCat += context.getResources().getString(R.string.strBeach);
                    break;
                case "Discoteche":
                    sottoCat += context.getResources().getString(R.string.strDisco);
                    break;
                case "Bar":
                    sottoCat += context.getResources().getString(R.string.strBar);
                    break;
                case "Pizzerie":
                    sottoCat += context.getResources().getString(R.string.strPizzaHouse);
                    break;
                case "Ristoranti":
                    sottoCat += context.getResources().getString(R.string.strRestaurant);
                    break;
                case "Hotel":
                    sottoCat += context.getResources().getString(R.string.strHotel);
                    break;
                case "B&B":
                    sottoCat += context.getResources().getString(R.string.strBB);
                    break;
                default:
                    sottoCat +="Default";
                    break;
            }
            Log.i(TAG, "final sottoCat:" + sottoCat);
            holder.sottoCat.setText(sottoCat);

            //controllo se il luogo è "aperto" o "chiuso"
            String oraA = luogo.getOraA();
            String oraC = luogo.getOraC();

            //ottengo ora attuale
            Date date = new Date();
            String strDateFormat = "HH:mm:ss";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String formattedDate= dateFormat.format(date);

            //confronto i dati
            if(formattedDate.compareTo(oraA) < 0 || formattedDate.compareTo(oraC) > 0)
                holder.stato.setText(context.getResources().getString(R.string.closedState));
            else holder.stato.setText(context.getResources().getString(R.string.openState));

            //Codice per assegnare l'icona dela corrispondente categoria, attivato solo dentro l'activity degli interessi
            int icon=R.drawable.ic_fiber_smart_record; //icona di default assegnata nel caso andasse storto qualcosa
            if(context instanceof InterestsListActivity){
                switch (luogo.getCategoria()){
                    case "Attrazione":
                        icon = R.drawable.ic_attractions;
                        break;
                    case "Dormire":
                        icon= R.drawable.ic_stay;
                        break;
                    case "Mangiare":
                        icon = R.drawable.ic_food;
                        break;
                }
                holder.categIcon.setImageDrawable( context.getDrawable( icon ) );
            }

            //holder.valutazione.setText(luogo.get());

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
                            if (row.getNome().toLowerCase().contains(charString.toLowerCase()) ||
                                    row.getSottoCat().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        itemListFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = itemListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    itemListFiltered = (ArrayList<Luogo>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        public interface ItemsAdapterListener {
            void onItemsSelected(Luogo item);
        }



}
