package com.comparator.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class UserUtils {
	public static String getLoggedInUserPreferedUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//		if (authentication instanceof KeycloakAuthenticationToken) {
//			KeycloakAuthenticationToken authenticationToken = (KeycloakAuthenticationToken) authentication;
//			AccessToken token = authenticationToken.getAccount().getKeycloakSecurityContext().getToken();
//			return token.getPreferredUsername();
//		}
		
		if (authentication != null &&  authentication instanceof JwtAuthenticationToken) {	
			JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
			return auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME);
		}

		return null;
	}
}
