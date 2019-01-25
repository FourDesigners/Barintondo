package it.uniba.di.sms.barintondo.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.uniba.di.sms.barintondo.InterestsListActivity;
import it.uniba.di.sms.barintondo.R;

import static it.uniba.di.sms.barintondo.utils.Constants.imagesPath;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private Context context;
    private List<Review> reviewList;

    private static final String TAG = ReviewAdapter.class.getSimpleName();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, textRecensione;
        public ImageView[] reviewVoteStar;

        public MyViewHolder(View view) {
            super(view);

            userName = view.findViewById( R.id.userName );
            textRecensione = view.findViewById( R.id.text_recensione );

            Resources res = view.getResources();
            for(int i=0; i<5; i++){
                int id = res.getIdentifier("reviewStar"+i, "id", view.getContext().getPackageName());
                reviewVoteStar[i]= view.findViewById( id );
            }
        }
    }


    public ReviewAdapter(Context context, List<Review> reviewList ) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recensione_list_content, parent, false);

        return new ReviewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MyViewHolder holder, final int position) {
        Review review = reviewList.get(position);

        //Log.i(TAG, "entered onBindViewHolder()");
        holder.userName.setText(review.getUserName());
        holder.textRecensione.setText(review.getReviewText());

        for(int i=0; i<review.getVote();i++){
            ImageViewCompat.setImageTintList(
                    holder.reviewVoteStar[i] ,
                    ColorStateList.valueOf( context.getResources().getColor( R.color.colorOrange ) )
            );

        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

}

