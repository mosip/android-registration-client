package io.mosip.registration.app.viewmodel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<GenericValueDto> {

    private ArrayList<GenericValueDto> dataList;
    private Context mContext;
    private int itemLayout;


    public CustomArrayAdapter(Context context, int resource, ArrayList<GenericValueDto> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Nullable
    @Override
    public GenericValueDto getItem(int position) {
        return super.getItem(position);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
