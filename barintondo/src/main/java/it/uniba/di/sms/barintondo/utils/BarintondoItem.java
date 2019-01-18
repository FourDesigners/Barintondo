package it.uniba.di.sms.barintondo.utils;

import android.widget.ImageView;

import java.sql.Time;

public class BarintondoItem {
    private String cod;
    private String nome;
    private String sottoCat;
    private String oraA;
    private String oraC;
    private String thumbnailLink;

    public BarintondoItem(){
    }

    public BarintondoItem(String newId, String newName, String newSottoCat, String newOraA, String newOraC, String newThumbnailLink){
        cod = newId;
        nome = newName;
        sottoCat = newSottoCat;
        oraA = newOraA;
        oraC = newOraC;
        thumbnailLink = newThumbnailLink;
    }

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

    @Override
    public String toString() {
        return "id= " + cod + " name= " + nome;
    }
}
