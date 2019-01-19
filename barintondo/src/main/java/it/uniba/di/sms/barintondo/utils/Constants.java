package it.uniba.di.sms.barintondo.utils;

public interface Constants {
    String TAG = "Barintondo";
    String INSERTED = "inserted";
    String PREFS_NAME = "BarintondoPrefs";

    //intent per ItemListActivity
    String INTENT_ACTIVITY_ITEM_TYPE = "item_type";
    String INTENT_ATTRACTIONS = "attractions";
    String INTENT_SLEEPING = "sleeping";
    String INTENT_EATING = "eating";
    String INTENT_EVENTS = "events";
    String INTENT_NEAR = "near";

    String CHIPS_ATTRACTIONS = "Chiese, Monumenti, Teatri, Lidi, Discoteche";
    String CHIPS_SLEEPING = "B&B, Hotel";
    String CHIPS_EATING = "Pizzerie, Ristoranti, Bar";
    String CHIPS_EVENTS = "Bari, vicino Bari";

    //costanti DB
    String DB_NAME = "datiUtente.db";
    String TABLE_UTENTE = "utenti";
    String COLUMN_NICKNAME = "nickname";

    String COLUMN_EMAIL = "email";
    String COLUMN_PASSWORD = "password";

    //stringa per link immagini
    String imagesPath = "http://barintondo.altervista.org/images/";

}
