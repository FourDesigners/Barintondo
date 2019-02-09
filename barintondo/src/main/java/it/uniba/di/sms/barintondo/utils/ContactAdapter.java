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

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Contact> itemList;
    private List<Contact> itemListFiltered;
    private ItemsAdapterListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView servizio, number;

        public MyViewHolder(View view) {
            super(view);

            servizio = view.findViewById(R.id.servizio);
            number = view.findViewById(R.id.number);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected Contact in callback
                    listener.onItemsSelected(itemListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public ContactAdapter(Context context, List<Contact> itemList, ItemsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.itemList = itemList;
        this.itemListFiltered = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_content, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Contact Contact = itemListFiltered.get(position);
        holder.servizio.setText(Contact.getServizio());
        holder.number.setText(Contact.getNumero());
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
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : itemList) {
                        if (row.getServizio().toLowerCase().contains(charString.toLowerCase())) {
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
                itemListFiltered = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemsAdapterListener {
        void onItemsSelected(Contact item);
    }



}
