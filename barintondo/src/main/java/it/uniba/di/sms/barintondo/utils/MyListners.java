package it.uniba.di.sms.barintondo.utils;

import android.location.Location;

public interface MyListners {

    public interface SingleLuogo {
        public void onLuogo(Luogo luogo);
        public void onEvento(Evento evento);
        public void onError(String error);
    }

    public interface LuoghiList{
        public void onList();
        public void onError(String error);
    }

    public interface Interests {
        public void onAdd(Boolean result);
        public void onRemove(Boolean result);
        public void onCheck(Boolean result);
        public void onError(String error);
    }

    public interface InterestsList{
        public void onInterestsList();
        public void onError(String error);
    }

    public interface CouponList{
        public void onCouponList();
        public void onError(String error);
    }

    public interface ItemsAdapterListener {
        public void onItemsSelected(Luogo item);
    }

    public interface CouponAdapterListener {
        public void onItemsSelected(Coupon item);
    }

    public interface ReviewsList{
        public void onReviewList();
        public void onError(String error);
    }

    public interface ReviewSave{
        public void onReviewAdded();
        public void onError(String error);
    }

    public interface UserLocationCallback{
        public void onLocation(Location location);
    }

}
