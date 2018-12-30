package it.uniba.di.sms.barintondo.utils;

public interface Constants {
    String TAG = "Barintondo";

    //intent per ItemListActivity
    String INTENT_ACTIVITY_ITEM_TYPE = "item_type";
    String INTENT_ATTRACTIONS = "attractions";
    String INTENT_SLEEPING = "dormire";
    String INTENT_EATING = "mangiare";
    String INTENT_EVENTS = "eventi";
    String INTENT_NEAR = "vicino";

    //costanti DB
    String DB_NAME = "datiUtente.db";
    String TABLE_UTENTE = "utenti";
    String COLUMN_USERNAME = "username";

    String COLUMN_EMAIL = "email";
    String COLUMN_PASSWORD = "password";
}
