package io.mosip.registration.app.ui.dynamic;

import android.content.Context;

import io.mosip.registration.app.ui.dynamic.views.DynamicTextBoxView;

public class DynamicComponentFactory {
    private Context context;
    public DynamicComponentFactory(Context context){
        this.context = context;
    }
    
    public DynamicComponent getTextComponent(){
        DynamicComponent<DynamicTextBoxView> component = new DynamicComponent<DynamicTextBoxView>(context) {
            @Override
            public void init() {
                this.addView(new DynamicTextBoxView(context));
                this.addView(new DynamicTextBoxView(context));
            }
        };
        return component;
    }
    
    public DynamicComponent getComponentByType(ComponentType componentType){
        switch (componentType){
            case TEXT:
                return this.getTextComponent();
            case AGE_DATE:
            case DROP_DOWN:
            case TOGGLE:
            default:
                return this.getTextComponent();
        }
    }
}
