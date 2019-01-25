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

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Evento> itemList;
    private List<Evento> itemListFiltered;
    private ItemsAdapterListener listener;

    private static final String TAG = EventoAdapter.class.getSimpleName();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cod, nome, dataInizio, citta;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);

            cod = view.findViewById(R.id.cod);
            nome = view.findViewById(R.id.nome);
            dataInizio = view.findViewById(R.id.dataInizio);
            citta = view.findViewById(R.id.citta);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected Evento in callback
                    listener.onItemsSelected(itemListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public EventoAdapter(Context context, List<Evento> itemList, ItemsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.itemList = itemList;
        this.itemListFiltered = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.evento_list_content, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Evento Evento = itemListFiltered.get(position);

        //thumbnail
        Glide.with(context)
                .load(imagesPath + Evento.getThumbnailLink())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);

        holder.cod.setText(Evento.getCod());
        holder.nome.setText(Evento.getNome());
        holder.dataInizio.setText(Evento.getDataInizio());
        holder.citta.setText(Evento.getCitta());
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
                    List<Evento> filteredList = new ArrayList<>();
                    for (Evento row : itemList) {

                        // name match condition
                            /*Log.i(TAG, "Compare: Nome=" + row.getNome() + " sottocat=" + row.getSottoCat() + " query=" + charString
                            + " esito=" + row.getSottoCat().toLowerCase().contains(charString.toLowerCase()));*/
                        if (row.getNome().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getCitta().toLowerCase().contains(charString.toLowerCase())) {
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
                itemListFiltered = (ArrayList<Evento>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemsAdapterListener {
        void onItemsSelected(Evento item);
    }



}
