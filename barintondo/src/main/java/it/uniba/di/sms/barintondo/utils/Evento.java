package it.uniba.di.sms.barintondo.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class Evento extends Luogo implements Parcelable {
        public Evento(Luogo luogo){
            this.setCod( luogo.getCod() );
            this.setNome( luogo.getNome() );
            this.setCitta( luogo.getCitta() );
            this.setSottoCat(luogo.getSottoCat());
            this.setOraA( luogo.getOraA() );
            this.setOraC( luogo.getOraC() );
            this.setThumbnailLink( luogo.getThumbnailLink() );
            this.setDescrizione_en( luogo.getDescrizione_en() );
            this.setDescrizione_it( luogo.getDescrizione_it() );
            this.setIndirizzo(luogo.getIndirizzo());
            this.setVoto( luogo.getVoto() );
        }
}
