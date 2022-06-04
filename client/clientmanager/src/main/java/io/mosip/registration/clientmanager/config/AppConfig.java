package io.mosip.registration.clientmanager.config;

import android.util.Log;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.SerializationFeature;


import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Spring Configuration class for Registration-Service Module
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */

@Import({ DaoConfig.class, AuditConfig.class, TemplateManagerBuilderImpl.class })
@EnableJpaRepositories(basePackages = "io.mosip.registration", repositoryBaseClass = HibernateRepositoryImpl.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
		".*IdObjectCompositeValidator",
		".*IdObjectMasterDataValidator",
		".*PacketDecryptorImpl",
		".*IdSchemaUtils",
		".*OnlinePacketCryptoServiceImpl"}),
		basePackages = { "io.mosip.registration",
		"io.mosip.kernel.idvalidator", "io.mosip.kernel.ridgenerator", "io.mosip.kernel.qrcode",
		"io.mosip.kernel.crypto", "io.mosip.kernel.jsonvalidator", "io.mosip.kernel.idgenerator",
		"io.mosip.kernel.virusscanner", "io.mosip.kernel.transliteration", "io.mosip.kernel.applicanttype",
		"io.mosip.kernel.core.pdfgenerator.spi", "io.mosip.kernel.pdfgenerator.itext.impl",
		"io.mosip.kernel.idobjectvalidator.impl", "io.mosip.kernel.biosdk.provider.impl",
		"io.mosip.kernel.biosdk.provider.factory", "io.mosip.commons.packet",
		"io.mosip.registration.api.config" })
@PropertySource(value = { "classpath:spring.properties", "classpath:props/mosip-application.properties" })
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@EnableConfigurationProperties
@EnableRetry
public class AppConfig {


	private DataSource datasource;


	public static Log getLogger(Class<?> className) {
		return Logfactory.getSlf4jLogger(className);
	}


	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

 	public RestTemplate selfTokenRestTemplate() {
		return new RestTemplate();
	}

 	public ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return mapper;
	}

 	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("entities");
	}
}
