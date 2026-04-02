/**
 * @author mounif.haydar
 * 
 * Created on: Dec 11, 2024
 */
package com.comparator.model.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "app.config")
public class AppConfigKeys {
	private String	comparator;
	private String	keycloak;
	private String	alfresco;
	private String	dbcomparator;

}
