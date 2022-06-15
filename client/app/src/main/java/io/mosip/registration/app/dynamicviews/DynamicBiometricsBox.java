package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import io.mosip.registration.app.R;
import io.mosip.registration.app.util.ClientConstants;
import io.mosip.registration.app.viewmodel.CustomViewPager2Adapter;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import org.apache.commons.collections4.ListUtils;

import java.util.*;

import static io.mosip.registration.app.util.ClientConstants.FIELD_LABEL_TEMPLATE;
import static io.mosip.registration.app.util.ClientConstants.REQUIRED_FIELD_LABEL_TEMPLATE;

public class DynamicBiometricsBox extends LinearLayout implements DynamicView {

    private static final String TAG = DynamicBiometricsBox.class.getSimpleName();

    RegistrationDto registrationDto = null;
    FieldSpecDto fieldSpecDto = null;
    List<String> bioAttributes = null;
    final int layoutId = R.layout.dynamic_biometrics_box;
    LinkedHashMap<Modality, List<String>> identifiedModalities = new LinkedHashMap<>();
    ViewPager2 viewPager2;
    TabLayout tabLayout;

    public DynamicBiometricsBox(Context context, FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.registrationDto = registrationDto;
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

        setupBiometricViewPager2(context);

        ((TextView)findViewById(R.id.biometric_label)).setText(Html.fromHtml(isRequired() ?
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
        return bioAttributes.stream().allMatch( attr -> registrationDto.hasBiometric(fieldSpecDto.getId(), attr));
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

    private void setupBiometricViewPager2(Context context) {
        viewPager2 = findViewById(R.id.bio_viewpager);
        tabLayout = findViewById(R.id.bio_tablayout);

        List<Modality> modalities = new ArrayList<>();
        for(Map.Entry<Modality, List<String>> entry : identifiedModalities.entrySet()) {
            modalities.add(entry.getKey());
        }

        CustomViewPager2Adapter adapter = new CustomViewPager2Adapter(context, modalities, identifiedModalities);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (modalities.get(position)) {
                            case FACE: tab.setText(R.string.face_label);
                                break;
                            case FINGERPRINT_SLAB_LEFT: tab.setText(R.string.left_slap);
                                break;
                            case FINGERPRINT_SLAB_RIGHT: tab.setText(R.string.right_slap);
                                break;
                            case FINGERPRINT_SLAB_THUMBS: tab.setText(R.string.thumbs_label);
                                break;
                            case IRIS_DOUBLE: tab.setText(R.string.double_iris);
                                break;
                        }
                    }
                }).attach();

        // To get swipe event of viewpager2
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            // This method is triggered when there is any scrolling activity for the current page
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            // triggered when you select a new page
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            // triggered when there is
            // scroll state will be changed
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void setupConfiguredBioAttributes() {
        //map with modality name and values of configured attributes
        //non-configured attributes should be blocked out
        for(Modality modality : Modality.values()) {
            identifiedModalities.put(modality, ListUtils.intersection(this.bioAttributes,
                    modality.getAttributes()));
        }
    }
}
