package it.uniba.di.sms.barintondo.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class Luogo implements Parcelable {
    private String cod;
    private String nome;
    private String categoria;
    private String sottoCat;
    private String oraA;
    private String oraC;
    private String thumbnailLink;
    private String descrizione_en;
    private String descrizione_it;
    private String indirizzo;

    public Luogo() {
    }

    public Luogo(String newId , String newName , String newSottoCat , String newOraA , String newOraC ,
                 String newThumbnailLink , String newDescEn , String newDescIt , String newIndirizzo) {
        cod = newId;
        nome = newName;
        sottoCat = newSottoCat;
        oraA = newOraA;
        oraC = newOraC;
        thumbnailLink = newThumbnailLink;
        descrizione_en = newDescEn;
        descrizione_it = newDescIt;
        indirizzo = newIndirizzo;
    }

    protected Luogo(Parcel in) {
        cod = in.readString();
        nome = in.readString();
        sottoCat = in.readString();
        categoria = in.readString();
        oraA = in.readString();
        oraC = in.readString();
        thumbnailLink = in.readString();
        descrizione_en = in.readString();
        descrizione_it = in.readString();
        indirizzo = in.readString();
    }

    //Serve per la parcelizzazione negli intent
    public static final Creator<Luogo> CREATOR = new Creator<Luogo>() {
        @Override
        public Luogo createFromParcel(Parcel in) {
            return new Luogo( in );
        }

        @Override
        public Luogo[] newArray(int size) {
            return new Luogo[size];
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

    public void setCategoria(String categoria) {this.categoria = categoria;}

    public String getCategoria() {return categoria;}

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

    public void setDescrizione_en(String descEn) {
        descrizione_en = descEn;
    }

    public void setDescrizione_it(String descIt) {
        descrizione_it = descIt;

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

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getIndirizzo() {
        return indirizzo;
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
        dest.writeString( categoria );
        dest.writeString( sottoCat );
        dest.writeString( oraA );
        dest.writeString( oraC );
        dest.writeString( thumbnailLink );
        dest.writeString( descrizione_en );
        dest.writeString( descrizione_it );
        dest.writeString( indirizzo );
    }
}
