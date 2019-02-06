package it.uniba.di.sms.barintondo.utils;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Locale;

public class Coupon implements Parcelable {
    private String cod;
    private String email;
    private String codLuogo;
    private String luogo;
    private String scadenza;
    private String descrizione_it; //DA METTERE PRIVATE
    private String descrizione_en; //DA METTERE PRIVATE
    private String sottoCat;

    public Coupon() {
    }

    public Coupon(String newCod , String newEmail , String newCodLuogo, String newLuogo , String newScadenza ,
                  String newDescrizione_it, String newDescrizione_en, String newSottoCat) {
        cod = newCod;
        email = newEmail;
        codLuogo = newCodLuogo;
        luogo = newLuogo;
        scadenza = newScadenza;
        descrizione_it = newDescrizione_it;
        descrizione_en = newDescrizione_en;
        sottoCat = newSottoCat;
    }

    protected Coupon(Parcel in) {
        cod = in.readString();
        email = in.readString();
        codLuogo = in.readString();
        luogo = in.readString();
        scadenza = in.readString();
        descrizione_it = in.readString();
        descrizione_en = in.readString();
        sottoCat = in.readString();
    }

    //Serve per la parcelizzazione negli intent
    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel in) {
            return new Coupon( in );
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodLuogo() {
        return codLuogo;
    }

    public void setCodLuogo(String codLuogo) {
        this.codLuogo = codLuogo;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }

    public void setDescrizione_en(String descEn){
        descrizione_en=descEn;
    }

    public void setDescrizione_it(String descIt){ descrizione_it=descIt; }

    public String getDescription() {
        String lang = Locale.getDefault().getLanguage();

        switch (lang) {
            case "it":
                return descrizione_it;
            default:
                return descrizione_en;
        }
    }

    public String getDescrizione_it() {
        return descrizione_it;
    }

    public String getDescrizione_en() {
        return descrizione_en;
    }

    public String getSottoCat() {
        return sottoCat;
    }

    public void setSottoCat(String sottoCat) {
        this.sottoCat = sottoCat;
    }

    @Override
    public String toString() {
        return "id= " + cod + " email= " + email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest , int flags) {
        dest.writeString( cod );
        dest.writeString( email );
        dest.writeString( codLuogo );
        dest.writeString( luogo );
        dest.writeString( scadenza );
        dest.writeString( descrizione_it );
        dest.writeString( descrizione_en );
        dest.writeString( sottoCat );
    }
}
