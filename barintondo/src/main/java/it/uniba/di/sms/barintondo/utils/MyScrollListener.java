package it.uniba.di.sms.barintondo.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

public class MyScrollListener extends OnScrollListener {
    private ToolbarSwitchCategories switchCategories;

    public MyScrollListener(ToolbarSwitchCategories switchCategories) {
        this.switchCategories = switchCategories;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(dy > 0) {
            switchCategories.hide();
        }else {
            switchCategories.show();
        }
    }
}
