package it.uniba.di.sms.barintondo.utils;

import android.location.Location;
import android.location.LocationListener;

import java.util.ArrayList;

public class UserUtils {
    public static ArrayList<String> codPref=new ArrayList<>();

    public static Location myLocation;
    public static boolean myLocationIsSetted=false;


    public static void setMyLocation(double latitude, double longitude){
        myLocation=new Location( "" );
        myLocation.setLatitude( latitude );
        myLocation.setLongitude( longitude );
    }

    public static void setMyLocation(Location location){
        myLocation=new Location( location );
    }
}
