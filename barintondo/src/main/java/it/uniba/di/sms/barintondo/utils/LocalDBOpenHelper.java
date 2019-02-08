package it.uniba.di.sms.barintondo.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LocalDBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "datiUtente.db";
    //informazioni per login
    private static final String TABLE_UTENTE = "utenti";
    private static final String COLUMN_ID = "_ID";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String CREATE_USER_QUERY =
            "CREATE TABLE " + TABLE_UTENTE + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NICKNAME + " text, " + COLUMN_EMAIL + " text, " + COLUMN_PASSWORD + " text);";
    //informazioni coupon
    private static final String TABLE_COUPON = "coupon";
    //riutilizzo la costante COLUMN_ID
    private static final String COLUMN_COD_COUPON = "codCoupon";
    private static final String COLUMN_COD_LUOGO = "codLuogo";
    private static final String COLUMN_LUOGO = "luogo";
    private static final String COLUMN_SCADENZA = "scadenza";
    private static final String COLUMN_DESC_IT = "descrizioneIt";
    private static final String COLUMN_DESC_EN = "descrizioneEn";
    private static final String COLUMN_CATEGORIA = "categoria";
    private static final String CREATE_COUPON_TABLE_QUERY =
            "CREATE TABLE " + TABLE_COUPON + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_COD_COUPON + " varchar(4), " +
                    COLUMN_COD_LUOGO + " varchar(4), " + COLUMN_LUOGO + " varchar(30), " + COLUMN_SCADENZA + " date, "
                    + COLUMN_DESC_IT + " text, " + COLUMN_DESC_EN + " text, " + COLUMN_CATEGORIA + " text);";

    public LocalDBOpenHelper(@Nullable Context context , @Nullable String name , @Nullable SQLiteDatabase.CursorFactory factory , int version) {
        super( context , name , factory , version );
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL( CREATE_USER_QUERY );
        db.execSQL( CREATE_COUPON_TABLE_QUERY );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db , int oldVersion , int newVersion) {

    }

    // Cancellazione unico account locale
    public static void delete(Context context) {
        LocalDBOpenHelper openHelper = new LocalDBOpenHelper( context , DB_NAME , null , 1 );
        SQLiteDatabase db = openHelper.getReadableDatabase();
        db.delete( TABLE_UTENTE , null , null );
        db.delete( TABLE_COUPON , null , null );
        db.close();
    }

    public static void deleteCoupon(Context context) {
        LocalDBOpenHelper openHelper = new LocalDBOpenHelper( context , DB_NAME , null , 1 );
        SQLiteDatabase db = openHelper.getReadableDatabase();
        db.delete( TABLE_COUPON , null , null );
        db.close();
    }

    // Verifica presenza di un utente locale
    public static boolean isPresent(Context context) {
        boolean found = false;

        LocalDBOpenHelper openHelper = new LocalDBOpenHelper( context , DB_NAME , null , 1 );
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] projection = {COLUMN_EMAIL};
        Cursor cursor = db.query( TABLE_UTENTE , projection , null , null , null , null , null );
        if (cursor.getCount() > 0) {
            found = true;
        }
        return found;
    }

    // Verifica presenza di un utente specifico in locale
    public static boolean isPresent(String email , Context context) {
        boolean found = false;

        LocalDBOpenHelper openHelper = new LocalDBOpenHelper( context , DB_NAME , null , 1 );
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] projection = {COLUMN_EMAIL};
        Cursor cursor = db.query( TABLE_UTENTE , projection , null , null , null , null , null );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String oEmail = cursor.getString( cursor.getColumnIndexOrThrow( Constants.COLUMN_EMAIL ) );
            if (email.equals( oEmail )) {
                found = true;
            }
        }
        return found;
    }

    public static void insertCoupon(Coupon newCoupon , Context context) {
        LocalDBOpenHelper openHelper = new LocalDBOpenHelper( context , DB_NAME , null , 1 );
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( COLUMN_COD_COUPON , newCoupon.getCod() );
        values.put( COLUMN_COD_LUOGO , newCoupon.getCodLuogo() );
        values.put( COLUMN_LUOGO , newCoupon.getLuogo() );
        values.put( COLUMN_SCADENZA , newCoupon.getScadenza() );
        values.put( COLUMN_DESC_IT , newCoupon.getDescrizione_it() );
        values.put( COLUMN_DESC_EN , newCoupon.getDescrizione_en() );
        values.put( COLUMN_CATEGORIA , newCoupon.getCategoria() );
        db.insert( TABLE_COUPON , null , values );
        db.close();
    }

    public static void insertInto(String nickname , String email , String password , Context context) {
        LocalDBOpenHelper openHelper = new LocalDBOpenHelper( context , DB_NAME , null , 1 );
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] projection = {Constants.COLUMN_EMAIL};
        Cursor cursor = db.query( Constants.TABLE_UTENTE , projection , null , null , null , null , null );
        if (cursor.getCount() > 0) {
            db.delete( Constants.TABLE_UTENTE , null , null );
        }
        db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put( COLUMN_NICKNAME , nickname );
        values.put( COLUMN_EMAIL , email );
        values.put( COLUMN_PASSWORD , password );
        db.insert( TABLE_UTENTE , null , values );
        db.close();
    }

    public static String getEmail(Context context) {
        //prelevo email dal db
        LocalDBOpenHelper dbHelper = new LocalDBOpenHelper( context , DB_NAME , null , 1 );
        SQLiteDatabase myDB = dbHelper.getReadableDatabase();
        //definisco la query
        String[] columns = {COLUMN_EMAIL};
        Cursor myCursor;
        //ottengo il cursore
        myCursor = myDB.query( TABLE_UTENTE , columns , null , null , null , null , null , null );
        myCursor.moveToFirst();

        String email = myCursor.getString( myCursor.getColumnIndex( COLUMN_EMAIL ) );

        myCursor.close();
        myDB.close();

        return email;
    }

    public static void getCouponList(Context context , List<Coupon> couponList) {
        couponList.clear();
        //prelevo coupon dal db
        LocalDBOpenHelper dbHelper = new LocalDBOpenHelper( context , DB_NAME , null , 1 );
        SQLiteDatabase myDB = dbHelper.getReadableDatabase();
        //definisco la query
        String[] columns = {COLUMN_COD_COUPON , COLUMN_COD_LUOGO , COLUMN_LUOGO , COLUMN_CATEGORIA, COLUMN_SCADENZA , COLUMN_DESC_IT , COLUMN_DESC_EN};
        Cursor myCursor;
        //ottengo il cursore
        myCursor = myDB.query( TABLE_COUPON , columns , null , null , null , null , null , null );
        myCursor.moveToFirst();
        while (!(myCursor.isAfterLast())) { //controllo se il cursore ha oltrepassato l'ultimo elemento
            //Log.i("LocalDBOpenHelper", "i= " + i);
            Coupon mCoupon = new Coupon();
            mCoupon.setCod( myCursor.getString( myCursor.getColumnIndex( COLUMN_COD_COUPON ) ) );
            mCoupon.setCodLuogo( myCursor.getString( myCursor.getColumnIndex( COLUMN_COD_LUOGO ) ) );
            mCoupon.setLuogo( myCursor.getString( myCursor.getColumnIndex( COLUMN_LUOGO ) ) );
            mCoupon.setScadenza( myCursor.getString( myCursor.getColumnIndex( COLUMN_SCADENZA ) ) );
            mCoupon.setDescrizione_it( myCursor.getString( myCursor.getColumnIndex( COLUMN_DESC_IT ) ) );
            mCoupon.setDescrizione_en( myCursor.getString( myCursor.getColumnIndex( COLUMN_DESC_EN ) ) );
            mCoupon.setCategoria( myCursor.getString( myCursor.getColumnIndex( COLUMN_CATEGORIA ) ) );
            couponList.add( mCoupon );
            myCursor.moveToNext();
        }

        myCursor.close();
        myDB.close();
    }

    public static int getNumberCouponLuogo(Context context, String codLuogo){
        LocalDBOpenHelper dbHelper = new LocalDBOpenHelper( context , DB_NAME , null , 1 );
        SQLiteDatabase myDB = dbHelper.getReadableDatabase();
        Cursor myCursor;
        String[] columns = {COLUMN_COD_COUPON , COLUMN_COD_LUOGO};

        //ottengo il cursore
        myCursor = myDB.query( TABLE_COUPON , columns , COLUMN_COD_LUOGO+"='"+codLuogo+"'", null , null , null , null , null );
        myCursor.moveToFirst();
        myCursor.moveToNext();
        int number = myCursor.getCount();

        myCursor.close();
        myDB.close();
        return number;
    }
}
