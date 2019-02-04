package it.uniba.di.sms.barintondo.utils;

public interface MyListners {

    public interface SingleLuogo {
        public void onLuogo(Luogo luogo);
        public void onEvento(Evento evento);
    }

    public interface LuoghiList{
        public void onList();
    }

    public interface Interests {
        public void onAdd(Boolean result);
        public void onRemove(Boolean result);
        public void onCheck(Boolean result);
    }
}
