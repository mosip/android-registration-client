package io.mosip.registration.app.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import io.mosip.registration.app.R;
import io.mosip.registration.app.activites.ModalityActivity;
import io.mosip.registration.clientmanager.constant.Modality;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * The adapter class for the RecyclerView, contains the sports data.
 */
public class ModalityAdapter extends RecyclerView.Adapter<ModalityAdapter.ViewHolder> {

    private ArrayList<Modality> modalityData;
    private HashMap<Modality, Boolean> captureStatuses;
    private Context mContext;
    private String fieldId;
    private String purpose;
    private ModalityAdapter modalityAdapter;

    /**
     * Constructor that passes in the Modality data and the context.
     *
     * @param list ArrayList containing the Modality data.
     * @param context Context of the application.
     */
    public ModalityAdapter(Context context, HashMap<Modality, Boolean>  captureStatuses, ArrayList<Modality> list,
                           String fieldId, String purpose) {
        this.modalityData = list;
        this.captureStatuses = captureStatuses;
        this.mContext = context;
        this.purpose = purpose;
        this.fieldId = fieldId;
    }


    /**
     * Required method for creating the viewholder objects.
     *
     * @param parent The ViewGroup into which the new View will be added
     *               after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return The newly created ViewHolder.
     */
    @Override
    public ModalityAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        modalityAdapter = this;
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.modality_list_item, parent, false));
    }

    /**
     * Required method that binds the data to the viewholder.
     *
     * @param holder The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    @Override
    public void onBindViewHolder(ModalityAdapter.ViewHolder holder,
                                 int position) {
        // Get current sport.
        Modality currentModality = modalityData.get(position);

        // Populate the textviews with data.
        holder.bindTo(currentModality);
    }

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return modalityData.size();
    }


    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member Variables for the TextViews
        private ImageView modalityImage;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the modality_list_item.xml layout file.
         */
        ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views.
            modalityImage = itemView.findViewById(R.id.modalityImage);
            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Modality modality) {
            // Populate the textviews with data.
            switch (modality) {
                case FACE:
                    modalityImage.setImageResource(R.drawable.face);
                    modalityImage.setTag(Modality.FACE.name());
                    break;
                case FINGERPRINT_SLAB_LEFT:
                    modalityImage.setImageResource(R.drawable.left_palm);
                    modalityImage.setTag(Modality.FINGERPRINT_SLAB_LEFT.name());
                    break;
                case FINGERPRINT_SLAB_RIGHT:
                    modalityImage.setImageResource(R.drawable.right_palm);
                    modalityImage.setTag(Modality.FINGERPRINT_SLAB_RIGHT.name());
                    break;
                case FINGERPRINT_SLAB_THUMBS:
                    modalityImage.setImageResource(R.drawable.thumbs);
                    modalityImage.setTag(Modality.FINGERPRINT_SLAB_THUMBS.name());
                    break;
                case IRIS_DOUBLE:
                    modalityImage.setImageResource(R.drawable.double_iris);
                    modalityImage.setTag(Modality.IRIS_DOUBLE.name());
                    break;
                case EXCEPTION_PHOTO:
                    modalityImage.setImageResource(R.drawable.exception_photo);
                    modalityImage.setTag(Modality.EXCEPTION_PHOTO.name());
                    break;
            }
            modalityImage.setBackground(mContext.getResources().getDrawable(captureStatuses.getOrDefault(modality, false) ?
                    R.drawable.border_green : R.drawable.border_red));
        }

        /**
         * Handle click to show DetailActivity.
         *
         * @param view View that is clicked.
         */
        @Override
        public void onClick(View view) {
            Modality currentModality = modalityData.get(getAdapterPosition());
            Intent detailIntent = new Intent(mContext, ModalityActivity.class);
            detailIntent.putExtra("modality", currentModality);
            detailIntent.putExtra("fieldId", fieldId);
            detailIntent.putExtra("purpose", purpose);
            mContext.startActivity(detailIntent);
        }
    }
}
