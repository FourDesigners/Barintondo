package it.uniba.di.sms.barintondo.utils;

public class BarintondoItem {
    private String id;
    private String name;

    public BarintondoItem(){
    }

    public BarintondoItem(String newId, String newName){
        id = newId;
        name = newName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "id= " + id + " name= " + name;
    }
}
