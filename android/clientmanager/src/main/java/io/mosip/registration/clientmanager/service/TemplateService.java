package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.packetmanager.dto.SimpleType;

@Singleton
public class TemplateService {

    private static final String TAG = TemplateService.class.getSimpleName();
    private static final String SLASH = "/";
    private static final String TEMPLATE_TYPE_CODE = "reg-android-preview-template-part";

    private Context appContext;

    MasterDataService masterDataService;

    IdentitySchemaRepository identitySchemaRepository;

    public TemplateService(Context appContext, MasterDataService masterDataService, IdentitySchemaRepository identitySchemaRepository){
        this.appContext = appContext;
        this.masterDataService = masterDataService;
        this.identitySchemaRepository = identitySchemaRepository;
    }

    public String getTemplate(RegistrationDto registrationDto, boolean isPreview) throws Exception {
        StringWriter writer = new StringWriter();
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        String templateText = "<!DOCTYPE html><html $rtl><head><style>body{font-family:\"Roboto\";}table, .head{font-size:13px;}.demoDiv{width: 85%;float: left;}.photoDiv{width: 15%;float:right;}.form{display:inline-block;}.section{align:center;padding:30px;width:400px;margin:auto;}.headings{color:#808080;font-size:11px;}p{display:inline;}.dataTable{word-wrap:break-word;table-layout:fixed;width:70%;}.headerTable{width:90%;}.uinHeaderTable{width:100%;}h5{display:inline;position:absolute;}.iris{top:-15px;left:152px;}.irisWithoutException{top:-15px;left:114px;}.tableWithoutException{width:50%;text-align:center;table-layout:fixed;margin:0 auto;}.biometricsTable{width:100%;text-align:center;table-layout:fixed;}.biometrics{position:relative;}.leftLittle{top:15px;left:11px;display:inline;position:absolute;}.leftRing{top:3px;left:23px;}.leftMiddle{top:-7px;left:37px;}.leftIndex{top:3px;left:54px;}.rightIndex{top:3px;left:26px;}.rightMiddle{top:-7px;left:39px;}.rightRing{top:3px;left:55px;}.rightLittle{top:15px;left:68px;}.leftThumb{top:-4px;left:24px;}.rightThumb{top:-4px;left:54px;}.parentStyle{top:-15px;left:152px;}.parentIris1{top:-15px;left:212px;}.parentIris2{top:-15px;left:214px;}li span{color:black;font-size:12px;} li{color:lightgrey}button{float:right;font-size:12px;border:none;background-color:transparent;outline:none;}button:active{background-color:black;color:white;}.bottom{vertical-align:bottom;}.consent-block{font-size:13px;border-radius: 8px;margin-top: 10px;margin-bottom: 10px;border: 2px solid #E88E3F;padding-top: -50px;padding:10px;}.consent-text{margin-left: 7px;margin-bottom: 4px;}input[type=\"radio\"] {display: none;} input[type=\"radio\"] + label:before { content: \"\"; display: inline-block; width: 15px; height: 15px; padding: 3px; margin-right: 5px; background-clip: content-box; border: 1px solid grey; background-color: #fff; border-radius: 50%;}input[type=\"radio\"]:checked + label:before { background-color: #FF4081; border-color:#FF4081;}*,*:before,*:after { box-sizing: border-box;}.consent-block label { display: inline-flex; align-items: center;}.demoDiv>table{width:100%}.demoDiv>table>tbody>tr>th{color:#808080;font-size:10px; width:25%; text-align: left;padding: 2px}.demoDiv>table>tbody>tr>td{width:25%; text-align: left;padding: 2px}</style><script>function changeColour(element) { var div = document.getElementById(\"radioId\"); if(element.value == \"yes\"){div.style = \"border: 2px solid #68A933\";} if(element.value == \"no\"){div.style = \"border: 2px solid #FF0000\";}} document.onreadystatechange = function () {if(document.readyState === \"complete\"){var body = document.getElementsByTagName(\"body\")[0]; body.addEventListener(\"dragstart\", function(event) {event.preventDefault();}); body.addEventListener(\"selectstart\", function(event) {event.preventDefault();});}}</script></head><body> <div class=section> <div class=form> <table class=headerTable> <tr> <td class=bottom><p class=headings>${ApplicationIDLabel}</p><br/>${ApplicationID}</td> #if ( $UIN ) <td class=bottom><p class=headings>${UINLabel}</p><br/>${UIN}</td> #end <td class=bottom><p class=headings>${DateLabel}</p><br/>${Date}</td> </tr> </table> <br/> #if ( $name ) <table class=headerTable> <tr><td><p class=headings>${NameLabel}</p><br/>${NameValue}</td></tr> </table> #end <br/> <hr/> <p class=head><b>${DemographicInfo}</b></p><hr/><div class=\"demoDiv\"><table> <tr> #if( $demographics.get(\"fullName\") ) <th>$demographics.get(\"fullName\").get(\"label\")</th> #end </tr><tr> #if( $demographics.get(\"fullName\") ) <td>$demographics.get(\"fullName\").get(\"value\")</td> #end </tr><tr> #if( $demographics.get(\"dateOfBirth\") ) <th>$demographics.get(\"dateOfBirth\").get(\"label\")</th> #end </tr><tr> #if( $demographics.get(\"dateOfBirth\") ) <td>$demographics.get(\"dateOfBirth\").get(\"value\")</td> #end </tr><tr> #if( $demographics.get(\"gender\") ) <th>$demographics.get(\"gender\").get(\"label\")</th> #end </tr><tr> #if( $demographics.get(\"gender\") ) <td>$demographics.get(\"gender\").get(\"value\")</td> #end </tr><tr> #if( $demographics.get(\"phone\") ) <th>$demographics.get(\"phone\").get(\"label\")</th> #end #if( $demographics.get(\"email\") ) <th>$demographics.get(\"email\").get(\"label\")</th> #end </tr><tr> #if( $demographics.get(\"phone\") ) <td>$demographics.get(\"phone\").get(\"value\")</td> #end #if( $demographics.get(\"email\") ) <td>$demographics.get(\"email\").get(\"value\")</td> #end </tr></table><table> #if( $demographics.get(\"addressLine1\") ) <hr/><h1 class=\"headings\">Address</h1> #end <tr> #if( $demographics.get(\"addressLine1\") ) <th>$demographics.get(\"addressLine1\").get(\"label\")</th> #end #if( $demographics.get(\"addressLine2\") ) <th>$demographics.get(\"addressLine2\").get(\"label\")</th> #end #if( $demographics.get(\"addressLine3\") ) <th>$demographics.get(\"addressLine3\").get(\"label\")</th> #end </tr><tr> #if( $demographics.get(\"addressLine1\") ) <td>$demographics.get(\"addressLine1\").get(\"value\")</td> #end #if( $demographics.get(\"addressLine2\") ) <td>$demographics.get(\"addressLine2\").get(\"value\")</td> #end #if( $demographics.get(\"addressLine3\") ) <td>$demographics.get(\"addressLine3\").get(\"value\")</td> #end </tr></table><table><tr> #if( $demographics.get(\"region\") ) <th>$demographics.get(\"region\").get(\"label\")</th> #end #if( $demographics.get(\"province\") ) <th>$demographics.get(\"province\").get(\"label\")</th> #end #if( $demographics.get(\"city\") ) <th>$demographics.get(\"city\").get(\"label\")</th> #end </tr><tr> #if( $demographics.get(\"region\") ) <td>$demographics.get(\"region\").get(\"value\")</td> #end #if( $demographics.get(\"province\") ) <td>$demographics.get(\"province\").get(\"value\")</td> #end #if( $demographics.get(\"city\") ) <td>$demographics.get(\"city\").get(\"value\")</td> #end </tr></table><table><tr> #if( $demographics.get(\"zone\") ) <th>$demographics.get(\"zone\").get(\"label\")</th> #end #if( $demographics.get(\"postalCode\") ) <th>$demographics.get(\"postalCode\").get(\"label\")</th> #end </tr><tr> #if( $demographics.get(\"zone\") ) <td>$demographics.get(\"zone\").get(\"value\")</td> #end #if( $demographics.get(\"postalCode\") ) <td>$demographics.get(\"postalCode\").get(\"value\")</td> #end </tr><tr>#if( $demographics.get(\"modeOfClaim\") ) <th>$demographics.get(\"modeOfClaim\").get(\"label\")</th> #end</tr><tr> #if( $demographics.get(\"modeOfClaim\") ) <td>$demographics.get(\"modeOfClaim\").get(\"value\")</td> #end</tr></table> #if( $demographics.get(\"parentOrGuardianName\") ) <hr/> <table> <h1 class=\"headings\">Gaurdian Details</h1><tr><th>$demographics.get(\"parentOrGuardianName\").get(\"label\")</th> #if( $demographics.get(\"parentOrGuardianRID\") ) <th>$demographics.get(\"parentOrGuardianRID\").get(\"label\")</th> #end #if( $demographics.get(\"parentOrGuardianUIN\") ) <th>$demographics.get(\"parentOrGuardianUIN\").get(\"label\")</th> #end </tr><tr><td>$demographics.get(\"parentOrGuardianName\").get(\"value\")</td> #if( $demographics.get(\"parentOrGuardianRID\") ) <td>$demographics.get(\"parentOrGuardianRID\").get(\"value\")</td> #end #if( $demographics.get(\"parentOrGuardianUIN\") ) <td>$demographics.get(\"parentOrGuardianUIN\").get(\"value\")</td> #end </tr></table> #end </div> #if ( $ApplicantImageSource ) <div class=\"photoDiv\"> <table> <tr> <td> <p>${Photo}</p> </td> </tr> <tr> <td><img src=${ApplicantImageSource} border=0 width=100 height=100/></td> </tr> </table> </div> #end <div style=\"width: 100%; height: 50%; clear:both\"></div> <br> <br/> <hr/> <p class=head><b>${DocumentsLabel}</b></p> <hr/> <table class=dataTable> #foreach( $key in $documents.keySet() ) <tr> <td> <p class=headings>$documents.get($key).get(\"label\")</p> <br/> <p> $documents.get($key).get(\"value\")</p> </td> </tr> #end </table> <br/> <hr/> <p class=head><b>${BiometricsLabel}</b></p> <hr/> #foreach( $key in $biometrics.keySet() ) <table> <tr> <td> $biometrics.get($key).get(\"label\")<br/> <p class=headings>${Fingers} ( $biometrics.get($key).get(\"FingerCount\") ),${Iris} ( $biometrics.get($key).get(\"IrisCount\") ),${Face} ( $biometrics.get($key).get(\"FaceCount\") )<br/></p> <br/> </td> </tr><table class=biometricsTable> <tr> #if( $biometrics.get($key).get(\"CapturedLeftEye\") ) <td><p class=headings>${LeftEyeLabel}</p> </td> #end #if( $biometrics.get($key).get(\"CapturedRightEye\") ) <td> <p class=headings>${RightEyeLabel}</p> </td> #end </tr> <tr> #if( $biometrics.get($key).get(\"CapturedLeftEye\") ) <td> #if( $biometrics.get($key).get(\"CapturedLeftEye\") ) <img src=$biometrics.get($key).get(\"CapturedLeftEye\") border=0 width=85 height=80/> #end </td> #end #if( $biometrics.get($key).get(\"CapturedRightEye\") ) <td> #if( $biometrics.get($key).get(\"CapturedRightEye\") ) <img src=$biometrics.get($key).get(\"CapturedRightEye\") border=0 width=85 height=80/> #end </td> #end </tr> </table> <table class=biometricsTable> <tr> #if( $biometrics.get($key).get(\"CapturedLeftSlap\") ) <td> <p class=headings>${LeftPalmLabel}</p> </td> #end #if( $biometrics.get($key).get(\"CapturedRightSlap\") ) <td> <p class=headings>${RightPalmLabel}</p> </td> #end #if( $biometrics.get($key).get(\"CapturedThumbs\") ) <td> <p class=headings>${ThumbsLabel}</p> </td> #end </tr> <tr> #if( $biometrics.get($key).get(\"CapturedLeftSlap\") ) <td style=\"text-align:-webkit-center\"> <img src=$biometrics.get($key).get(\"CapturedLeftSlap\") border=0 width=85 height=80/> </td> #end #if( $biometrics.get($key).get(\"CapturedRightSlap\") ) <td style=\"text-align:-webkit-center\"> <img src=$biometrics.get($key).get(\"CapturedRightSlap\") border=0 width=85 height=80/> </td> #end #if( $biometrics.get($key).get(\"CapturedThumbs\") ) <td style=\"text-align:-webkit-center\"> <img src=$biometrics.get($key).get(\"CapturedThumbs\") border=0 width=85 height=80/> </td> #end </tr> </table> <br/> <table class=biometricsTable> <tr> #if( $biometrics.get($key).get(\"FaceImageSource\") ) <td> <p class=headings>${FaceLabel}</p> </td> #end #if( $biometrics.get($key).get(\"subType\") == \"applicant\" && $ExceptionImageSource ) <td> <p class=headings>${ExceptionPhotoLabel}</p> </td> #end </tr> <tr> #if( $biometrics.get($key).get(\"FaceImageSource\") ) <td> <img src=$biometrics.get($key).get(\"FaceImageSource\") border=0 width=85 height=80/> </td> #end #if( $biometrics.get($key).get(\"subType\") == \"applicant\" && $ExceptionImageSource ) <td> <img src=${ExceptionImageSource} border=0 width=85 height=80/> </td> #end </tr> </table> </table> #end <br/> </div> </body> </html>";//this.masterDataService.getPreviewTemplateContent(TEMPLATE_TYPE_CODE,
               // registrationDto.getSelectedLanguages().get(0));

        InputStream is = new ByteArrayInputStream(templateText.getBytes(StandardCharsets.UTF_8));

        VelocityContext velocityContext = new VelocityContext();

        Double version = identitySchemaRepository.getLatestSchemaVersion();
        if (version == null)
            throw new Exception("No Schema found");
        List<FieldSpecDto> schemaFields = identitySchemaRepository.getAllFieldSpec(appContext, version);

        setBasicDetails(isPreview, registrationDto, velocityContext);

        Map<String, Map<String, Object>> demographicsData = new HashMap<>();
        Map<String, Map<String, Object>> documentsData = new HashMap<>();
        Map<String, Map<String, Object>> biometricsData = new HashMap<>();

        for (FieldSpecDto field : schemaFields) {
            switch (field.getType()) {
                case "documentType":
                    Map<String, Object> docData = getDocumentData(field, registrationDto, velocityContext);
                    if (docData != null) {
                        documentsData.put(field.getId(), docData);
                    }
                    break;

                case "biometricsType":
                    Map<String, Object> bioData = getBiometricData(field, registrationDto, isPreview, velocityContext);
                    if (bioData != null) {
                        biometricsData.put(field.getId(), bioData);
                    }
                    break;

                default:
                    Map<String, Object> demoData = getDemographicData(field, registrationDto);
                    if (demoData != null) {
                        demographicsData.put(field.getId(), demoData);
                    }
                    break;
            }
        }
        velocityContext.put("demographics", demographicsData);
        velocityContext.put("documents", documentsData);
        velocityContext.put("biometrics", biometricsData);

        velocityEngine.evaluate(velocityContext, writer, "templateManager-mergeTemplate", new InputStreamReader(is));
        return writer.toString();
    }

