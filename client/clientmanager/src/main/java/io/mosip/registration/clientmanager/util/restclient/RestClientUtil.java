//`package io.mosip.registration.clientmanager.util.restclient;
//
//import java.io.FileOutputStream;
//import java.net.MalformedURLException;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicReference;
//
//import io.mosip.kernel.clientcrypto.constant.ClientCryptoManagerConstant;
//import io.mosip.kernel.core.util.FileUtils;
//import io.mosip.registration.context.ApplicationContext;
//import io.mosip.registration.entity.FileSignature;
//import io.mosip.registration.repositories.FileSignatureRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StreamUtils;
//import org.springframework.web.client.*;
//
//import io.mosip.kernel.core.logger.spi.Logger;
//import io.mosip.registration.config.AppConfig;
//import io.mosip.registration.constants.RegistrationConstants;
//
///**
// * This is a general method which gives the response for all httpmethod
// * designators.
// *
// * @author Yaswanth S
// * @since 1.0.0
// */
//@Service
//public class RestClientUtil {
//
//	/**
//	 * Rest Template is a interaction with HTTP servers and enforces RESTful systems
//	 */
//	private static final Logger LOGGER = AppConfig.getLogger(RestClientUtil.class);
//	private static final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//
//	@Autowired
//	private RestTemplate plainRestTemplate;
//
//
//	@Autowired
//	private FileSignatureRepository fileSignatureRepository;
//
//
//
//	/**
//	 * Access resource using restTemplate {@link RestTemplate}
//	 * Note: restTemplate is synchronous client
//	 * @param requestHTTPDTO
//	 * @return
//	 * @throws RestClientException
//	 */
//	public Map<String, Object> invokeURL(RequestHTTPDTO requestHTTPDTO) throws RestClientException {
//		LOGGER.debug("invoke method called {} ", requestHTTPDTO.getUri());
//		Map<String, Object> responseMap = null;
//
//		plainRestTemplate.setRequestFactory(getHttpRequestFactory());
//		ResponseEntity<?> responseEntity = plainRestTemplate.exchange(requestHTTPDTO.getUri(), requestHTTPDTO.getHttpMethod(),
//				requestHTTPDTO.getHttpEntity(), requestHTTPDTO.getClazz());
//
//		if (responseEntity != null && responseEntity.hasBody()) {
//			responseMap = new LinkedHashMap<>();
//			responseMap.put(RegistrationConstants.REST_RESPONSE_BODY, responseEntity.getBody());
//			responseMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, responseEntity.getHeaders());
//		}
//
//		LOGGER.debug("invoke method ended {} ", requestHTTPDTO.getUri());
//		return responseMap;
//	}
//
//
//	public Map<String, Object> invokeForToken(RequestHTTPDTO requestHTTPDTO)
//			throws RestClientException {
//		LOGGER.debug("invokeForToken method called {} ", requestHTTPDTO.getUri());
//
//		Map<String, Object> responseMap = null;
//		requestHTTPDTO.setHttpEntity(new HttpEntity<>(requestHTTPDTO.getRequestBody(), requestHTTPDTO.getHttpHeaders()));
//		plainRestTemplate.setRequestFactory(getHttpRequestFactory());
//		ResponseEntity<?> responseEntity = plainRestTemplate.exchange(requestHTTPDTO.getUri(), requestHTTPDTO.getHttpMethod(),
//				requestHTTPDTO.getHttpEntity(), requestHTTPDTO.getClazz());
//
//		if (responseEntity != null && responseEntity.hasBody()) {
//			responseMap = new LinkedHashMap<>();
//			responseMap.put(RegistrationConstants.REST_RESPONSE_BODY, responseEntity.getBody());
//			responseMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, responseEntity.getHeaders());
//		}
//
//		LOGGER.debug("invokeForToken method ended {} ", requestHTTPDTO.getUri());
//		return responseMap;
//	}
//
//	public boolean isConnectedToSyncServer(String serviceUrl) throws MalformedURLException, URISyntaxException {
//		plainRestTemplate.setRequestFactory(getHttpRequestFactory());
//		ResponseEntity responseEntity = plainRestTemplate.getForEntity(new URL(serviceUrl).toURI(), String.class);
//		return responseEntity.getStatusCode().is2xxSuccessful();
//	}
//
//	public void downloadFile(RequestHTTPDTO requestHTTPDTO) throws Exception {
//		LOGGER.info("downloadFile method called {}", requestHTTPDTO.getUri());
//		AtomicReference<Integer> contentLength = new AtomicReference<>();
//		AtomicReference<String> fileSignature = new AtomicReference<String>();
//
//		plainRestTemplate.setRequestFactory(getHttpRequestFactory());
//		plainRestTemplate.execute(requestHTTPDTO.getUri(), HttpMethod.GET,
//				clientHttpRequest ->  {
//					long[] range = getFileRange(requestHTTPDTO);
//					clientHttpRequest.getHeaders().addAll(requestHTTPDTO.getHttpHeaders());
//					clientHttpRequest.getHeaders().set("Range", String.format("bytes=%s-%s", (range == null) ? 0 :
//							range[0], (range == null) ? "" : range[1]));
//				},
//				response ->  {
//					long[] range = getFileRange(requestHTTPDTO);
//					fileSignature.set(response.getHeaders().getFirst("file-signature"));
//					contentLength.set(Integer.valueOf(response.getHeaders().getFirst("content-length")));
//					StreamUtils.copy(response.getBody(), new FileOutputStream(requestHTTPDTO.getFilePath().toFile(),
//							(range == null) ? false : true));
//					return requestHTTPDTO.getFilePath().toFile();
//				});
//
//		saveFileSignature(requestHTTPDTO, fileSignature.get(), contentLength.get());
//	}
//
//
//	private long[] getFileRange(RequestHTTPDTO requestHTTPDTO) {
//		long[] range = new long[2];
//		Optional<FileSignature> signature = fileSignatureRepository.findByFileName(requestHTTPDTO.getFilePath().toFile().getName());
//		if(signature.isPresent() && requestHTTPDTO.getFilePath().toFile().length() < signature.get().getContentLength()) {
//			range[0] = requestHTTPDTO.getFilePath().toFile().length();
//			range[1] = signature.get().getContentLength();
//			return range;
//		}
//		return null;
//	}
//
//
//	private void saveFileSignature(RequestHTTPDTO requestHTTPDTO, String signature, Integer contentLength) {
//		if(signature == null)
//			return;
//
//		FileSignature fileSignature = new FileSignature();
//		fileSignature.setSignature(signature);
//		fileSignature.setFileName(requestHTTPDTO.getFilePath().toFile().getName());
//		fileSignature.setEncrypted(requestHTTPDTO.isFileEncrypted());
//		fileSignature.setContentLength(contentLength);
//		fileSignatureRepository.save(fileSignature);
//	}
//
//	public SimpleClientHttpRequestFactory getHttpRequestFactory() {
//		requestFactory.setReadTimeout(
//				Integer.parseInt((String) ApplicationContext.map().get(RegistrationConstants.HTTP_API_READ_TIMEOUT)));
//		requestFactory.setConnectTimeout(
//				Integer.parseInt((String) ApplicationContext.map().get(RegistrationConstants.HTTP_API_WRITE_TIMEOUT)));
//		return requestFactory;
//	}
//}
//`