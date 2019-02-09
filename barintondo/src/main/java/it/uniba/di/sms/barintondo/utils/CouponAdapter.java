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
import java.util.Date;
import java.util.List;

import it.uniba.di.sms.barintondo.R;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Coupon> couponList;
    private List<Coupon> itemListFiltered;
    private MyListeners.CouponAdapterListener couponAdapterListener;


    private static final String TAG = CouponAdapter.class.getSimpleName();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView codLuogo, luogo, scadenza;
        public ImageView iconaCategoria;

        public MyViewHolder(View view) {
            super( view );

            luogo = view.findViewById( R.id.luogo );
            scadenza = view.findViewById( R.id.scadenza );
            iconaCategoria = view.findViewById( R.id.icon_categoria );

            view.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected Coupon in callback
                    couponAdapterListener.onItemsSelected( itemListFiltered.get( getAdapterPosition() ) );
                }
            } );
        }
    }


    public CouponAdapter(Context context , List<Coupon> couponList , MyListeners.CouponAdapterListener listener) {
        this.context = context;
        this.couponAdapterListener = listener;
        this.couponList = couponList;
        this.itemListFiltered = couponList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent , int viewType) {
        View itemView = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.coupon_list_content , parent , false );

        return new MyViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder , final int position) {
        Coupon couponL = itemListFiltered.get( position );

        //scelgo icona da assegnare


        Log.i( TAG , "couponL: " + couponL.getCod() + couponL.getCodLuogo() + couponL.getLuogo() );
        holder.luogo.setText( couponL.getLuogo() );

        //controllo se il coupon è ancora valido
        String scadenza = couponL.getScadenza();

        Date date = new Date();
        String strDateFormat = "yyyy/mm/dd";
        DateFormat dateFormat = new SimpleDateFormat( strDateFormat );
        String formattedCurrentDate = dateFormat.format( date );

        //confronto date
        if (scadenza.compareTo( formattedCurrentDate ) <= 0)
            holder.scadenza.setText( context.getResources().getString( R.string.validString ) );
        else holder.scadenza.setText( context.getResources().getString( R.string.expiredString ) );

        int icon=R.drawable.ic_fiber_smart_record;
        switch (couponL.getCategoria()) {
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

        holder.iconaCategoria.setImageDrawable( context.getResources().getDrawable( icon) );
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
                    List<Coupon> filteredList = new ArrayList<>();
                    for (Coupon row : couponList) {

                        if (row.getLuogo().toLowerCase().contains( charString.toLowerCase() )) {
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
                itemListFiltered = (ArrayList<Coupon>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}

