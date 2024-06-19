package io.mosip.registration_client.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CustomToast{

    private Toast toast = null;
    private View layout = null;
    private Activity activity;

    public CustomToast(Activity activity){
        this.activity = activity;
        final LayoutInflater inflater = LayoutInflater.from(activity);
        layout = inflater.inflate(R.layout.toast_layout, null);
        // Show the toast
        toast = new Toast(activity);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_LONG);
    }

    public void showToast(){
        toast.show();
    }

    public void setText(String text){
        if(layout!=null){
            EditText parent = layout.findViewById(R.id.toast_message);
            parent.setText(text);
        }
    }

    public void setIcon(int icon){
        if(layout!=null){
            ImageView imageView = layout.findViewById(R.id.toast_icon);
            imageView.setImageResource(icon);
        }
    }

    public void hideToast(){
        if(toast!=null){
            toast.cancel();
        }
    }
}