    private Map<String, Object> getBiometricData(FieldSpecDto field, RegistrationDto registrationDto, boolean isPreview, VelocityContext velocityContext) throws Exception {
        velocityContext.put("Fingers", appContext.getString(R.string.fingers));
        velocityContext.put("Iris", appContext.getString(R.string.double_iris));
        velocityContext.put("Face", appContext.getString(R.string.face_label));

        Map<String, Object> bioData = new HashMap<>();

        List<BiometricsDto> capturedFingers = registrationDto.getBestBiometrics(field.getId(), Modality.FINGERPRINT_SLAB_RIGHT);
        capturedFingers.addAll(registrationDto.getBestBiometrics(field.getId(), Modality.FINGERPRINT_SLAB_LEFT));
        capturedFingers.addAll(registrationDto.getBestBiometrics(field.getId(), Modality.FINGERPRINT_SLAB_THUMBS));
        List<BiometricsDto> capturedIris = registrationDto.getBestBiometrics(field.getId(), Modality.IRIS_DOUBLE);
        List<BiometricsDto> capturedFace = registrationDto.getBestBiometrics(field.getId(), Modality.FACE);

        bioData.put("FingerCount", capturedFingers.stream().filter(b -> b.getBioValue() != null).count());
        bioData.put("IrisCount", capturedIris.stream().filter(b -> b.getBioValue() != null).count());
        bioData.put("FaceCount", capturedFace.stream().filter(b -> b.getBioValue() != null).count()); //TODO check this
        bioData.put("subType", field.getSubType());
        bioData.put("label", getFieldLabel(field, registrationDto));

        Bitmap missingImage = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.wrong);
        Optional<BiometricsDto> result = capturedIris.stream()
                .filter(b -> b.getBioSubType().equalsIgnoreCase("Left")).findFirst();
        if (result.isPresent()) {
            BiometricsDto biometricsDto = result.get();
            bioData.put("LeftEye", (biometricsDto.getBioValue() != null) ? "&#10003;" : "&#10008;");
            setBiometricImage(bioData, "CapturedLeftEye", isPreview ? R.drawable.cross_mark : R.drawable.eye,
                    isPreview ? UserInterfaceHelperService.getIrisBitMap(biometricsDto) : null);
        }

