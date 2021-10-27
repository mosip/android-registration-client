package io.mosip.registration.app.ui.dynamic;

import android.content.Context;

import java.util.ArrayList;

public abstract class DynamicComponent<T extends DynamicView> {
    private ArrayList<T> views;
    protected Context context;

    public DynamicComponent(Context context){
        this.context = context;
        views = new ArrayList<>();
        init();
    }
    public T getPrimaryView() {
        return views.get(0);
    }

    public void setPrimaryView(T primaryView) {
        this.setView(0,primaryView);
    }

    public T getView(int index) {
        return views.get(index);
    }
    public T setView(int index, T view){
        return views.set(index,view);
    }
    public T addView(T view){
        views.add(view);
        return view;
    }
    public abstract void init();
}
