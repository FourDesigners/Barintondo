package it.uniba.di.sms.barintondo.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import it.uniba.di.sms.barintondo.InterestsListActivity;
import it.uniba.di.sms.barintondo.LuogoListActivity;
import it.uniba.di.sms.barintondo.R;

public class ToolbarSwitchCategories implements Constants {
    Activity activity;
    ImageButton attraction, food, sleep, near, event;
    LinearLayoutCompat thisToolbar;


    public ToolbarSwitchCategories(final Activity activity , String category) {
        this.activity = activity;
        thisToolbar=activity.findViewById( R.id.toolbar_switch_categories );

        attraction = activity.findViewById( R.id.btnSwitchAttractions );
        onClickcategory( attraction , INTENT_ATTRACTIONS );
        food = activity.findViewById( R.id.btnSwitchFood );
        onClickcategory( food , INTENT_EATING );
        sleep = activity.findViewById( R.id.btnSwitchSleep );
        onClickcategory( sleep , INTENT_SLEEPING );
        near = activity.findViewById( R.id.btnSwitchNear );
        onClickcategory( near , INTENT_NEAR );
        event=activity.findViewById( R.id.btnSwitchEvent );
        onClickcategory( event, INTENT_EVENTS );

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
            case INTENT_EVENTS:
                event.setColorFilter( color );
        }
    }

    private void onClickcategory(ImageButton iB , final String category) {

        iB.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (activity.getClass()!=LuogoListActivity.class ||
                        !((LuogoListActivity) activity).getItems_type().equals( category )) {
                    Intent intent = new Intent( activity , LuogoListActivity.class );
                    intent.putExtra( Constants.INTENT_ACTIVITY_ITEM_TYPE , category );
                    activity.startActivity( intent );
                }
            }
        } );

    }

    public void hide(){
        thisToolbar.setVisibility( View.GONE );
    }

    public void show(){
        thisToolbar.setVisibility( View.VISIBLE );
    }


}
