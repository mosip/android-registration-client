package io.mosip.registration.app.viewmodel;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import io.mosip.registration.app.R;
import io.mosip.registration.app.activites.PreviewDocumentActivity;
import io.mosip.registration.clientmanager.spi.RegistrationService;

public class CustomPagerAdapter extends PagerAdapter {

    private static final String TAG = PreviewDocumentActivity.class.getSimpleName();

    private Context context;
    private RegistrationService registrationService;
    private String fieldId;

    public CustomPagerAdapter(Context context, RegistrationService registrationService, String fieldId) {
        this.context = context;
        this.registrationService = registrationService;
        this.fieldId = fieldId;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.document_view, container, false);
        ImageView imageView = layout.findViewById(R.id.doc_image);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0,bytes.length));
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
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
        return true;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.fieldId + " -- " + position;
    }
}
