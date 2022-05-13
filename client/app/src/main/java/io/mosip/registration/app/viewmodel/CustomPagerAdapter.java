package io.mosip.registration.app.viewmodel;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import io.mosip.registration.app.R;
import io.mosip.registration.app.activites.PreviewDocumentActivity;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import java.util.Objects;

public class CustomPagerAdapter extends PagerAdapter {

    private static final String TAG = PreviewDocumentActivity.class.getSimpleName();

    private Context context;
    private RegistrationService registrationService;
    private String fieldId;
    LayoutInflater inflater;

    public CustomPagerAdapter(Context context, RegistrationService registrationService, String fieldId) {
        this.context = context;
        this.registrationService = registrationService;
        this.fieldId = fieldId;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        byte[] bytes = new byte[0];
        try {
            bytes = this.registrationService.getRegistrationDto().getScannedPages(this.fieldId).get(position);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        View itemView = inflater.inflate(R.layout.document_view, container, false);
        ImageView imageView = itemView.findViewById(R.id.imageViewMain);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0,bytes.length));
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getCount() {
        try {
            return this.registrationService.getRegistrationDto().getScannedPages(this.fieldId).size();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.fieldId + " -- " + position;
    }
}
