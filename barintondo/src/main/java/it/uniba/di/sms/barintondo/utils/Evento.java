package it.uniba.di.sms.barintondo.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class Evento implements Parcelable {
    private String cod;
    private String nome;
    private String dataInizio;
    private String dataFine;
    private String citta;
    private String indirizzo;
    private String luogo;
    private String descrizione_en;
    private String descrizione_it;
    private String oraI;
    private String oraF;
    private String thumbnailLink;

    public Evento() {
    }

    public Evento(String cod, String nome, String dataInizio, String dataFine, String citta,
                 String indirizzo, String luogo, String descrizione_it, String descrizione_en, String oraI, String oraF, String thumbnailLink) {
        this.cod = cod;
        this.nome = nome;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.citta = citta;
        this.indirizzo = indirizzo;
        this.descrizione_en = descrizione_en;
        this.descrizione_it = descrizione_it;
        this.luogo = luogo;
        this.oraF = oraF;
        this.oraI = oraI;
        this.thumbnailLink = thumbnailLink;
    }

    protected Evento(Parcel in) {
        cod = in.readString();
        nome = in.readString();
        dataInizio = in.readString();
        dataFine = in.readString();
        citta = in.readString();
        indirizzo = in.readString();
        luogo = in.readString();
        descrizione_en = in.readString();
        descrizione_it = in.readString();
        oraI = in.readString();
        oraF = in.readString();
        thumbnailLink = in.readString();
    }

    //Serve per la parcelizzazione negli intent
    public static final Creator<Evento> CREATOR = new Creator<Evento>() {
        @Override
        public Evento createFromParcel(Parcel in) {
            return new Evento( in );
        }

        @Override
        public Evento[] newArray(int size) {
            return new Evento[size];
        }
    };

    public void setCod(String cod) {
        this.cod = cod;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataInizio(String dataInizio) {
        this.dataInizio = dataInizio;
    }

    public void setDataFine(String dataFine) {
        this.dataFine = dataFine;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public void setDescrizione_en(String descrizione_en) {
        this.descrizione_en = descrizione_en;
    }

    public void setDescrizione_it(String descrizione_it) {
        this.descrizione_it = descrizione_it;
    }

    public void setOraI(String oraI) {
        this.oraI = oraI;
    }

    public void setOraF(String oraF) {
        this.oraF = oraF;
    }

    public String getCod() {

        return cod;
    }

    public String getNome() {
        return nome;
    }

    public String getDataInizio() {
        return dataInizio;
    }

    public String getDataFine() {
        return dataFine;
    }

    public String getCitta() {
        return citta;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public String getLuogo() {
        return luogo;
    }

    public String getDescrizione_en() {
        return descrizione_en;
    }

    public String getDescrizione_it() {
        return descrizione_it;
    }

    public String getOraI() {
        return oraI;
    }

    public String getOraF() {
        return oraF;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public String getThumbnailLink() {

        return thumbnailLink;
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
        dest.writeString( dataInizio );
        dest.writeString( dataFine );
        dest.writeString( citta );
        dest.writeString( indirizzo );
        dest.writeString( luogo );
        dest.writeString( descrizione_en );
        dest.writeString( descrizione_it );
        dest.writeString( oraF );
        dest.writeString( oraI );
        dest.writeString( thumbnailLink );
    }
}
