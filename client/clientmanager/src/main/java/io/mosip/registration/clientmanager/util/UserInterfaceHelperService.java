package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import com.gemalto.jp2.JP2Decoder;
import io.mosip.biometrics.util.face.FaceBDIR;
import io.mosip.biometrics.util.finger.FingerBDIR;
import io.mosip.biometrics.util.iris.IrisBDIR;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.uispec.ConditionalBioAttrDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.RequiredDto;
import io.mosip.registration.keymanager.util.CryptoUtil;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.*;

@Singleton
public class UserInterfaceHelperService {

    private static final String TAG = UserInterfaceHelperService.class.getSimpleName();
    private static final String MVEL_CONTEXT_KEY = "identity";
    private static final String MVEL_ENGINE_TYPE = "MVEL";
    private Context context;

    @Inject
    public UserInterfaceHelperService(Context context) {
        this.context = context;
    }


    public static boolean isFieldVisible(FieldSpecDto fieldSpecDto, Map<String, Object> dataContext) {
        boolean visible = true;
        if (fieldSpecDto.getVisible() != null && fieldSpecDto.getVisible().getEngine().equalsIgnoreCase(MVEL_ENGINE_TYPE)
                && fieldSpecDto.getVisible().getExpr() != null) {
            visible = evaluateMvel(fieldSpecDto.getVisible().getExpr(), dataContext);
        }
        return visible;
    }

    public static boolean isRequiredField(FieldSpecDto fieldSpecDto, Map<String, Object> dataContext) {
        boolean required = fieldSpecDto.getRequired() == null ? false : fieldSpecDto.getRequired();
        if (fieldSpecDto.getRequiredOn() != null && !fieldSpecDto.getRequiredOn().isEmpty()) {
            Optional<RequiredDto> expression = fieldSpecDto.getRequiredOn().stream()
                    .filter(field -> MVEL_ENGINE_TYPE.equalsIgnoreCase(field.getEngine()) && field.getExpr() != null).findFirst();

            if (expression.isPresent()) {
                required = evaluateMvel(expression.get().getExpr(), dataContext);
            }
        }
        return required;
    }

    public static List<String> getRequiredBioAttributes(FieldSpecDto fieldSpecDto, Map<String, Object> dataContext) {
        if(!isRequiredField(fieldSpecDto, dataContext))
            return Collections.EMPTY_LIST;

        if(fieldSpecDto.getConditionalBioAttributes() != null) {
            ConditionalBioAttrDto selectedCondition = getConditionalBioAttributes(fieldSpecDto, dataContext);
            if(selectedCondition != null)
                return selectedCondition.getBioAttributes();
        }
        return fieldSpecDto.getBioAttributes();
    }

    public static ConditionalBioAttrDto getConditionalBioAttributes(FieldSpecDto fieldSpecDto, Map<String, Object> dataContext) {
        if(fieldSpecDto.getConditionalBioAttributes() == null || fieldSpecDto.getConditionalBioAttributes().isEmpty())
            return null;

        Optional<ConditionalBioAttrDto> result = fieldSpecDto.getConditionalBioAttributes().stream().filter(c ->
                c.getAgeGroup().equalsIgnoreCase((String) dataContext.getOrDefault(RegistrationConstants.AGE_GROUP,
                        RegistrationConstants.DEFAULT_AGE_GROUP)))
                .findFirst();

        if(!result.isPresent()) {
            result = fieldSpecDto.getConditionalBioAttributes().stream().filter(c ->
                    (c.getAgeGroup().equalsIgnoreCase(
                            (String) dataContext.getOrDefault(RegistrationConstants.AGE_GROUP,
                                    RegistrationConstants.DEFAULT_AGE_GROUP))) ||
                            (c.getAgeGroup().equalsIgnoreCase("ALL"))).findFirst();
        }
        return result.isPresent() ? result.get() : null;
    }

    public static boolean evaluateMvel(String expression,  Map<String, Object> dataContext) {
        try {
            Map context = new HashMap();
            context.put(MVEL_CONTEXT_KEY, dataContext);
            VariableResolverFactory resolverFactory = new MapVariableResolverFactory(context);
            return MVEL.evalToBoolean(expression, resolverFactory);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to evaluate mvel expression", t);
        }
        return false;
    }


    public static Bitmap getFaceBitMap(BiometricsDto biometricsDto) {
        if(biometricsDto == null)
            return null;
        try(ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(biometricsDto.getBioValue()));
            DataInputStream inputStream = new DataInputStream(bais);) {
            FaceBDIR faceBDIR = new FaceBDIR(inputStream);
            byte[] bytes = faceBDIR.getRepresentation().getRepresentationData().getImageData().getImage();
            return new JP2Decoder(bytes).decode();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static Bitmap getFingerBitMap(BiometricsDto biometricsDto) {
        if(biometricsDto == null)
            return null;
        try(ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(biometricsDto.getBioValue()));
            DataInputStream inputStream = new DataInputStream(bais);) {
            FingerBDIR fingerBDIR = new FingerBDIR(inputStream);
            byte[] bytes = fingerBDIR.getRepresentation().getRepresentationBody().getImageData().getImage();
            return new JP2Decoder(bytes).decode();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static Bitmap getIrisBitMap(BiometricsDto biometricsDto) {
        if(biometricsDto == null)
            return null;
        try(ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(biometricsDto.getBioValue()));
            DataInputStream inputStream = new DataInputStream(bais);) {
            IrisBDIR irisBDIR = new IrisBDIR(inputStream);
            byte[] bytes = irisBDIR.getRepresentation()
                    .getRepresentationData().getImageData().getImage();
            return new JP2Decoder(bytes).decode();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static Bitmap combineBitmaps(List<Bitmap> images, Bitmap missingImage) {
        // Get the size of the images combined side by side.
        int width = 0;
        int height = 0;
        for(Bitmap image : images) {
            //in case image is not present, replace null with missingImage
            int imageH = (image == null) ? missingImage.getHeight() : image.getHeight();
            int imageW = (image == null) ? missingImage.getWidth() : image.getWidth();
            width = width + imageW;
            height = imageH > height ? imageH : height;
        }

        // Create a Bitmap large enough to hold both input images and a canvas to draw to this
        // combined bitmap.
        Bitmap combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combined);

        // Render both input images into the combined bitmap and return it.
        float left = 0f;
        float top = 0f;
        for(Bitmap image : images) {
            if(image == null)
                image = missingImage;
            canvas.drawBitmap(image, left, top, null);
            left = left + image.getWidth();
        }
        return combined;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }
    }
}
