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

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.barintondo.utils.BarintondoContent;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder>
            implements Filterable {
        private Context context;
        private List<BarintondoContent.BarintondoItem> itemList;
        private List<BarintondoContent.BarintondoItem> itemListFiltered;
        private ItemsAdapterListener listener;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView id, name;
            //public ImageView thumbnail;

            public MyViewHolder(View view) {
                super(view);
                id = view.findViewById(R.id.item_id);
                name = view.findViewById(R.id.item_name);
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


        public ItemsAdapter(Context context, List<BarintondoContent.BarintondoItem> itemList, ItemsAdapterListener listener) {
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
            final BarintondoContent.BarintondoItem barintondoItem = itemListFiltered.get(position);
            holder.id.setText(barintondoItem.getId());
            holder.name.setText(barintondoItem.getName());

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
                        List<BarintondoContent.BarintondoItem> filteredList = new ArrayList<>();
                        for (BarintondoContent.BarintondoItem row : itemList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
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
                    itemListFiltered = (ArrayList<BarintondoContent.BarintondoItem>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        public interface ItemsAdapterListener {
            void onItemsSelected(BarintondoContent.BarintondoItem item);
        }
}
