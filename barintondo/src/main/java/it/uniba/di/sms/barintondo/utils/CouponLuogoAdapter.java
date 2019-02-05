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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import it.uniba.di.sms.barintondo.R;

public class CouponLuogoAdapter extends RecyclerView.Adapter<CouponLuogoAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<CouponLuogo> couponList;
    private List<CouponLuogo> itemListFiltered;
    private ItemsAdapterListener listener;

    private static final String TAG = CouponLuogoAdapter.class.getSimpleName();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView codLuogo, luogo, scadenza;
        public ImageView icona;

        public MyViewHolder(View view) {
            super(view);

            luogo = view.findViewById(R.id.luogo);
            scadenza = view.findViewById(R.id.scadenza);
            icona = view.findViewById(R.id.icona);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected CouponLuogo in callback
                    listener.onItemsSelected(itemListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public CouponLuogoAdapter(Context context, List<CouponLuogo> couponList, ItemsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.couponList = couponList;
        this.itemListFiltered = couponList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coupon_luogo_list_content, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        CouponLuogo couponL = itemListFiltered.get(position);

        //scelgo icona da assegnare
        if(InternetConnection.isNetworkAvailable(context)) {
            HashSet<String> container = new HashSet<>();
            container.addAll(Arrays.asList(context.getResources().getStringArray(R.array.attractions)));
            if (container.contains(couponL.getSottoCat()))
                holder.icona.setImageResource(R.drawable.ic_attractions_coupon);
            else {
                container.clear();
                container.addAll(Arrays.asList(context.getResources().getStringArray(R.array.eating)));
                if (container.contains(couponL.getSottoCat()))
                    holder.icona.setImageResource(R.drawable.ic_eating_coupon);
                else holder.icona.setImageResource(R.drawable.ic_sleeping_coupon);
            }
        }
        else holder.icona.setImageResource(R.drawable.ic_coupon);

        Log.i(TAG, "couponL: " + couponL.getCod() + couponL.getCodLuogo() + couponL.getLuogo());
        holder.luogo.setText(couponL.getLuogo());

        //controllo se il coupon è ancora valido
        String scadenza = couponL.getScadenza();

        Date date = new Date();
        String strDateFormat = "yyyy/mm/dd";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedCurrentDate= dateFormat.format(date);

        //confronto date
        if(scadenza.compareTo(formattedCurrentDate) <= 0)
            holder.scadenza.setText(context.getResources().getString(R.string.validString));
        else holder.scadenza.setText(context.getResources().getString(R.string.expiredString));

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
                    itemListFiltered = couponList;
                } else {
                    List<CouponLuogo> filteredList = new ArrayList<>();
                    for (CouponLuogo row : couponList) {

                        if (row.getLuogo().toLowerCase().contains(charString.toLowerCase())) {
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
                itemListFiltered = (ArrayList<CouponLuogo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemsAdapterListener {
        void onItemsSelected(CouponLuogo item);
    }

}

