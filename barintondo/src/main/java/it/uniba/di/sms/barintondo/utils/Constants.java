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
    String INTENT_ITEM = "item";

    //intent per CouponDetailActivity
    String INTENT_COUPON = "coupon";

    // SharedPreferences
    String ORDER = "order";
    String CHIESE = "Chiese";
    String MONUMENTI = "Monumenti";
    String TEATRI = "Teatri";
    String LIDI = "Lidi";
    String DISCOTECHE = "Discoteche";
    String FAMIGLIA = "Famiglia";
    String SKIP = "skip";
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

    //Stesso nome delle categorie del db, questa stringa sarà usata dallo script per la quesry, NON CAMBIARE
    String REQUEST_GET_ATTRACTIONS = "Attrazione";
    String REQUEST_GET_SLEEP = "Dormire";
    String REQUEST_GET_EAT = "Mangiare";
    String REQUEST_GET_EVENTS = "Evento"; //da controllare perchè non ancora inserita nel DB
    String REQUEST_GET_NEAR_BARI="Near"; //da controllare perchè non ancora inserita nel DB

    String REQUEST_CHECK_PREF = "checkPref";
    String REQUEST_ADD_PREF="addPref";
    String REQUEST_REMOVE_PREF="removePref";
    String REQUEST_GET_ALL_PREF="getAllPref";
    String REQUEST_RESULT_OK="ok";

}
