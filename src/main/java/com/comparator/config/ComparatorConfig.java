package com.comparator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
@ComponentScan("com.compare")
public class ComparatorConfig {

	@Bean(name = "mapperIndent")
	public ObjectMapper serializingObjectMapper() {
		// JsonMapper.builder().configure
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
				.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
				.configure(SerializationFeature.INDENT_OUTPUT, true)
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
}
