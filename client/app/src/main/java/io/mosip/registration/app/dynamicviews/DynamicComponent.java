package io.mosip.registration.app.dynamicviews;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class DynamicComponent    {
    private List<DynamicView> views;
    protected Context context;

    public DynamicComponent(Context context){
        this.context = context;
        views = new ArrayList<>();
    }

    public DynamicView getPrimaryView() {
        return views.get(0);
    }
    public DynamicView getSecondaryView() {
        return views.get(1);
    }
    public void setPrimaryView(DynamicView primaryView) {
        this.setView(0,primaryView);
    }

    public DynamicView getView(int index) {
        return views.get(index);
    }
    public DynamicView setView(int index, DynamicView view){
        return views.set(index,view);
    }
    public DynamicView addView(DynamicView view){
        views.add(view);
        return view;
    }

    public int getViewCount(){
        if(views!=null)
            return views.size();
        else
            return 0;
    }
//    public abstract void init();
}
