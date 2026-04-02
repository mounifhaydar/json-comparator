/**
 * @author mounif.haydar
 * 
 * Created on: Dec 11, 2024
 */
package com.comparator.model.config;

import lombok.Data;

/**
 * 
 */
@Data
public class KeycloakConfiguration {
	private String	keycloakBaseUrl;
	private String	keycloakClientSecretKeyEncrypted;
	private String	keycloakClientSecretKeyPlain;
	private String	realm;
	private String	client;
}
