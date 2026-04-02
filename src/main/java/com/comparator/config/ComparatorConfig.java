package com.comparator.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.comparator.model.config.KeycloakConfiguration;
import com.comparator.model.prop.AppConfigKeys;
import com.comparator.model.prop.ComparatorAppProperties;
import com.devkit.exception.DecryptionException;
import com.devkit.service.IDevkitConfigurationLoaderService;
import com.devkit.service.IEncryptService;
import com.devkit.service.impl.DevkitConfigurationLoaderService;
import com.devkit.service.impl.EncryptService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
@ComponentScan("com.compare")
public class ComparatorConfig {

	@Autowired
	private AppConfigKeys			appConfigKeys;

	@Autowired
	private ComparatorAppProperties	comparatorAppProperties;

	@Bean(name = "mapperIndent")
	public ObjectMapper serializingObjectMapper() {
		// JsonMapper.builder().configure
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true).configure(SerializationFeature.INDENT_OUTPUT, true)
		/*
		 * .configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
		 * .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,
		 * true).configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
		 */;
		return objectMapper;
	}

	/*
	 * @Bean(name = "mapperIndent") public JsonNodeFactory serializingJsonNode() {
	 * JsonNodeFactory objectMapper = new JsonNodeFactory(true);
	 * objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,
	 * true).configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
	 * true).configure(SerializationFeature.INDENT_OUTPUT, true)
	 * .configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
	 * .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,
	 * true).configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true); return
	 * objectMapper; }
	 */

	@Bean
	public IDevkitConfigurationLoaderService devkitConfigurationLoaderService(IEncryptService encryptService) throws JsonParseException, JsonMappingException, IOException {
		IDevkitConfigurationLoaderService globalCommonConfig = new DevkitConfigurationLoaderService();
		globalCommonConfig.loadConfiguration(comparatorAppProperties.getEnvConfigPath(), encryptService);
		return globalCommonConfig;
	}

	@Bean
	public IEncryptService encryptionService() {
		EncryptService encryptService = new EncryptService();
		encryptService.setIV_VAR(comparatorAppProperties.getEnvEncIv());
		encryptService.setKEY_VAR(comparatorAppProperties.getEnvEncKey());
		return encryptService;
	}

	@Bean
	public KeycloakConfiguration keycloakConfiguration(IEncryptService encryptService, IDevkitConfigurationLoaderService configurationLoaderService)
			throws JsonParseException, JsonMappingException, IOException, DecryptionException {
		KeycloakConfiguration keycloakConfiguration = configurationLoaderService.getApplicationConfiguration(appConfigKeys.getKeycloak(), KeycloakConfiguration.class);
		keycloakConfiguration.setKeycloakClientSecretKeyPlain(encryptService.decrypt(keycloakConfiguration.getKeycloakClientSecretKeyEncrypted()));
		return keycloakConfiguration;
	}
}
