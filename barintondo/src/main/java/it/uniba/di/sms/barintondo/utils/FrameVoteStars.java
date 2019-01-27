package it.uniba.di.sms.barintondo.utils;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.provider.MediaStore;
import android.support.v4.widget.ImageViewCompat;
import android.view.View;
import android.widget.ImageView;

import it.uniba.di.sms.barintondo.BuildConfig;
import it.uniba.di.sms.barintondo.R;

public class FrameVoteStars implements Constants{

    private View view;
    private ImageView[] voteStar;


    public FrameVoteStars(View view){
        this.view=view;
        voteStar=new ImageView[5];
        Resources res = view.getResources();
        for(int i=0; i<5; i++){
            int id = res.getIdentifier("Star"+String.valueOf( i ), "id", BuildConfig.APPLICATION_ID);
            voteStar[i]= view.findViewById( id );
        }
    }

    public void setStars(int vote){
        for(int i=0; i<vote;i++){
            ImageViewCompat.setImageTintList(
                    voteStar[i] ,
                    ColorStateList.valueOf( view.getResources().getColor( R.color.colorOrange ) )
            );

        }
    }
}
