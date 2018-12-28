package it.uniba.di.sms.barintondo.utils;

import java.util.ArrayList;
import java.util.List;

public class BarintondoContent {

    public static final List<BarintondoItem> BITEMS = new ArrayList<BarintondoItem>();



    public static class BarintondoItem {
        private static int id;
        private static String name;

        public BarintondoItem(int id, String name){
            this.id=id;
            this.name=name;
        }

        public static int getId() {
            return id;
        }

        public static String getName() {
            return name;
        }

    }

}
