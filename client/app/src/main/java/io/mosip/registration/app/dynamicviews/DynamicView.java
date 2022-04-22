package io.mosip.registration.app.dynamicviews;

import java.util.Observer;

public interface DynamicView extends Observer {

     String getDataType();
     void setValue();
     boolean isValidValue();
     boolean isRequired();
}
