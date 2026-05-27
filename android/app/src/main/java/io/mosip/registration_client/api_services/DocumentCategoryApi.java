package io.mosip.registration_client.api_services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.mvel2.MVEL;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dao.FileSignatureDao;
import io.mosip.registration.clientmanager.entity.DocumentType;
import io.mosip.registration.clientmanager.entity.FileSignature;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.dto.JWTSignatureVerifyRequestDto;
import io.mosip.registration.keymanager.dto.JWTSignatureVerifyResponseDto;
import io.mosip.registration.keymanager.service.LocalClientCryptoServiceImpl;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.util.HMACUtils2;
import io.mosip.registration_client.model.DocumentCategoryPigeon;

@Singleton
public class DocumentCategoryApi implements DocumentCategoryPigeon.DocumentCategoryApi {

    MasterDataService masterDataService;

    private final RegistrationService registrationService;

    private static final Map<String, String> SCRIPT_CACHE = new HashMap<>();

    private Map<String, Object> applicationMap;

    private Context context;

    FileSignatureDao fileSignatureRepository;

    GlobalParamRepository globalParamRepository;

    CertificateManagerService certificateManagerService;

    LocalClientCryptoServiceImpl clientCryptoFacade;

    CryptoManagerService cryptoManagerServiceImpl;

    ClientCryptoManagerService clientCryptoManagerService;

    @Inject
    public DocumentCategoryApi(RegistrationService registrationService, FileSignatureDao fileSignatureRepository,
            GlobalParamRepository globalParamRepository, MasterDataService masterDataService,
            CertificateManagerService certificateManagerService, CryptoManagerService cryptoManagerServiceImpl,
            ClientCryptoManagerService clientCryptoManagerService, Context context) {
        this.registrationService = registrationService;
        this.context = context;
        this.fileSignatureRepository = fileSignatureRepository;
        this.applicationMap = new HashMap<>();
        this.globalParamRepository = globalParamRepository;
        this.masterDataService = masterDataService;
        this.certificateManagerService = certificateManagerService;
        this.cryptoManagerServiceImpl = cryptoManagerServiceImpl;
        this.clientCryptoManagerService = clientCryptoManagerService;
    }

    @Override
    public void getDocumentCategories(@NonNull String categoryCode, @NonNull String langCode, @NonNull List<String> languages, @NonNull DocumentCategoryPigeon.Result<List<DocumentCategoryPigeon.DocumentType>> result) {
        List<DocumentCategoryPigeon.DocumentType> documentTypeList = new ArrayList<>();
        try {
            Map<String, Object> dataContext = this.registrationService.getRegistrationDto().getMVELDataContext();
            String applicantTypeCode = this
                    .evaluateMvelScript((String) this.globalParamRepository.getCachedStringMAVELScript(), dataContext);
            Log.i(getClass().getSimpleName(), "applicantType: " + applicantTypeCode);

            List<String> languageCodes  = (languages == null || languages.isEmpty())
                    ? Collections.singletonList(langCode)
                    : languages;

            List<DocumentType> documentTypes  = this.masterDataService.getDocumentTypes(categoryCode, applicantTypeCode, languageCodes);
            documentTypes  = documentTypes  != null ? documentTypes  : Collections.emptyList();

            documentTypeList = documentTypes.stream()
                    .filter(doc -> doc != null && doc.getCode() != null)
                    .sorted(Comparator.comparingInt(doc -> languageCodes.indexOf(doc.getLangCode())))
                    .collect(Collectors.groupingBy(
                            DocumentType::getCode,
                            LinkedHashMap::new,
                            Collectors.mapping(
                                    doc -> (doc.getName() != null && !doc.getName().isEmpty())
                                            ? doc.getName()
                                            : doc.getCode(),
                                    Collectors.joining(" / ")
                            )
                    ))
                    .entrySet().stream()
                    .map(entry -> new DocumentCategoryPigeon.DocumentType.Builder()
                            .setCode((String) entry.getKey())
                            .setLabel(entry.getValue())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch document values: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(documentTypeList);
    }

    @Override
    public void getDocumentSize(@NonNull DocumentCategoryPigeon.Result<String> result) {
        String documentSize = "";
        try {
            documentSize = this.globalParamRepository.getCachedStringDocumentSize();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Fetch document size: " + Arrays.toString(e.getStackTrace()));
        }
        result.success(documentSize);
    }

    public String evaluateMvelScript(String scriptName, Map<String, Object> dataContext) {
        try {
            Map<String, String> ageGroups = new HashMap<String, String>();
            JSONObject ageGroupConfig = new JSONObject((String) this.globalParamRepository.getCachedStringAgeGroup());
            for (Iterator<String> it = ageGroupConfig.keys(); it.hasNext();) {
                String key = it.next();
                ageGroups.put(key, ageGroupConfig.getString(key));
            }

            Map context = new HashMap();
            MVEL.eval(getScript(scriptName), context);
            context.put("identity", dataContext);
            context.put("ageGroups", ageGroups);
            return MVEL.eval("return getApplicantType();", context, String.class);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Failed to evaluate mvel script", e);
        }
        return null;
    }

    private String getScript(String scriptName) {
        if (SCRIPT_CACHE.containsKey(scriptName) && SCRIPT_CACHE.get(scriptName) != null)
            return SCRIPT_CACHE.get(scriptName);

        try {
            Optional<FileSignature> fileSignature = this.fileSignatureRepository.findByFileName(scriptName);
            if (!fileSignature.isPresent()) {
                Log.e("File signature not found : {}", scriptName);
                return null;
            }

            Path path = Paths.get(context.getFilesDir().getAbsolutePath(), scriptName);
            byte[] bytes;
            if (fileSignature.get().getEncrypted()) {
                CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
                cryptoRequestDto.setValue(FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8));
                CryptoResponseDto cryptoResponseDto = clientCryptoFacade.decrypt(cryptoRequestDto);
                bytes = CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
            } else {
                bytes = FileUtils.readFileToByteArray(path.toFile());
            }
            String actualData = String.format("{\"hash\":\"%s\"}", HMACUtils2.digestAsPlainText(bytes));
            if (!validateScriptSignature(fileSignature.get().getSignature(), actualData)) {
                Log.e("File signature validation failed : {}", scriptName);
                return null;
            }
            SCRIPT_CACHE.put(scriptName, new String(bytes));

        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Failed to get mvel script", e);
        }
        return SCRIPT_CACHE.get(scriptName);
    }

    private boolean validateScriptSignature(String signature, String actualData) throws Exception {

        String certificateData = this.certificateManagerService.getCertificate("SERVER-RESPONSE", "SIGN-VERIFY");

        JWTSignatureVerifyRequestDto jwtSignatureVerifyRequestDto = new JWTSignatureVerifyRequestDto();
        jwtSignatureVerifyRequestDto.setJwtSignatureData(signature);
        jwtSignatureVerifyRequestDto
                .setActualData(CryptoUtil.encodeToURLSafeBase64(actualData.getBytes(StandardCharsets.UTF_8)));
        jwtSignatureVerifyRequestDto.setCertificateData(certificateData);

        JWTSignatureVerifyResponseDto verifyResponseDto = this.clientCryptoManagerService
                .jwtVerify(jwtSignatureVerifyRequestDto);

        return verifyResponseDto.isSignatureValid();
    }

}