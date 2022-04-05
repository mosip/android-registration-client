package io.mosip.registration.app.dynamicviews;

public interface DynamicView {

     String getDataType();
     Object getValue();
     void setValue();
     boolean validValue();
}
