package it.uniba.di.sms.barintondo.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import it.uniba.di.sms.barintondo.EventoDetailActivity;
import it.uniba.di.sms.barintondo.R;

public class SliderAdapter extends PagerAdapter implements Constants {

    private LayoutInflater inflater;
    private Activity activity;
    private ArrayList<Evento> eventList;

    public SliderAdapter(Activity activity, ArrayList<Evento> eventList) {
        this.activity = activity;
        this.eventList=eventList;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View myImageLayout = inflater.inflate(R.layout.slider_image_content, view, false);
        myImageLayout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( activity, EventoDetailActivity.class );
                intent.putExtra( INTENT_LUOGO_COD, eventList.get( position ).getCod() );
                activity.startActivity(intent);
            }
        } );
        ImageView myImage = myImageLayout.findViewById(R.id.image);
        TextView eventTitle = myImageLayout.findViewById( R.id.text_slider_event_title );
        TextView eventDays = myImageLayout.findViewById( R.id.slider_event_days );
        String daysToEvent= eventList.get( position ).getDaysToEventString( myImageLayout.getContext() );
        eventDays.setText( activity.getString( R.string.placeholderCittaDaysToevent, eventList.get( position ).getCitta(), daysToEvent.toLowerCase() ) );
        eventTitle.setText( eventList.get( position ).getNome() );
        Glide.with( activity )
                .load( imagesPath + eventList.get( position ).getThumbnailLink() )
                .into( myImage );
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}