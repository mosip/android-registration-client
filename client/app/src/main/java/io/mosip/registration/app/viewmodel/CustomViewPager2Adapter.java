package io.mosip.registration.app.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.mosip.registration.app.R;
import io.mosip.registration.app.util.BiometricService;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.packetmanager.dto.PacketWriter.Entry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;

public class CustomViewPager2Adapter extends RecyclerView.Adapter<CustomViewPager2Adapter.ViewHolder>{

    private LinkedHashMap<Modality, List<String>> modalityDetails;
    private List<Modality> modalities;
    private Context ctx;

    // Constructor of our ViewPager2Adapter class
    public CustomViewPager2Adapter(Context ctx, List<Modality> modalities, LinkedHashMap<Modality, List<String>> configuredModalities) {
        this.ctx = ctx;
        this.modalityDetails = configuredModalities;
        this.modalities = modalities;
    }

    // This method returns our layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.bio_image, parent, false);
        return new ViewHolder(view);
    }

    // This method binds the screen with the view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // This will set the images in imageview
        switch (modalities.get(position)) {
            case FACE:
                holder.bioCapture.setImageResource(R.drawable.face);
                holder.bioCapture.setTag(Modality.FACE.name());
                setupClickListener(holder, Modality.FACE, this.ctx);
                break;
            case FINGERPRINT_SLAB_LEFT:
                holder.bioCapture.setImageResource(R.drawable.left_palm);
                setupClickListener(holder, Modality.FINGERPRINT_SLAB_LEFT, this.ctx);
                holder.bioCapture.setTag(Modality.FINGERPRINT_SLAB_LEFT.name());
                break;
            case FINGERPRINT_SLAB_RIGHT:
                holder.bioCapture.setImageResource(R.drawable.right_palm);
                setupClickListener(holder, Modality.FINGERPRINT_SLAB_RIGHT, this.ctx);
                holder.bioCapture.setTag(Modality.FINGERPRINT_SLAB_RIGHT.name());
                break;
            case FINGERPRINT_SLAB_THUMBS:
                holder.bioCapture.setImageResource(R.drawable.thumbs);
                setupClickListener(holder, Modality.FINGERPRINT_SLAB_THUMBS, this.ctx);
                holder.bioCapture.setTag(Modality.FINGERPRINT_SLAB_THUMBS.name());
                break;
            case IRIS_DOUBLE:
                holder.bioCapture.setImageResource(R.drawable.double_iris);
                setupClickListener(holder, Modality.IRIS_DOUBLE, this.ctx);
                holder.bioCapture.setTag(Modality.IRIS_DOUBLE.name());
                break;
        }
    }

    // This Method returns the size of the Array
    @Override
    public int getItemCount() {
        return modalities.size();
    }

    private void setupClickListener(ViewHolder holder, Modality modality, Context context) {
        holder.bioCapture.setClickable(true);
        holder.bioCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                BiometricService biometricService = (BiometricService) context;
                biometricService.startBiometricCapture(modality);
            }
        });
    }

    // The ViewHolder class holds the view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bioCapture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bioCapture = itemView.findViewById(R.id.bio_capture);
            bioCapture.setClickable(true);
        }
    }
}
