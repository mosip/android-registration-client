package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import io.mosip.registration.app.R;
import io.mosip.registration.app.util.ClientConstants;
import io.mosip.registration.app.viewmodel.CustomViewPager2Adapter;
import io.mosip.registration.app.viewmodel.ModalityAdapter;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.service.Biometrics095Service;
import io.mosip.registration.clientmanager.spi.BiometricsService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.stream.Collectors;

import static io.mosip.registration.app.util.ClientConstants.FIELD_LABEL_TEMPLATE;
import static io.mosip.registration.app.util.ClientConstants.REQUIRED_FIELD_LABEL_TEMPLATE;

public class DynamicBiometricsBox extends LinearLayout implements DynamicView {

    private static final String TAG = DynamicBiometricsBox.class.getSimpleName();

    RegistrationDto registrationDto = null;
    FieldSpecDto fieldSpecDto = null;
    List<String> bioAttributes = null;
    final int layoutId = R.layout.biometrics_box;
    LinkedHashMap<Modality, List<String>> identifiedModalities = new LinkedHashMap<>();

    ArrayList<Modality> modalities = new ArrayList<>();
    HashMap<Modality, Boolean> statuses = new HashMap<>();
    private RecyclerView mRecyclerView;
    private ModalityAdapter modalityAdapter;

    Biometrics095Service biometricsService;

    public DynamicBiometricsBox(Context context, FieldSpecDto fieldSpecDto, RegistrationDto registrationDto,
                                Biometrics095Service biometricsService) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.registrationDto = registrationDto;
        this.biometricsService = biometricsService;
        initializeView(context);
    }

    private void initializeView(Context context) {
        inflate(context, layoutId, this);
        this.setTag(fieldSpecDto.getId());

        List<String> labels = new ArrayList<>();
        for(String language : registrationDto.getSelectedLanguages()) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }

        bioAttributes = UserInterfaceHelperService.getRequiredBioAttributes(fieldSpecDto,
                registrationDto.getMVELDataContext());
        //TODO
        if(bioAttributes.contains("face")) {
            bioAttributes.remove("face");
            bioAttributes.add("");
        }

        setupConfiguredBioAttributes();

        setModalityRecycleView(context);

        ((TextView)findViewById(R.id.biometrics_label)).setText(Html.fromHtml(isRequired() ?
                String.format(REQUIRED_FIELD_LABEL_TEMPLATE, String.join(ClientConstants.LABEL_SEPARATOR, labels)) :
                String.format(FIELD_LABEL_TEMPLATE, String.join(ClientConstants.LABEL_SEPARATOR, labels)), 1));

        this.setVisibility((isRequired() && UserInterfaceHelperService.isFieldVisible(fieldSpecDto,
                registrationDto.getMVELDataContext())) ? VISIBLE : GONE);
    }

    @Override
    public String getDataType() {
        return fieldSpecDto.getType();
    }

    @Override
    public void setValue() {
    }

    @Override
    public boolean isValidValue() {
        return getCaptureStatuses().values().stream().allMatch(status -> status);
    }

    @Override
    public boolean isRequired() {
        return !bioAttributes.isEmpty();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(UserInterfaceHelperService.isFieldVisible(fieldSpecDto, registrationDto.getMVELDataContext())) {
            this.setVisibility(VISIBLE);
        }
        else {
            registrationDto.removeBiometricField(fieldSpecDto.getId());
            this.setVisibility(GONE);
        }
    }

    private void setModalityRecycleView(Context context) {
        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.biometricRecyclerView);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, gridColumnCount));

        for(Map.Entry<Modality, List<String>> entry : identifiedModalities.entrySet()) {
            modalities.add(entry.getKey());
        }

        addOrRemoveExceptionModality();

        // Initialize the adapter and set it to the RecyclerView.
        modalityAdapter = new ModalityAdapter(context, getCaptureStatuses(), modalities, fieldSpecDto.getId(), "Registration");
        mRecyclerView.setAdapter(modalityAdapter);

        // Notify the adapter of the change.
        modalityAdapter.notifyDataSetChanged();
    }

    public void updateAdapterItems() {
        addOrRemoveExceptionModality();
        getCaptureStatuses();
        // Notify the adapter of the change.
        modalityAdapter.notifyDataSetChanged();
    }


    private void addOrRemoveExceptionModality() {
        boolean isExceptionPresent = registrationDto.EXCEPTIONS.entrySet()
                .stream()
                .anyMatch( e -> e.getKey().startsWith(fieldSpecDto.getId()) && e.getValue() != null && !e.getValue().isEmpty());

        modalities.remove(Modality.EXCEPTION_PHOTO);

        if(isExceptionPresent && fieldSpecDto.isExceptionPhotoRequired()) {
            modalities.add(Modality.EXCEPTION_PHOTO);
        }
    }

    private void setupConfiguredBioAttributes() {
        //map with modality name and values of configured attributes
        //non-configured attributes should be blocked out
        for(Modality modality : Modality.values()) {
            identifiedModalities.put(modality, ListUtils.intersection(this.bioAttributes,
                    modality.getAttributes()));
        }
    }

    private HashMap<Modality, Boolean> getCaptureStatuses() {
        for(Modality modality : modalities) {
            int threshold = biometricsService.getModalityThreshold(modality);
            int allowedAttempts = biometricsService.getAttemptsCount(modality);
            List<BiometricsDto> list = registrationDto.getBestBiometrics(fieldSpecDto.getId(), modality);
            if(list.isEmpty()) {
                statuses.put(modality, false);
                continue;
            }

            long exceptions = list.stream().filter( o -> o.isException()).count();
            double totalScores = list.stream().mapToDouble(BiometricsDto::getQualityScore).sum();
            int score = (int)Math.ceil(totalScores/(modality.getAttributes().size() - exceptions));
            int retries = list.get(0).getNumOfRetries();
            //will be true only in below 3 cases
            //1. All attributes are marked as exception
            //2. average quality score is greater or equal to configured threshold
            //3. exhausted all the available attempts
            statuses.put(modality, exceptions == modality.getAttributes().size() || score >= threshold || retries >= allowedAttempts);
        }
        return statuses;
    }
}
