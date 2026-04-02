/**
 * @author mounif.haydar
 * 
 * Created on: Dec 9, 2024
 */
package com.comparator.config.security;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * 
 */
public class CustomTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// Implement client trust validation logic if needed
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// Implement server trust validation logic
		// For example, validate the certificate chain
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0]; // Return a list of accepted issuers
	}
}