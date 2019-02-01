package it.uniba.di.sms.barintondo.utils;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;

import java.util.Locale;
import java.util.Objects;

public class Luogo implements Parcelable, Comparable<Luogo>{
    private String cod;
    private String nome;
    private String citta;
    private String categoria;
    private String sottoCat;
    private float latitudine;
    private float longitudine;
    private String oraA;
    private String oraC;
    private String thumbnailLink;
    private String descrizione_en;
    private String descrizione_it;
    private String indirizzo;
    private int voto;
    private int order;

    public Luogo() {
    }

    public Luogo(String newId , String newName , String newCitta, String newSottoCat , String newOraA , String newOraC ,
                 String newThumbnailLink , String newDescEn , String newDescIt , String newIndirizzo, int newVoto) {
        cod = newId;
        nome = newName;
        citta=newCitta;
        sottoCat = newSottoCat;
        oraA = newOraA;
        oraC = newOraC;
        thumbnailLink = newThumbnailLink;
        descrizione_en = newDescEn;
        descrizione_it = newDescIt;
        indirizzo = newIndirizzo;
        voto=newVoto;
    }

    protected Luogo(Parcel in) {
        cod = in.readString();
        nome = in.readString();
        citta = in.readString();
        sottoCat = in.readString();
        categoria = in.readString();
        latitudine = in.readFloat();
        longitudine = in.readFloat();
        oraA = in.readString();
        oraC = in.readString();
        thumbnailLink = in.readString();
        descrizione_en = in.readString();
        descrizione_it = in.readString();
        indirizzo = in.readString();
        voto=in.readInt();
        order=in.readInt();
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

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public void setCategoria(String categoria) {this.categoria = categoria;}

    public String getCategoria() {return categoria;}

    public String getSottoCat() {
        return sottoCat;
    }

    public void setSottoCat(String sottoCat) {
        this.sottoCat = sottoCat;
    }

    public float getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(float latitudine) {
        this.latitudine = latitudine;
    }

    public float getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(float longitudine) {
        this.longitudine = longitudine;
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

    public String getDescrizione_en() {
        return descrizione_en;
    }

    public String getDescrizione_it() {
        return descrizione_it;
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

    public int getVoto() {
        return voto;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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
        dest.writeString( citta );
        dest.writeString( categoria );
        dest.writeString( sottoCat );
        dest.writeFloat( latitudine );
        dest.writeFloat( longitudine );
        dest.writeString( oraA );
        dest.writeString( oraC );
        dest.writeString( thumbnailLink );
        dest.writeString( descrizione_en );
        dest.writeString( descrizione_it );
        dest.writeString( indirizzo );
        dest.writeInt(voto);
        dest.writeInt( order );
    }


    @Override
    public int compareTo(Luogo o) {
        if(this.order> o.order) return -1;
        if(this.order==o.order) return 0;
        return 1;
    }

    public int calculateDistanceTo(Location destinazione){
        float[] results=new float[10];
        Location.distanceBetween( latitudine, longitudine, destinazione.getLatitude(), destinazione.getLongitude(), results  );
        int distance = (int) results[0];
        return distance;
    }
}
