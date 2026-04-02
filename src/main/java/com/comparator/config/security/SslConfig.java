/**
 * @author mounif.haydar
 * 
 * Created on: Dec 9, 2024
 */
package com.comparator.config.security;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 */
@Configuration
public class SslConfig {
	 @Bean
	    public SSLContext sslContext() throws NoSuchAlgorithmException, KeyManagementException {
	        SSLContext sslContext = SSLContext.getInstance("TLS");

	        // Create and set custom TrustManager
	        TrustManager[] trustManagers = {new CustomTrustManager()};
	        sslContext.init(null, trustManagers, new java.security.SecureRandom());

	        return sslContext;
	    }
}
