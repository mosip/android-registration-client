package io.mosip.registration.app.activites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;
import androidx.annotation.Nullable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.sbi.*;
import io.mosip.registration.clientmanager.exception.BiometricsServiceException;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.service.Biometrics095Service;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ModalityActivity extends DaggerAppCompatActivity {

    private static final String TAG = ModalityActivity.class.getSimpleName();
    private static final String COLON_SEPARATED_MODULE_NAME = "%s: %s";
    private String callbackId;
    private Modality currentModality;
    private String fieldId;
    private String purpose;

    @Inject
    RegistrationService registrationService;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    AuditManagerService auditManagerService;

    @Inject
    GlobalParamRepository globalParamRepository;

    @Inject
    Biometrics095Service biometricsService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modality_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        currentModality = (Modality) getIntent().getSerializableExtra("modality");
        fieldId = getIntent().getStringExtra("fieldId");
        purpose = getIntent().getStringExtra("purpose");

        displayCapturedImage(fieldId);
    }

    private void resetBioCaptureImageView(Modality modality) {
        ImageView modalityImage = findViewById(R.id.current_modality_image);
        switch (modality) {
            case FACE:
                getSupportActionBar().setTitle(R.string.face_label);
                modalityImage.setImageResource(R.drawable.face);
                break;
            case FINGERPRINT_SLAB_LEFT:
                getSupportActionBar().setTitle(R.string.left_slap);
                modalityImage.setImageResource(R.drawable.left_palm);
                overlayLeftPalmExceptionImage(this, modalityImage);
                break;
            case FINGERPRINT_SLAB_RIGHT:
                getSupportActionBar().setTitle(R.string.right_slap);
                modalityImage.setImageResource(R.drawable.right_palm);
                overlayRightPalmExceptionImage(this, modalityImage);
                break;
            case FINGERPRINT_SLAB_THUMBS:
                getSupportActionBar().setTitle(R.string.thumbs_label);
                modalityImage.setImageResource(R.drawable.thumbs);
                overlayThumbsExceptionImage(this, modalityImage);
                break;
            case IRIS_DOUBLE:
                getSupportActionBar().setTitle(R.string.double_iris);
                modalityImage.setImageResource(R.drawable.double_iris);
                overlayExceptionImage(this, modalityImage);
                break;
        }

        overlayThresholdOnProgressBar(this, getModalityThreshold());
        setupScoreBar(0);
        setupAttemptButtons();
    }

    private void overlayThresholdOnProgressBar(Context context, int threshold) {
        FrameLayout layout = findViewById(R.id.bio_score_frame);
        ProgressBar progressBar = findViewById(R.id.bio_score_bar);
        ViewTreeObserver vto = progressBar.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                progressBar.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalWidth = progressBar.getMeasuredWidth();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50,50);
                ImageView iv = new ImageView(context);
                iv.setAdjustViewBounds(true);
                iv.setImageBitmap(getThresholdBitmap(threshold+""));
                float computed_x = (finalWidth * (threshold/100f));
                iv.setX(computed_x + progressBar.getX());
                iv.setY(progressBar.getY());
                layout.addView(iv, layoutParams);
                return true;
            }
        });
    }


    private Bitmap getThresholdBitmap(String text) {
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.green_round);
        try {
            Bitmap.Config config = src.getConfig();
            if(config == null){
                config = Bitmap.Config.ARGB_8888;
            }
            Bitmap newBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), config);
            Canvas newCanvas = new Canvas(newBitmap);
            newCanvas.drawBitmap(src, 0, 0, null);
            if(text != null) {
                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.BLACK);
                paintText.setTextSize(85);
                paintText.setStyle(Paint.Style.FILL);
                Rect rectText = new Rect();
                paintText.getTextBounds(text, 0, text.length(), rectText);
                newCanvas.drawText(text,2, rectText.height(), paintText);
            }
            return newBitmap;
        } catch (Throwable t) {
            Log.e(TAG, t.getMessage(), t);
        }
        return src;
    }

    private void overlayRightPalmExceptionImage(Context context, ImageView imageView) {
        LinearLayout layout = findViewById(R.id.exceptionImages);
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalHeight = imageView.getMeasuredHeight();
                int finalWidth = imageView.getMeasuredWidth();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150,150);
                ImageView index = getImageView(context, R.integer.right_index_x, R.integer.right_index_y, finalWidth, finalHeight, "rightIndex");
                layout.addView(index, 0, layoutParams);
                ImageView middle = getImageView(context, R.integer.right_middle_x, R.integer.right_middle_y, finalWidth, finalHeight, "rightMiddle");
                layout.addView(middle, 1, layoutParams);
                ImageView ring = getImageView(context, R.integer.right_ring_x, R.integer.right_ring_y, finalWidth, finalHeight, "rightRing");
                layout.addView(ring, 2, layoutParams);
                ImageView little = getImageView(context, R.integer.right_little_x, R.integer.right_little_y, finalWidth, finalHeight, "rightLittle");
                layout.addView(little, 3, layoutParams);
                return true;
            }
        });
    }

    private void overlayLeftPalmExceptionImage(Context context, ImageView imageView) {
        LinearLayout layout = findViewById(R.id.exceptionImages);
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalHeight = imageView.getMeasuredHeight();
                int finalWidth = imageView.getMeasuredWidth();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150,150);
                ImageView little = getImageView(context, R.integer.left_little_x, R.integer.left_little_y, finalWidth, finalHeight, "leftLittle");
                layout.addView(little, 0, layoutParams);
                ImageView ring = getImageView(context, R.integer.left_ring_x, R.integer.left_ring_y, finalWidth, finalHeight, "leftRing");
                layout.addView(ring, 1, layoutParams);
                ImageView middle = getImageView(context, R.integer.left_middle_x, R.integer.left_middle_y, finalWidth, finalHeight, "leftMiddle");
                layout.addView(middle, 2, layoutParams);
                ImageView index = getImageView(context, R.integer.left_index_x, R.integer.left_index_y, finalWidth, finalHeight, "leftIndex");
                layout.addView(index, 3, layoutParams);
                return true;
            }
        });
    }

    private void overlayThumbsExceptionImage(Context context, ImageView imageView) {
        LinearLayout layout = findViewById(R.id.exceptionImages);
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalHeight = imageView.getMeasuredHeight();
                int finalWidth = imageView.getMeasuredWidth();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                ImageView left = getImageView(context, R.integer.left_thumb_x, R.integer.left_thumb_y, finalWidth, finalHeight, "leftThumb");
                layout.addView(left, 0, layoutParams);
                ImageView right = getImageView(context, R.integer.right_thumb_x, R.integer.right_thumb_y, finalWidth, finalHeight, "rightThumb");
                layout.addView(right, 1, layoutParams);
                return true;
            }
        });
    }

    private void overlayExceptionImage(Context context, ImageView imageView) {
        LinearLayout layout = findViewById(R.id.exceptionImages);
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalHeight = imageView.getMeasuredHeight();
                int finalWidth = imageView.getMeasuredWidth();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                ImageView left = getImageView(context, R.integer.left_iris_x, R.integer.left_iris_y, finalWidth, finalHeight, "leftEye");
                layout.addView(left, 0, layoutParams);
                ImageView right = getImageView(context, R.integer.right_iris_x, R.integer.right_iris_y, finalWidth, finalHeight, "rightEye");
                layout.addView(right, 1, layoutParams);
                return true;
            }
        });
    }

    private ImageView getImageView(Context context, int x, int y, int parentWidth, int parentHeight, String subType) {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(x, typedValue, true);
        float xOffset = typedValue.getFloat();
        getResources().getValue(y, typedValue, true);
        float yOffset = typedValue.getFloat();
        ImageView iv = new ImageView(context);
        iv.setAdjustViewBounds(true);
        iv.setImageResource(R.drawable.blue_cross_mark);
        int height = iv.getDrawable().getIntrinsicHeight();
        int width = iv.getDrawable().getIntrinsicWidth();
        iv.setX((parentWidth*xOffset)-(width/2));
        iv.setY((parentHeight*yOffset)-(height/2));
        iv.setVisibility(View.VISIBLE);

        try {
            iv.setAlpha((this.registrationService.getRegistrationDto().isBioException(fieldId, currentModality, subType))  ? 1.0f : 0.0f);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set alpha " + subType, e);
        }

        iv.setOnClickListener(v -> {
            Toast.makeText(this, "Clicked on exception : " + subType, Toast.LENGTH_LONG).show();
            try {
                if(iv.getAlpha() == 0.0f) { //currently transparent, user clicked to make it visible
                    iv.setAlpha(1.0f);
                    this.registrationService.getRegistrationDto().addBioException(fieldId, currentModality, subType);
                }
                else {
                    iv.setAlpha(0.0f);
                    this.registrationService.getRegistrationDto().removeBioException(fieldId, currentModality, subType);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to add / remove bio exception " + subType, e);
            }
        });
        return iv;
    }

    public void scan_modality(View view) {
        auditManagerService.audit(AuditEvent.BIOMETRIC_CAPTURE, Components.REGISTRATION.getId(),
                String.format(COLON_SEPARATED_MODULE_NAME, Components.REGISTRATION.getName(), currentModality.name()));
        discoverSBI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    parseDiscoverResponse(data.getExtras());
                    break;
                case 2:
                    parseDeviceInfoResponse(data.getExtras());
                    break;
                case 3:
                    parseRCaptureResponse(data.getExtras());
                    break;
            }
        }
    }

    private void queryPackage(Intent intent) throws ClientCheckedException {
        List activities = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
        //if(activities.size() == 0)
        //    throw new ClientCheckedException("Supported apps not found!");
    }

    private void discoverSBI() {
        try {
            Toast.makeText(this, "Started to discover SBI", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setAction(RegistrationConstants.DISCOVERY_INTENT_ACTION);
            queryPackage(intent);
            DiscoverRequest discoverRequest = new DiscoverRequest();
            discoverRequest.setType(currentModality.getSingleType().name());
            intent.putExtra(RegistrationConstants.SBI_INTENT_REQUEST_KEY, objectMapper.writeValueAsBytes(discoverRequest));
            this.startActivityForResult(intent, 1);
        } catch (Exception ex) {
            auditManagerService.audit(AuditEvent.DISCOVER_SBI_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void info(String callbackId) {
        if (callbackId == null) {
            Toast.makeText(this, "No SBI found!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Intent intent = new Intent();
            intent.setAction(callbackId + RegistrationConstants.D_INFO_INTENT_ACTION);
            queryPackage(intent);
            Toast.makeText(getApplicationContext(), "Initiating Device info request : " + callbackId,
                    Toast.LENGTH_LONG).show();
            startActivityForResult(intent, 2);
        } catch (ClientCheckedException ex) {
            auditManagerService.audit(AuditEvent.DEVICE_INFO_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void rcapture(String callbackId, String deviceId) {
        if (deviceId == null || callbackId == null) {
            Toast.makeText(this, "No SBI found!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Intent intent = new Intent();
            //callbackId = callbackId.replace("\\.info","");
            intent.setAction(callbackId + RegistrationConstants.R_CAPTURE_INTENT_ACTION);
            queryPackage(intent);
            Toast.makeText(this, "Initiating capture request : " + callbackId, Toast.LENGTH_LONG).show();
            CaptureRequest captureRequest = biometricsService.getRCaptureRequest(currentModality, deviceId,
                    getExceptionAttributes());
            intent.putExtra("input", objectMapper.writeValueAsBytes(captureRequest));
            startActivityForResult(intent, 3);
        } catch (Exception ex) {
            auditManagerService.audit(AuditEvent.R_CAPTURE_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private List<String> getExceptionAttributes() {
        return currentModality.getAttributes().stream()
                .filter( it ->
                {
                    try {
                        return this.registrationService.getRegistrationDto().isBioException(fieldId, currentModality, it);
                    } catch (Exception e) {}
                    return false;
                })
                .collect(Collectors.toList());
    }

    private void parseDiscoverResponse(Bundle bundle) {
        try {
            byte[] bytes = bundle.getByteArray(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            callbackId = biometricsService.handleDiscoveryResponse(currentModality, bytes);
        } catch (BiometricsServiceException e) {
            auditManagerService.audit(AuditEvent.DISCOVER_SBI_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse discover response", e);
        }
        info(callbackId);
    }

    private void parseDeviceInfoResponse(Bundle bundle) {
        String callbackId = null;
        String serialNo = null;
        try {
            byte[] bytes = bundle.getByteArray(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            String[] result = biometricsService.handleDeviceInfoResponse(currentModality, bytes);
            callbackId = result[0];
            serialNo = result[1];
            Log.i(TAG, callbackId + " --> " + serialNo);
        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.DEVICE_INFO_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse device info response", e);
        }
        rcapture(callbackId, serialNo);
    }

    private void parseRCaptureResponse(Bundle bundle) {
        try {
            Uri uri = bundle.getParcelable(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            InputStream respData = getContentResolver().openInputStream(uri);
            List<BiometricsDto> biometricsDtoList = biometricsService.handleRCaptureResponse(currentModality, respData,
                    getExceptionAttributes());

            biometricsDtoList.forEach( dto -> {
                try {
                    this.registrationService.getRegistrationDto().addBiometric(fieldId,
                            Modality.getBioAttribute(dto.getBioSubType()), dto);
                } catch (Exception ex) { }
            });
            this.registrationService.getRegistrationDto().incrementBioAttempt(fieldId, currentModality);
            displayCapturedImage(fieldId);

        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.R_CAPTURE_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse rcapture response", e);
            Toast.makeText(this, "Failed parsing Capture response : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayCapturedImage(String fieldId) {
        List<BiometricsDto> list = null;
        try {
           list = this.registrationService.getRegistrationDto().getBiometrics(fieldId, currentModality);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get captured list of biometrics", e);
        }

        if (list == null || list.isEmpty()) {
            resetBioCaptureImageView(currentModality);
            return;
        }

        //Remove all the exception views
        LinearLayout layout = findViewById(R.id.exceptionImages);
        layout.removeAllViews();

        Bitmap missingImage = BitmapFactory.decodeResource(getResources(), R.drawable.blue_cross_mark);
        ImageView imageView = findViewById(R.id.current_modality_image);
        List<Bitmap> bitmaps = new ArrayList<>();
        int total = 0, score = 0;
        switch (currentModality) {
            case FACE:
                imageView.setImageBitmap(UserInterfaceHelperService.getFaceBitMap(list.get(0)));
                score = (int) list.get(0).getQualityScore(); total=1;
                break;
            case FINGERPRINT_SLAB_LEFT:
            case FINGERPRINT_SLAB_RIGHT:
            case FINGERPRINT_SLAB_THUMBS:
                for(String subType : Modality.getSpecBioSubType(currentModality.getAttributes())) {
                    BiometricsDto finger = getBiometricsDto(subType, list);
                    bitmaps.add(UserInterfaceHelperService.getFingerBitMap(finger));
                    score += finger == null ? 0 : finger.getQualityScore();
                    total += finger == null ? 0 : 1;
                }
                imageView.setImageBitmap(UserInterfaceHelperService.combineBitmaps(bitmaps, missingImage));
                break;
            case IRIS_DOUBLE:
                for(String subType : Modality.getSpecBioSubType(currentModality.getAttributes())) {
                    BiometricsDto iris = getBiometricsDto(subType, list);
                    bitmaps.add(UserInterfaceHelperService.getIrisBitMap(iris));
                    score += iris == null ? 0 : iris.getQualityScore();
                    total += iris == null ? 0 : 1;
                }
                imageView.setImageBitmap(UserInterfaceHelperService.combineBitmaps(bitmaps, missingImage));
                break;
        }
        setupScoreBar(score/total);
        Toast.makeText(getApplicationContext(), "Registration Capture completed", Toast.LENGTH_LONG).show();
    }

    private BiometricsDto getBiometricsDto(String subType, List<BiometricsDto> list) {
        Optional<BiometricsDto> result = list.stream().filter(dto -> subType.equals(dto.getBioSubType())).findFirst();
        if(result.isPresent())
            return result.get();
        return null;
    }

    private void setupScoreBar(int score) {
        TextView textView = findViewById(R.id.current_score);
        textView.setText(score+"");
        ProgressBar progressBar = findViewById(R.id.bio_score_bar);
        progressBar.setProgress(score);
        progressBar.setProgressTintList((score <= getModalityThreshold()) ? ColorStateList.valueOf(Color.RED) :
                ColorStateList.valueOf(Color.GREEN));
    }

    private void setupAttemptButtons() {
        LinearLayout layout = findViewById(R.id.bio_action_buttons);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(R.dimen.bio_attempts_margin,R.dimen.bio_attempts_margin,R.dimen.bio_attempts_margin,R.dimen.bio_attempts_margin);
        for(int i=1;i<=getAttemptsCount();i++){
            FloatingActionButton button = new FloatingActionButton(this);
            button.setTag(i);
            button.setEnabled(true);
            button.setImageBitmap(textAsBitmap(String.format("#%d", i), 15, Color.WHITE));
            button.setOnClickListener(v -> {
                Toast.makeText(this, "Attempt : " + button.getTag(), Toast.LENGTH_LONG).show();
            });
            layout.addView(button, i, layoutParams);
        }
    }

    //method to convert your text to image
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private int getModalityThreshold() {
        switch (currentModality) {
            case FINGERPRINT_SLAB_LEFT:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.LEFT_SLAP_THRESHOLD_KEY);
            case FINGERPRINT_SLAB_RIGHT:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.RIGHT_SLAP_THRESHOLD_KEY);
            case FINGERPRINT_SLAB_THUMBS:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.THUMBS_THRESHOLD_KEY);
            case IRIS_DOUBLE:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.IRIS_THRESHOLD_KEY);
            case FACE:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.FACE_THRESHOLD_KEY);
        }
        return 0;
    }

    private int getAttemptsCount() {
        switch (currentModality) {
            case FINGERPRINT_SLAB_LEFT:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.LEFT_SLAP_ATTEMPTS_KEY);
            case FINGERPRINT_SLAB_RIGHT:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.RIGHT_SLAP_ATTEMPTS_KEY);
            case FINGERPRINT_SLAB_THUMBS:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.THUMBS_ATTEMPTS_KEY);
            case IRIS_DOUBLE:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.IRIS_ATTEMPTS_KEY);
            case FACE:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.FACE_ATTEMPTS_KEY);
        }
        return 0;
    }
}
