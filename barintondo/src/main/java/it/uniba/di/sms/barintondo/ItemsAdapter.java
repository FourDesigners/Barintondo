package it.uniba.di.sms.barintondo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.uniba.di.sms.barintondo.utils.BarintondoItem;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder>
            implements Filterable {
        private Context context;
        private List<BarintondoItem> itemList;
        private List<BarintondoItem> itemListFiltered;
        private ItemsAdapterListener listener;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView cod, nome, sottoCat, stato, valutazione;
            //public ImageView thumbnail;

            public MyViewHolder(View view) {
                super(view);

                cod = view.findViewById(R.id.cod);
                nome = view.findViewById(R.id.nome);
                sottoCat = view.findViewById(R.id.sottoCat);
                stato = view.findViewById(R.id.stato);
                valutazione = view.findViewById(R.id.valutazione);
                //thumbnail = view.findViewById(R.id.thumbnail);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // send selected BarintondoContent.BarintondoItem in callback
                        listener.onItemsSelected(itemListFiltered.get(getAdapterPosition()));
                    }
                });
            }
        }


        public ItemsAdapter(Context context, List<BarintondoItem> itemList, ItemsAdapterListener listener) {
            this.context = context;
            this.listener = listener;
            this.itemList = itemList;
            this.itemListFiltered = itemList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            BarintondoItem barintondoItem = itemListFiltered.get(position);

            holder.cod.setText(barintondoItem.getCod());
            holder.nome.setText(barintondoItem.getNome());
            holder.sottoCat.setText(barintondoItem.getSottoCat());

            //controllo se il luogo è "aperto" o "chiuso"
            String oraA = barintondoItem.getOraA();
            String oraC = barintondoItem.getOraC();

            //ottengo ora attuale
            Date date = new Date();
            String strDateFormat = "HH:mm:ss";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String formattedDate= dateFormat.format(date);

            //confronto i dati
            if(formattedDate.compareTo(oraA) < 0 || formattedDate.compareTo(oraC) > 0)
                holder.stato.setText(context.getResources().getString(R.string.closedState));
            else holder.stato.setText(context.getResources().getString(R.string.openState));
            //holder.stato.setText(barintondoItem.get());
            //holder.valutazione.setText(barintondoItem.get());

            /*Glide.with(context)
                    .load(BarintondoContent.BarintondoItem.getImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.thumbnail);*/
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
                        List<BarintondoItem> filteredList = new ArrayList<>();
                        for (BarintondoItem row : itemList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getNome().toLowerCase().contains(charString.toLowerCase())) {
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
                    itemListFiltered = (ArrayList<BarintondoItem>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        public interface ItemsAdapterListener {
            void onItemsSelected(BarintondoItem item);
        }
}