        result = capturedIris.stream()
                .filter(b -> b.getBioSubType().equalsIgnoreCase("Right")).findFirst();
        if (result.isPresent()) {
            BiometricsDto biometricsDto = result.get();
            bioData.put("RightEye", (biometricsDto.getBioValue() != null) ? "&#10003;" : "&#10008;");
            setBiometricImage(bioData, "CapturedRightEye", isPreview ? R.drawable.cross_mark : R.drawable.eye,
                    isPreview ? UserInterfaceHelperService.getIrisBitMap(biometricsDto) : null);
        }

        if (!capturedFingers.isEmpty()) {
            List<String> leftFingers = Modality.FINGERPRINT_SLAB_LEFT.getAttributes();
            List<BiometricsDto> leftHandFingersDtoList = capturedFingers.stream().filter(b -> leftFingers.contains(Modality.getBioAttribute(b.getBioSubType()))).collect(Collectors.toList());
            if(!leftHandFingersDtoList.isEmpty()) {
                setFingerRankings(leftHandFingersDtoList, leftFingers, bioData);
                List<Bitmap> images  = new ArrayList<>();
                BiometricsDto lLittleFinger;
                BiometricsDto lRingFinger;
                BiometricsDto lMiddleFinger;
                BiometricsDto lIndexFinger;
                result = leftHandFingersDtoList.stream().filter(dto -> "Left LittleFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    lLittleFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(lLittleFinger));
                }

                result = leftHandFingersDtoList.stream().filter(dto -> "Left RingFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    lRingFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(lRingFinger));
                }
                result = leftHandFingersDtoList.stream().filter(dto -> "Left MiddleFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    lMiddleFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(lMiddleFinger));
                }
                result = leftHandFingersDtoList.stream().filter(dto -> "Left IndexFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    lIndexFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(lIndexFinger));
                }

                Bitmap leftHandBitmaps = UserInterfaceHelperService.combineBitmaps(images, missingImage);
                setBiometricImage(bioData, "CapturedLeftSlap", isPreview ? 0 : R.drawable.left_palm,
                        isPreview ? leftHandBitmaps : null);
            }

            List<String> rightFingers = Modality.FINGERPRINT_SLAB_RIGHT.getAttributes();
            List<BiometricsDto> rightHandFingersDtoList = capturedFingers.stream().filter(b -> rightFingers.contains(Modality.getBioAttribute(b.getBioSubType()))).collect(Collectors.toList());
            if(!rightHandFingersDtoList.isEmpty()) {
                setFingerRankings(rightHandFingersDtoList, Modality.FINGERPRINT_SLAB_RIGHT.getAttributes(), bioData);
                List<Bitmap> images  = new ArrayList<>();
                BiometricsDto rIndexFinger;
                BiometricsDto rMiddleFinger;
                BiometricsDto rRingFinger;
                BiometricsDto rLittleFinger;

                result = rightHandFingersDtoList.stream().filter(dto -> "Right IndexFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    rIndexFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(rIndexFinger));
                }

                result = rightHandFingersDtoList.stream().filter(dto -> "Right MiddleFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    rMiddleFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(rMiddleFinger));
                }

                result = rightHandFingersDtoList.stream().filter(dto -> "Right RingFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    rRingFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(rRingFinger));
                }

                result = rightHandFingersDtoList.stream().filter(dto -> "Right LittleFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    rLittleFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(rLittleFinger));
                }

                Bitmap rightHandBitmaps = UserInterfaceHelperService.combineBitmaps(images, missingImage);
                setBiometricImage(bioData, "CapturedRightSlap", isPreview ? 0 : R.drawable.right_palm,
                        isPreview ? rightHandBitmaps : null);
            }


            List<String> thumbs = Modality.FINGERPRINT_SLAB_THUMBS.getAttributes();
            List<BiometricsDto> thumbsDtoList = capturedFingers.stream().filter(b -> thumbs.contains(Modality.getBioAttribute(b.getBioSubType()))).collect(Collectors.toList());
            if(!thumbsDtoList.isEmpty()) {
                setFingerRankings(thumbsDtoList, Modality.FINGERPRINT_SLAB_THUMBS.getAttributes(), bioData);
                List<Bitmap> images  = new ArrayList<>();
                BiometricsDto lThumb;
                BiometricsDto rThumb;

                result = thumbsDtoList.stream().filter(dto -> "Left Thumb".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    lThumb = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(lThumb));
                }

                result = thumbsDtoList.stream().filter(dto -> "Right Thumb".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    rThumb = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(rThumb));
                }

