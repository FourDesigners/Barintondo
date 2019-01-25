package it.uniba.di.sms.barintondo.utils;

public class Contact {
    private String servizio;
    private String numero;

    public Contact(){}

    public Contact(String servizio, String numero){
        this();
        this.servizio = servizio;
        this.numero = numero;
    }

    public void setServizio(String servizio) {
        this.servizio = servizio;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "numero='" + numero + '\'' +
                '}';
    }

    public String getServizio() {
        return servizio;
    }

    public void setNumero(String numero) {
        if(numero.length() == 10) {
            this.numero = numero;
        }
    }

    public String getNumero() {
        return numero;
    }

}
