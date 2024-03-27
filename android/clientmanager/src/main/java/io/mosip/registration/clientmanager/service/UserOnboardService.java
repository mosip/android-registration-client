package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.registration.clientmanager.constant.ClientManagerError;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.dto.http.OnboardError;
import io.mosip.registration.clientmanager.dto.http.OnboardResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.keymanager.dto.CertificateRequestDto;
import io.mosip.registration.keymanager.dto.CryptoManagerRequestDto;
import io.mosip.registration.keymanager.dto.CryptoManagerResponseDto;
import io.mosip.registration.keymanager.dto.SignRequestDto;
import io.mosip.registration.keymanager.dto.SignResponseDto;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;
import io.mosip.registration.packetmanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.util.DateUtils;
import io.mosip.registration.packetmanager.util.HMACUtils2;
import io.mosip.registration.packetmanager.util.JsonUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class UserOnboardService {

    private static final String TAG = UserOnboardService.class.getSimpleName();
    public static final String ON_BOARD_TIME_STAMP = "timestamp";
    public static final String OPERATOR_BIO_KEY = "%s_%s";
    public static final String ON_BOARD_BIOMETRICS = "biometrics";
    public static final String AUTH_HASH = "hash";
    public static String ONBOARD_CERT_THUMBPRINT = "thumbprint";
    private static final String APPLICATION_ID = "IDA";

    private static final String APPLICATION_NAME = "Android Registration Client";
    private static final String REFERENCE_ID = "INTERNAL";
    public static final String CERTIFICATE = "certificate";
    public static final String ID = "id";
    public static final String IDENTITY = "mosip.identity.auth.internal";
    public static final String VERSION = "version";
    public static final String ENV = "env";
    public static final String DOMAIN_URI = "domainUri";
    public static final String CONSENT_OBTAINED = "consentObtained";
    public static final String INDIVIDUAL_ID = "individualId";
    public static final String INDIVIDUAL_ID_TYPE = "individualIdType";
    public static final String KEY_INDEX = "keyIndex";
    public static final String REQUEST_TIME = "requestTime";
    public static final String TRANSACTION_ID = "transactionID";
    public static final String BIO = "bio";
    public static final String REQUEST_AUTH = "requestedAuth";
    public static final String PACKET_SYNC_VERSION = "1.0";
    public static final String SERVER_ACTIVE_PROFILE = "Staging";
    public static final String TRANSACTION_ID_VALUE = "1234567890";
    public static final String USER_ID_CODE = "USERID";
    public static final String ON_BOARD_BIO_TYPE = "bioType";
    public static final String ON_BOARD_BIO_SUB_TYPE = "bioSubType";
    public static final String ON_BOARD_BIO_VALUE = "bioValue";
    public static final String ON_BOARD_BIO_DATA = "data";
    public static final String ON_BOARD_REQUEST = "request";
    public static final String ON_BOARD_REQUEST_HMAC = "requestHMAC";
    public static final String ON_BOARD_REQUEST_SESSION_KEY = "requestSessionKey";
    public static final String SESSION_KEY = "sessionKey";
    public static final String ON_BOARD_AUTH_STATUS = "authStatus";
    public static final String PURPOSE = "purpose";
    public static final String PURPOSE_AUTH = "Auth";
    public static final String TRANSACTION_Id = "transactionId";
    private Context context;
    private CertificateManagerService certificateManagerService;
    private CryptoManagerService cryptoManagerService;
    private SyncRestService syncRestService;
    private AuditManagerService auditManagerService;
    private ObjectMapper objectMapper;
    private ClientCryptoManagerService clientCryptoManagerService;
    SharedPreferences sharedPreferences;
    public static final String USER_ID = "user_id";
    public static final String USER_TOKEN = "user_token";
    private RegistrationService registrationService;
    private UserBiometricRepository userBiometricRepository;

    private static final String BIOMETRIC_KEY_PATTERN = "%s_%s_%s";

    private List<BiometricsDto> operatorBiometrics=new ArrayList<>();
    public List<BiometricsDto> getOperatorBiometrics(){
        return operatorBiometrics;
    }
    private boolean idaResponse = false;
    public boolean isIdaResponse() {
        return idaResponse;
    }
    public void setIdaResponse(boolean idaResponse) {
        this.idaResponse = idaResponse;
    }

    @Inject
    public UserOnboardService(Context context, ObjectMapper objectMapper, AuditManagerService auditManagerService,
                              CertificateManagerService certificateManagerService,
                              SyncRestService syncRestService, CryptoManagerService cryptoManagerService, RegistrationService registrationService, UserBiometricRepository userBiometricRepository, ClientCryptoManagerService clientCryptoManagerService) {
        this.context = context;
        this.certificateManagerService = certificateManagerService;
        this.syncRestService = syncRestService;
        this.cryptoManagerService = cryptoManagerService;
        this.objectMapper = objectMapper;
        this.auditManagerService = auditManagerService;
        this.registrationService = registrationService;
        this.userBiometricRepository = userBiometricRepository;
        this.clientCryptoManagerService = clientCryptoManagerService;
        sharedPreferences = this.context.getSharedPreferences(
                        this.context.getString(R.string.app_name),
                        Context.MODE_PRIVATE);
    }

    public boolean validateWithIDAuthAndSave(List<BiometricsDto> biometrics) throws ClientCheckedException {
        Log.i(TAG, "validateWithIDAuthAndSave invoked ");

        if (Objects.isNull(biometrics))
            throw new ClientCheckedException(ClientManagerError.REG_BIOMETRIC_DTO_NULL.getErrorCode(),
                    ClientManagerError.REG_BIOMETRIC_DTO_NULL.getErrorMessage());

        String userId = sharedPreferences.getString(USER_ID, "");
        if (validateWithIDA(userId, biometrics)) {
            Log.i(TAG, "User onboarding success");
            return save(biometrics, userId);
        }
        return false;
    }

    public boolean validateWithIDA(String userId, List<BiometricsDto> biometrics) {
        if (isIdaResponse()) { return true; }
        Map<String, Object> idaRequestMap = new LinkedHashMap<>();
        idaRequestMap.put(ID, IDENTITY);
        idaRequestMap.put(VERSION, PACKET_SYNC_VERSION);
        idaRequestMap.put(REQUEST_TIME,
                DateUtils.formatToISOString(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()));
        idaRequestMap.put(ENV, SERVER_ACTIVE_PROFILE);
        idaRequestMap.put(DOMAIN_URI, "https://api-internal.qa-platform1.mosip.net");
        idaRequestMap.put(TRANSACTION_ID, TRANSACTION_ID_VALUE);
        idaRequestMap.put(CONSENT_OBTAINED, true);
        idaRequestMap.put(INDIVIDUAL_ID, userId);
        idaRequestMap.put(INDIVIDUAL_ID_TYPE, USER_ID_CODE);
        idaRequestMap.put(KEY_INDEX, "");
        Map<String, Boolean> tempMap = new HashMap<>();
        tempMap.put(BIO, true);
        idaRequestMap.put(REQUEST_AUTH, tempMap);

        List<Map<String, Object>> listOfBiometric = new ArrayList<>();
        try {
            String certificateData = getCertificate();
            Certificate certificate = this.cryptoManagerService.convertToCertificate(certificateData);

            if (Objects.nonNull(biometrics) && !biometrics.isEmpty()) {
                String previousHash = HMACUtils2.digestAsPlainText("".getBytes());

                for (BiometricsDto dto : biometrics) {
                    LinkedHashMap<String, Object> dataBlock = buildDataBlock(dto.getModality(), dto.getBioSubType(),
                            io.mosip.registration.keymanager.util.CryptoUtil.base64decoder.decode(dto.getBioValue()), previousHash);
                    dataBlock.put(ONBOARD_CERT_THUMBPRINT, CryptoUtil.encodeToURLSafeBase64(cryptoManagerService.getCertificateThumbprint(certificate)));
                    previousHash = (String) dataBlock.get(AUTH_HASH);
                    listOfBiometric.add(dataBlock);
                }
            }

            if (!listOfBiometric.isEmpty()) {
                Map<String, Object> requestMap = new LinkedHashMap<>();
                requestMap.put(ON_BOARD_BIOMETRICS, listOfBiometric);
                requestMap.put(ON_BOARD_TIME_STAMP, DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
                return getIdaAuthResponse(idaRequestMap, requestMap, certificate);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    private LinkedHashMap<String, Object> buildDataBlock(String bioType, String bioSubType, byte[] attributeISO,
                                                         String previousHash) throws Exception {
        Log.i(TAG, "Building data block for User Onboard Authentication with IDA");

        LinkedHashMap<String, Object> dataBlock = new LinkedHashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put(ON_BOARD_TIME_STAMP, DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
        data.put(ON_BOARD_BIO_TYPE, bioType);
        data.put(ON_BOARD_BIO_SUB_TYPE, bioSubType);
        SplitEncryptedData splitEncryptedData = getSessionKey(data, attributeISO);
        data.put(ON_BOARD_BIO_VALUE, splitEncryptedData.getEncryptedData());
        data.put(TRANSACTION_Id, TRANSACTION_ID_VALUE);
        data.put(PURPOSE, PURPOSE_AUTH);
        data.put(ENV, SERVER_ACTIVE_PROFILE);
        data.put(DOMAIN_URI,  "https://api-internal.qa-platform1.mosip.net");
        String dataBlockJsonString = this.objectMapper.writeValueAsString(data);
        dataBlock.put(ON_BOARD_BIO_DATA, CryptoUtil.encodeToURLSafeBase64(dataBlockJsonString.getBytes()));

        String presentHash = HMACUtils2.digestAsPlainText(dataBlockJsonString.getBytes());
        String concatenatedHash = previousHash + presentHash;
        String finalHash = HMACUtils2.digestAsPlainText(concatenatedHash.getBytes());

        dataBlock.put(AUTH_HASH, finalHash);
        dataBlock.put(SESSION_KEY, splitEncryptedData.getEncryptedSessionKey());

        Log.i(TAG, "Returning the dataBlock for User Onboard Authentication with IDA");
        return dataBlock;
    }

    private boolean getIdaAuthResponse(Map<String, Object> idaRequestMap, Map<String, Object> requestMap,
                                                   Certificate certificate) throws ClientCheckedException {
        try {
            PublicKey publicKey = certificate.getPublicKey();
            idaRequestMap.put(ONBOARD_CERT_THUMBPRINT, CryptoUtil.encodeToURLSafeBase64(cryptoManagerService.getCertificateThumbprint(certificate)));

            // Symmetric key alias session key
            KeyGenerator keyGenerator = cryptoManagerService.generateAESKey(256);
            // Generate AES Session Key
            final SecretKey symmentricKey = keyGenerator.generateKey();
            // request
            idaRequestMap.put(ON_BOARD_REQUEST,
                    CryptoUtil.encodeToURLSafeBase64(cryptoManagerService.symmetricEncryptWithRandomIV(symmentricKey,
                            new ObjectMapper().writeValueAsString(requestMap).getBytes(), null)));

            // requestHMAC
            idaRequestMap.put(ON_BOARD_REQUEST_HMAC,
                    CryptoUtil.encodeToURLSafeBase64(cryptoManagerService.symmetricEncryptWithRandomIV(symmentricKey, HMACUtils2
                                    .digestAsPlainText(new ObjectMapper().writeValueAsString(requestMap).getBytes()).getBytes(),
                            null)));

            // requestSession Key
            idaRequestMap.put(ON_BOARD_REQUEST_SESSION_KEY,
                    CryptoUtil.encodeToURLSafeBase64(cryptoManagerService.asymmetricEncrypt(publicKey, symmentricKey.getEncoded())));

            String encodedData = io.mosip.registration.keymanager.util.CryptoUtil.base64encoder.encodeToString((JsonUtils.javaObjectToJsonString(idaRequestMap)).getBytes(StandardCharsets.UTF_8));
            SignRequestDto signRequestDto = new SignRequestDto(encodedData);
            SignResponseDto signResponseDto = clientCryptoManagerService.sign(signRequestDto);

            String authToken = sharedPreferences.getString(USER_TOKEN, "");
            String signature = signResponseDto.getData();
            String cookie = String.format("Authorization=%s", authToken);

            Call<OnboardResponseWrapper<Map<String, Object>>> call = syncRestService.doOperatorAuth(authToken, signature, cookie,
                    idaRequestMap);

            call.enqueue(new Callback<OnboardResponseWrapper<Map<String, Object>>>() {
                @Override
                public void onResponse(Call<OnboardResponseWrapper<Map<String, Object>>> call, Response<OnboardResponseWrapper<Map<String, Object>>> response) {
                    if (response.isSuccessful()) {
                        OnboardError error = SyncRestUtil.getServiceError(response.body());
                        if (error == null || (error != null && error.getErrorCode() == null)) {
                            setIdaResponse((Boolean) response.body().getResponse().get(ON_BOARD_AUTH_STATUS));
                        }
                    } else{
                        setIdaResponse(false);
                        Log.i("Response UserOnboard Service",response.toString());
                        Toast.makeText(context, "Failed to fetch IDA Response :" + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<OnboardResponseWrapper<Map<String, Object>>> call, Throwable t) {
                    t.printStackTrace();
                    setIdaResponse(false);
                    Toast.makeText(context, "IDA Response fetch failed", Toast.LENGTH_LONG).show();
                }

            });
            return isIdaResponse();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            throw new ClientCheckedException("", e.getMessage(), e);
        }
    }

    private String getCertificate() {
        String certData = this.certificateManagerService.getCertificate(APPLICATION_ID, REFERENCE_ID);
        if(certData == null) {
            Call<ResponseWrapper<Map<String, Object>>> call = syncRestService.getIDACertificate();
            call.enqueue(new Callback<ResponseWrapper<Map<String, Object>>>() {
                @Override
                public void onResponse(Call<ResponseWrapper<Map<String, Object>>> call, Response<ResponseWrapper<Map<String, Object>>> response) {
                    if (response.isSuccessful()) {
                        ServiceError error = SyncRestUtil.getServiceError(response.body());
                        if (error == null) {
                            CertificateRequestDto certificateRequestDto = new CertificateRequestDto();
                            certificateRequestDto.setApplicationId(APPLICATION_ID);
                            certificateRequestDto.setReferenceId(REFERENCE_ID);
                            certificateRequestDto.setCertificateData((String) response.body().getResponse().get(CERTIFICATE));
                            certificateManagerService.uploadOtherDomainCertificate(certificateRequestDto);

                        }
                    } else{
                        Log.i("Response UserOnboard Service",response.toString());
                        Toast.makeText(context, "Failed to fetch IDA Internal certificate :" + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseWrapper<Map<String, Object>>> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(context, "IDA Internal Certificate fetch failed", Toast.LENGTH_LONG).show();
                }
            });

        }
        return this.certificateManagerService.getCertificate(APPLICATION_ID, REFERENCE_ID);
    }

    private synchronized SplitEncryptedData getSessionKey(Map<String, Object> requestMap, byte[] data) throws Exception {
        Log.i(TAG,"Getting sessionKey for User Onboard Authentication with IDA");

        String timestamp = (String) requestMap.get(ON_BOARD_TIME_STAMP);
        byte[] xorBytes = getXOR(timestamp, TRANSACTION_ID_VALUE);
        byte[] saltLastBytes = getLastBytes(xorBytes, 12);
        byte[] aadLastBytes = getLastBytes(xorBytes, 16);

        CryptoManagerRequestDto cryptomanagerRequestDto = new CryptoManagerRequestDto();
        cryptomanagerRequestDto.setAad(CryptoUtil.encodeToURLSafeBase64(aadLastBytes));
        cryptomanagerRequestDto.setApplicationId(APPLICATION_ID);
        cryptomanagerRequestDto.setData(CryptoUtil.encodeToURLSafeBase64(data));
        cryptomanagerRequestDto.setReferenceId(REFERENCE_ID);
        cryptomanagerRequestDto.setSalt(CryptoUtil.encodeToURLSafeBase64(saltLastBytes));
        cryptomanagerRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTime());
        CryptoManagerResponseDto cryptomanagerResponseDto = cryptoManagerService.encrypt(cryptomanagerRequestDto);

        Log.i(TAG,"Returning the sessionKey for User Onboard Authentication with IDA");
        return splitEncryptedData(cryptomanagerResponseDto.getData());
    }

    /**
     * Method to insert specified number of 0s in the beginning of the given string
     *
     * @param string
     * @param count  - number of 0's to be inserted
     * @return bytes
     */
    private byte[] prependZeros(byte[] string, int count) {
        byte[] newBytes = new byte[string.length + count];
        int i = 0;
        for (; i < count; i++) {
            newBytes[i] = 0;
        }

        for (int j = 0; i < newBytes.length; i++, j++) {
            newBytes[i] = string[j];
        }

        return newBytes;
    }

    /**
     * Method to return the XOR of the given strings
     *
     */
    private byte[] getXOR(String timestamp, String transactionId) {
        Log.i(TAG,"Started getting XOR of timestamp and transactionId");

        byte[] timestampBytes = timestamp.getBytes();
        byte[] transactionIdBytes = transactionId.getBytes();
        // Lengths of the given strings
        int timestampLength = timestampBytes.length;
        int transactionIdLength = transactionIdBytes.length;

        // Make both the strings of equal lengths
        // by inserting 0s in the beginning
        if (timestampLength > transactionIdLength) {
            transactionIdBytes = prependZeros(transactionIdBytes, timestampLength - transactionIdLength);
        } else if (transactionIdLength > timestampLength) {
            timestampBytes = prependZeros(timestampBytes, transactionIdLength - timestampLength);
        }

        // Updated length
        int length = Math.max(timestampLength, transactionIdLength);
        byte[] xorBytes = new byte[length];

        // To store the resultant XOR
        for (int i = 0; i < length; i++) {
            xorBytes[i] = (byte) (timestampBytes[i] ^ transactionIdBytes[i]);
        }

        Log.i(TAG, "Returning XOR of timestamp and transactionId");
        return xorBytes;
    }

    /**
     * Gets the last bytes.
     *
     * @param xorBytes
     * @param lastBytesNum the last bytes num
     * @return the last bytes
     */
    private byte[] getLastBytes(byte[] xorBytes, int lastBytesNum) {
        assert (xorBytes.length >= lastBytesNum);
        return Arrays.copyOfRange(xorBytes, xorBytes.length - lastBytesNum, xorBytes.length);
    }

    /**
     * Split encrypted data.
     *
     * @param data the data
     * @return the splitted encrypted data
     */
    public SplitEncryptedData splitEncryptedData(String data) {
        byte[] dataBytes = CryptoUtil.decodeURLSafeBase64(data);
        //TODO take keysplitter from properties
        //mosip.kernel.data-key-splitter=#KEY_SPLITTER#
        String KEY_SPLITTER = "#KEY_SPLITTER#";
        byte[][] splits = splitAtFirstOccurrence(dataBytes, KEY_SPLITTER.getBytes());
        return new SplitEncryptedData(CryptoUtil.encodeToURLSafeBase64(splits[0]), CryptoUtil.encodeToURLSafeBase64(splits[1]));
    }

    /**
     * Split at first occurance.
     *
     * @param strBytes the str bytes
     * @param sepBytes the sep bytes
     * @return the byte[][]
     */
    private static byte[][] splitAtFirstOccurrence(byte[] strBytes, byte[] sepBytes) {
        int index = findIndex(strBytes, sepBytes);
        if (index >= 0) {
            byte[] bytes1 = new byte[index];
            byte[] bytes2 = new byte[strBytes.length - (bytes1.length + sepBytes.length)];
            System.arraycopy(strBytes, 0, bytes1, 0, bytes1.length);
            System.arraycopy(strBytes, (bytes1.length + sepBytes.length), bytes2, 0, bytes2.length);
            return new byte[][] { bytes1, bytes2 };
        } else {
            return new byte[][] { strBytes, new byte[0] };
        }
    }

    /**
     * Find index.
     *
     * @param arr    the arr
     * @param subarr the subarr
     * @return the int
     */
    private static int findIndex(byte arr[], byte[] subarr) {
        int len = arr.length;
        int subArrayLen = subarr.length;
        return IntStream.range(0, len).filter(currentIndex -> {
                    if ((currentIndex + subArrayLen) <= len) {
                        byte[] sArray = new byte[subArrayLen];
                        System.arraycopy(arr, currentIndex, sArray, 0, subArrayLen);
                        return Arrays.equals(sArray, subarr);
                    }
                    return false;
                }).findFirst() // first occurence
                .orElse(-1); // No element found
    }

    /**
     * The Class SplitEncryptedData.
     */
    public static class SplitEncryptedData {
        private String encryptedSessionKey;
        private String encryptedData;

        public SplitEncryptedData(String encryptedSessionKey, String encryptedData) {
            this.encryptedData = encryptedData;
            this.encryptedSessionKey = encryptedSessionKey;
        }
        public String getEncryptedData() {
            return encryptedData;
        }
        public String getEncryptedSessionKey() {
            return encryptedSessionKey;
        }

    }
    
    private boolean save(List<BiometricsDto> biometrics, String userId) {
        Log.i(TAG, "Entering save method");
        String onBoardingResponse = "";
        try {
            List<BiometricsDto> fingerprintsList = getBiometricsByModality(RegistrationConstants.FINGER,
                    biometrics);
            List<BiometricsDto> irisList = getBiometricsByModality(RegistrationConstants.IRIS, biometrics);
            List<BiometricsDto> face = getBiometricsByModality(RegistrationConstants.FACE, biometrics);

            List<BIR> fpTemplates = getExtractedTemplates(fingerprintsList);
            List<BIR> irisTemplates = getExtractedTemplates(irisList);
            List<BIR> faceTemplates = getExtractedTemplates(face);

            List<BIR> templates = Stream.of(fpTemplates, irisTemplates, faceTemplates).flatMap(Collection::stream)
                    .collect(Collectors.toList());

            onBoardingResponse = userBiometricRepository.insertExtractedTemplates(templates, userId);
            if (onBoardingResponse.equalsIgnoreCase(RegistrationConstants.SUCCESS)) {
                Log.i(TAG, "operator details inserted");
                if ((RegistrationConstants.SUCCESS).equalsIgnoreCase(userBiometricRepository.saveOnboardStatus(userId))) {
                    Log.i(TAG, "center user machine details inserted");
                    return true;
                }
            }
        } catch (Exception exception) {
            Log.e(TAG, "Exception while saving operator biometrics", exception);
            return false;
        }
        return false;
    }

    private List<BIR> getExtractedTemplates(List<BiometricsDto> biometrics) {
        List<BIR> templates = new ArrayList<>();
        if (biometrics != null && !biometrics.isEmpty()) {
            List<BIR> birList = new ArrayList<>();
            for (BiometricsDto biometricsDto : biometrics) {
                try {
                    BIR bir = registrationService.buildBIR(biometricsDto);
                    Log.i(TAG, "Adding bir");
                    birList.add(bir);
                } catch (Exception e){
                    Log.e(TAG, "Error in adding BIR: " + e.getMessage());
                }
            }
            return birList;
        //TODO - Template extration logic to be done when SDK for android is implemented
//            templates = bioAPIFactory
//                    .getBioProvider(BiometricType.fromValue(birList.get(0).getBdbInfo().getType().get(0).value()),
//                            BiometricFunction.EXTRACT)
//                    .extractTemplate(birList, null);
        }
        return templates;
    }

    private List<BiometricsDto> getBiometricsByModality(String modality, List<BiometricsDto> biometrics) {
        return biometrics.stream().filter(dto -> dto.getModality().toLowerCase().contains(modality.toLowerCase()))
                .collect(Collectors.toList());
    }

}