                Bitmap thumbsBitmap = UserInterfaceHelperService.combineBitmaps(images, missingImage);
                setBiometricImage(bioData, "CapturedThumbs", isPreview ? 0 : R.drawable.thumbs,
                        isPreview ? thumbsBitmap : null);
            }

        }

        if (!capturedFace.isEmpty()) {
            Bitmap faceBitmap = UserInterfaceHelperService.getFaceBitMap(capturedFace.get(0));
            setBiometricImage(bioData, "FaceImageSource", isPreview ? 0 : R.drawable.face,
                    isPreview ? faceBitmap : null);

            if ("applicant".equalsIgnoreCase(field.getSubType())) {
                setBiometricImage(velocityContext, "ApplicantImageSource", faceBitmap);
            }
        }
        return bioData;
    }


    private void setBiometricImage(Map<String, Object> templateValues, String key, int imagePath, Bitmap bitmap) {
        if (bitmap != null) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
                templateValues.put(key, "\"data:image/jpeg;base64," + encodedBytes + "\"");
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        } else if (imagePath != 0) {
            templateValues.put(key, getImage(imagePath));
        }
    }

    private void setBiometricImage(VelocityContext velocityContext, String key, Bitmap bitmap) {
        if (bitmap != null) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
                velocityContext.put(key, "\"data:image/jpeg;base64," + encodedBytes + "\"");
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
    }


    private void setFingerRankings(List<BiometricsDto> capturedFingers, List<String> fingers, Map<String, Object> data) {
        Map<String, Float> sortedValues = capturedFingers.stream()
                .filter(b -> b.getBioValue() != null)
                .sorted(Comparator.comparing(BiometricsDto::getQualityScore))
                .collect(Collectors.toMap(BiometricsDto::getBioSubType, BiometricsDto::getQualityScore));

        int rank = 0;
        double prev = 0;
        Map<String, Integer> rankings = new HashMap<>();
        for (Map.Entry<String, Float> entry : sortedValues.entrySet()) {
            rankings.put(entry.getKey(), prev == 0 ? ++rank : entry.getValue() == prev ? rank : ++rank);
            prev = entry.getValue();
        }

        for (String finger : fingers) {
            Optional<BiometricsDto> result = capturedFingers.stream()
                    .filter(b -> b.getBioSubType().equalsIgnoreCase(finger)).findFirst();
            if (result.isPresent()) {
                data.put(finger, result.get().getBioValue() == null ? "&#10008;" :
                        rankings.get(finger));
            }
        }
    }


    private String getImage(int imagePath) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
            byte[] byteArray = byteStream.toByteArray();
            String imageEncodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
            return "data:image/jpeg;base64," + imageEncodedBytes;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return "";
    }


    private void setBasicDetails(boolean isPreview, RegistrationDto registrationDto, VelocityContext velocityContext) {
        velocityContext.put("isPreview", isPreview);
        velocityContext.put("ApplicationIDLabel", appContext.getString(R.string.app_id));
        velocityContext.put("ApplicationID", registrationDto.getRId());
        velocityContext.put("UINLabel", appContext.getString(R.string.uin));
        velocityContext.put("UIN", registrationDto.getDemographics().get("UIN"));

        LocalDateTime currentTime = OffsetDateTime.now().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        velocityContext.put("Date", currentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
        velocityContext.put("DateLabel", appContext.getString(R.string.date));

        velocityContext.put("DemographicInfo", appContext.getString(R.string.demographic_info));
        velocityContext.put("Photo", appContext.getString(R.string.photo));
        velocityContext.put("DocumentsLabel", appContext.getString(R.string.documents));
        velocityContext.put("BiometricsLabel", appContext.getString(R.string.biometrics));
        velocityContext.put("FaceLabel", appContext.getString(R.string.face_label));
        velocityContext.put("ExceptionPhotoLabel", appContext.getString(R.string.exception_photo_label));
        velocityContext.put("RONameLabel", appContext.getString(R.string.ro_label));
        velocityContext.put("ROName", "110011");
        velocityContext.put("RegCenterLabel", appContext.getString(R.string.reg_center));
        velocityContext.put("RegCenter", "10011");
        velocityContext.put("ImportantGuidelines", appContext.getString(R.string.imp_guidelines));

        velocityContext.put("LeftEyeLabel", appContext.getString(R.string.left_iris));
        velocityContext.put("RightEyeLabel", appContext.getString(R.string.right_iris));
        velocityContext.put("LeftPalmLabel", appContext.getString(R.string.left_slap));
        velocityContext.put("RightPalmLabel", appContext.getString(R.string.right_slap));
        velocityContext.put("ThumbsLabel", appContext.getString(R.string.thumbs_label));
    }

    private Map<String, Object> getDemographicData(FieldSpecDto field, RegistrationDto registrationDto) {
        Map<String, Object> data = null;
        if ("UIN".equalsIgnoreCase(field.getId()) || "IDSchemaVersion".equalsIgnoreCase(field.getId()))
            return null;

        String value = getValue(registrationDto.getDemographics().get(field.getId()));
        if (value != null && !value.isEmpty()) {
            data = new HashMap<>();
            data.put("label", getFieldLabel(field, registrationDto));
            data.put("value", getFieldValue(field, registrationDto));
        }
        return data;
    }

    private String getValue(Object fieldValue) {
        String value = "";

        if (fieldValue instanceof String || fieldValue instanceof Integer || fieldValue instanceof BigInteger
                || fieldValue instanceof Double) {
            value = String.valueOf(fieldValue);
        } else {
            if (null != fieldValue) {
                List<SimpleType> valueList = (List<SimpleType>) fieldValue;
                value = valueList.get(0).getValue();
            }
        }
        return value;
    }

    private String getValue(Object fieldValue, String lang) {
        String value = "";

        if (fieldValue instanceof List<?>) {
            Optional<SimpleType> demoValueInRequiredLang = ((List<SimpleType>) fieldValue).stream()
                    .filter(valueDTO -> valueDTO.getLanguage().equals(lang)).findFirst();

            if (demoValueInRequiredLang.isPresent() && demoValueInRequiredLang.get().getValue() != null) {
                value = demoValueInRequiredLang.get().getValue();
            }
        } else if (fieldValue instanceof String || fieldValue instanceof Integer || fieldValue instanceof BigInteger
                || fieldValue instanceof Double) {
            value = String.valueOf(fieldValue);
        }

        return value == null ? "" : value;
    }

    private Object getFieldLabel(FieldSpecDto field, RegistrationDto registrationDto) {
        List<String> labels = new ArrayList<>();
        List<String> selectedLanguages = registrationDto.getSelectedLanguages();
        for (String selectedLanguage : selectedLanguages) {
            labels.add(field.getLabel().get(selectedLanguage));
        }
        return String.join(SLASH, labels);
    }

    private String getFieldValue(FieldSpecDto field, RegistrationDto registrationDto) {
        Object fieldValue = registrationDto.getDemographics().get(((FieldSpecDto) field).getId());
        List<String> values = new ArrayList<>();
        List<String> selectedLanguages = registrationDto.getSelectedLanguages();
        for (String selectedLanguage : selectedLanguages) {
            values.add(getValue(fieldValue, selectedLanguage));
            if (!field.getType().equalsIgnoreCase("simpleType")) {
                return String.join(SLASH, values);
            }
        }
        return String.join(SLASH, values);
    }

    private Map<String, Object> getDocumentData(FieldSpecDto field, RegistrationDto registrationDto, VelocityContext velocityContext) {
        Map<String, Object> data = null;
        if (registrationDto.getDocuments().get(field.getId()) != null) {
            data = new HashMap<>();
            data.put("label", getFieldLabel(field, registrationDto));
            data.put("value", registrationDto.getDocuments().get(field.getId()).getType());
        }
        return data;
    }

}
