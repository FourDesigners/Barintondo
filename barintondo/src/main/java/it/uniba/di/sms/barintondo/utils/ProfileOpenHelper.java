package it.uniba.di.sms.barintondo.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.Nullable;

public class ProfileOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "datiUtente.db";
    private static final String TABLE_UTENTE = "utenti";
    private static final String COLUMN_ID = "_ID";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String CREATE_USER_QUERY =
            "CREATE TABLE " + TABLE_UTENTE + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NICKNAME + " text, " + COLUMN_EMAIL + " text, " + COLUMN_PASSWORD + " text);";


    public ProfileOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_USER_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
