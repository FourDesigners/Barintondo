package it.uniba.di.sms.barintondo.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.sql.Time;
import java.util.Locale;

public class BarintondoItem implements Parcelable {
    private String cod;
    private String nome;
    private String sottoCat;
    private String oraA;
    private String oraC;
    private String thumbnailLink;
    private String descrizione_en;
    private String descrizione_it;

    public BarintondoItem() {
    }

    public BarintondoItem(String newId , String newName , String newSottoCat , String newOraA , String newOraC ,
                          String newThumbnailLink , String newDescEn , String newDescIt) {
        cod = newId;
        nome = newName;
        sottoCat = newSottoCat;
        oraA = newOraA;
        oraC = newOraC;
        thumbnailLink = newThumbnailLink;
        descrizione_en = newDescEn;
        descrizione_it = newDescIt;
    }

    protected BarintondoItem(Parcel in) {
        cod = in.readString();
        nome = in.readString();
        sottoCat = in.readString();
        oraA = in.readString();
        oraC = in.readString();
        thumbnailLink = in.readString();
        descrizione_en = in.readString();
        descrizione_it = in.readString();
    }

    //Serve per la parcelizzazione negli intent
    public static final Creator<BarintondoItem> CREATOR = new Creator<BarintondoItem>() {
        @Override
        public BarintondoItem createFromParcel(Parcel in) {
            return new BarintondoItem( in );
        }

        @Override
        public BarintondoItem[] newArray(int size) {
            return new BarintondoItem[size];
        }
    };

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSottoCat() {
        return sottoCat;
    }

    public void setSottoCat(String sottoCat) {
        this.sottoCat = sottoCat;
    }

    public String getOraA() {
        return oraA;
    }

    public void setOraA(String oraA) {
        this.oraA = oraA;
    }

    public String getOraC() {
        return oraC;
    }

    public void setOraC(String oraC) {
        this.oraC = oraC;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public void setDescrizione_en(String descEn){
        descrizione_en=descEn;
    }
    public void setDescrizione_it(String descIt){
        descrizione_it=descIt;
    }

    public String getDescription() {
        String lang = Locale.getDefault().getLanguage();

        switch (lang) {
            case "it":
                return descrizione_it;
            default:
                return descrizione_en;
        }
    }

    @Override
    public String toString() {
        return "id= " + cod + " name= " + nome;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest , int flags) {
        dest.writeString( cod );
        dest.writeString( nome );
        dest.writeString( sottoCat );
        dest.writeString( oraA );
        dest.writeString( oraC );
        dest.writeString( thumbnailLink );
        dest.writeString( descrizione_en );
        dest.writeString( descrizione_it );
    }
}
