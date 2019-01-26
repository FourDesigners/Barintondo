package it.uniba.di.sms.barintondo.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;

import it.uniba.di.sms.barintondo.InterestsListActivity;
import it.uniba.di.sms.barintondo.LuogoListActivity;
import it.uniba.di.sms.barintondo.R;

public class ToolbarSwitchCategories implements Constants {
    Activity activity;
    ImageButton interests, attraction, food, sleep, near;

    public ToolbarSwitchCategories(final Activity activity , String category) {
        this.activity = activity;
        interests= activity.findViewById( R.id.btnSwitchInterests );
        interests.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InterestsListActivity.class);
                activity.startActivity(intent);
            }
        } );
        attraction = activity.findViewById( R.id.btnSwitchAttractions );
        onClickcategory( attraction , INTENT_ATTRACTIONS );
        food = activity.findViewById( R.id.btnSwitchFood );
        onClickcategory( food , INTENT_EATING );
        sleep = activity.findViewById( R.id.btnSwitchSleep );
        onClickcategory( sleep , INTENT_SLEEPING );
        near = activity.findViewById( R.id.btnSwitchNear );
        onClickcategory( near , INTENT_NEAR );

        int color = activity.getResources().getColor( R.color.colorSecondaryBlue );
        switch (category) {
            case INTENT_ATTRACTIONS:
                attraction.setColorFilter( color );
                break;
            case INTENT_EATING:
                food.setColorFilter( color );
                break;
            case INTENT_SLEEPING:
                sleep.setColorFilter( color );
                break;
            case INTENT_NEAR:
                near.setColorFilter( color );
                break;
            case INTENT_INTERESES:
                interests.setColorFilter( color );
        }
    }

    private void onClickcategory(ImageButton iB , final String category) {

        iB.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( activity , LuogoListActivity.class );
                intent.putExtra( Constants.INTENT_ACTIVITY_ITEM_TYPE , category );
                activity.startActivity( intent );
            }
        } );

    }


}
