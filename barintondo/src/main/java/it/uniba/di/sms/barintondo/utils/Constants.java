package it.uniba.di.sms.barintondo.utils;

public interface Constants {
    String TAG = "Barintondo";
    String INSERTED = "inserted";
    String PREFS_NAME = "BarintondoPrefs";

    //intent per LuogoListActivity
    String INTENT_ACTIVITY_ITEM_TYPE = "item_type";
    String INTENT_ATTRACTIONS = "attractions";
    String INTENT_SLEEPING = "sleeping";
    String INTENT_EATING = "eating";
    String INTENT_EVENTS = "events";
    String INTENT_NEAR = "near";
    String INTENT_ITEM="item";

    // SharedPreferences
    String ORDER = "order";
    String CHIESE = "Chiese";
    String MONUMENTI = "Monumenti";
    String TEATRI = "Teatri";
    String LIDI = "Lidi";
    String DISCOTECHE = "Discoteche";
    String FAMIGLIA = "Famiglia";
    String MAX = "max";

    //costanti DB
    String DB_NAME = "datiUtente.db";
    String TABLE_UTENTE = "utenti";
    String COLUMN_NICKNAME = "nickname";

    String COLUMN_EMAIL = "email";
    String COLUMN_PASSWORD = "password";

    String ITEM_DESCRIPTION = "description";
    String ITEM_DIRECTIONS = "directions";
    String ITEM_REVIEWS = "reviews";
    String ITEM_ORA_A ="oraA";
    String ITEM_ORA_C="oraC";

    //stringa per link immagini
    String imagesPath = "http://barintondo.altervista.org/images/";

    String REQUEST_CHECK_PREF = "checkPref";

}
