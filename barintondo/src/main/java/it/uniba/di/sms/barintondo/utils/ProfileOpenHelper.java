package it.uniba.di.sms.barintondo.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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

    public static void delete(ProfileOpenHelper openHelper) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        db.delete(Constants.TABLE_UTENTE, null, null);
        db.close();
    }

    public static boolean isPresent(ProfileOpenHelper openHelper) {
        boolean found = false;

        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] projection = {Constants.COLUMN_EMAIL};
        Cursor cursor = db.query(Constants.TABLE_UTENTE, projection, null, null, null, null, null);
        if(cursor.getCount() > 0) {
            found = true;
        }
        return found;
    }

    public static boolean isPresent(String email, ProfileOpenHelper openHelper) {
        boolean found = false;

        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] projection = {Constants.COLUMN_EMAIL};
        Cursor cursor = db.query(Constants.TABLE_UTENTE, projection, null, null, null, null, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            String oEmail = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_EMAIL));
            if(email.equals(oEmail)) {
                found = true;
            }
        }
        return found;
    }

    public static boolean isPresent(String email, String password, ProfileOpenHelper openHelper) {
        boolean found = false;

        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] projection = {Constants.COLUMN_EMAIL, Constants.COLUMN_PASSWORD};
        Cursor cursor = db.query(Constants.TABLE_UTENTE, projection, null, null, null, null, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            String oEmail = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_EMAIL));
            String oPassword = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_PASSWORD));
            if(email.equals(oEmail) && password.equals(oPassword)) {
                found = true;
            }
        }
        return found;
    }

    public static void insertInto(String nickname, String email, String password, ProfileOpenHelper openHelper) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] projection = {Constants.COLUMN_EMAIL};
        Cursor cursor = db.query(Constants.TABLE_UTENTE, projection, null, null, null, null, null);
        if(cursor.getCount() > 0) {
            db.delete(Constants.TABLE_UTENTE, null, null);
        }
        db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_NICKNAME, nickname);
        values.put(Constants.COLUMN_EMAIL, email);
        values.put(Constants.COLUMN_PASSWORD, password);
        db.insert(Constants.TABLE_UTENTE, null, values);
        db.close();
    }

    public static String[] getLocalAccount(ProfileOpenHelper openHelper) {
        String[] params = new String[3];

        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] projection = {Constants.COLUMN_NICKNAME, Constants.COLUMN_EMAIL, Constants.COLUMN_PASSWORD};
        Cursor cursor = db.query(Constants.TABLE_UTENTE, projection, null, null, null, null, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            params[0] = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_NICKNAME));
            params[1] = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_EMAIL));
            params[2] = cursor.getString(cursor.getColumnIndexOrThrow(Constants.COLUMN_PASSWORD));
        }else {
            params = null;
        }
        return params;
    }

    public static void setNickname(Context context, String email, String password, ProfileOpenHelper openHelper) {
        BackgroundGetNickname bgn = new BackgroundGetNickname(context, email, password, openHelper);
        bgn.execute();
    }
}
