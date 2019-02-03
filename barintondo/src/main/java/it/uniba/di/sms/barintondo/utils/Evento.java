package it.uniba.di.sms.barintondo.utils;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.uniba.di.sms.barintondo.R;

public class Evento extends Luogo implements Parcelable {
    private String codLuogo;
    private Date dataInizio;
    private Date dataFine;

    public Evento() {
    }

    public Evento(Luogo luogo) {
        this.setCod( luogo.getCod() );
        this.setNome( luogo.getNome() );
        this.setCitta( luogo.getCitta() );
        this.setSottoCat( luogo.getSottoCat() );
        this.setCategoria( luogo.getCategoria() );
        this.setLatitudine( luogo.getLatitudine() );
        this.setLongitudine( luogo.getLongitudine() );
        this.setOraA( luogo.getOraA() );
        this.setOraC( luogo.getOraC() );
        this.setThumbnailLink( luogo.getThumbnailLink() );
        this.setDescrizione_en( luogo.getDescrizione_en() );
        this.setDescrizione_it( luogo.getDescrizione_it() );
        this.setIndirizzo( luogo.getIndirizzo() );
        this.setVoto( luogo.getVoto() );
    }

    public String getCodLuogo() {
        return codLuogo;
    }

    public void setCodLuogo(String codLuogo) {
        this.codLuogo = codLuogo;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(String dataInizio) {

        this.dataInizio = Date.valueOf( dataInizio );
    }

    public Date getDataFine() {
        return dataFine;
    }

    public void setDataFine(String dataFine) {
        this.dataFine = Date.valueOf( dataFine );
    }

    public String getDaysToEventString(Context context){
        int days = getDaysToEvent();
        String inDays;
        if(days<=0){
            if(dataInizio.equals(dataFine)){
                inDays = context.getResources().getString( R.string.strToday );
            }
            else inDays= context.getResources().getString( R.string.strInProgress );
        } else if(days<=30){
            inDays = context.getResources().getQuantityString(R.plurals.EventInDays, days, days);

        }else{
            if(days%30>=5) days=(days/30)+1;
            else days=days/30;
            inDays = context.getResources().getQuantityString(R.plurals.EventInMonths, days, days);
        }
        return inDays;
    }

    public int getDaysToEvent(){
        java.util.Date today = new java.util.Date();
        long diff = dataInizio.getTime() - today.getTime() ;
        int days= (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return days;
    }
}
