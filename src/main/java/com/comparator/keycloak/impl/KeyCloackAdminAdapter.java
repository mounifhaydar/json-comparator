package com.comparator.keycloak.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLException;

import org.json.simple.JSONObject;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.comparator.keycloak.IKeyCloackAdminAdapter;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

public class KeyCloackAdminAdapter implements IKeyCloackAdminAdapter {
	Logger						logger					= LoggerFactory.getLogger(KeyCloackAdminAdapter.class);

	private final String		GET_USER_URL;
	private final String		GET_USER_ROLE_MAPPINGS_URL;
	private final String		GET_USER_GROUPS_URL;
	private final String		GET_GROUP_ROLE_MAPPINGS_URL;

	private final String		GENERATE_TOKEN_URL;
	private final String		GET_GROUPS_URL;
	private final String		GET_USERS_URL;
	private final String		GET_GROUP_MEMBERS_URL;
	private static final String	ACCESS_TOKEN_KEY		= "access_token";
	private static final String	AUTHORIZATION_TEMPLATE	= "Bearer %s";

	private String				resourceName;
	private String				credentialsSecret;

	private WebClient			webClient;

	public KeyCloackAdminAdapter(String realm, String authServerUrl/*, int webClientMemorySize*/, String resourceName, String credentialsSecret) throws SSLException {
		GET_USER_URL = String.format("/admin/realms/%s/users/", realm) + "%s";
		GET_USER_ROLE_MAPPINGS_URL = String.format("/admin/realms/%s/users/", realm) + "%s/role-mappings";
		GET_USER_GROUPS_URL = String.format("/admin/realms/%s/users/", realm) + "%s/groups";
		GET_GROUP_ROLE_MAPPINGS_URL = String.format("/admin/realms/%s/groups/", realm) + "%s/role-mappings";

		GET_GROUPS_URL = String.format("/admin/realms/%s/groups/", realm);
		GET_USERS_URL = String.format("/admin/realms/%s/users/", realm);
		GENERATE_TOKEN_URL = String.format("/realms/%s/protocol/openid-connect/token", realm);
		GET_GROUP_MEMBERS_URL = GET_GROUPS_URL + "%s/members";

		try {
			SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			HttpClient httpClient = HttpClient.create().wiretap(true).secure(t -> t.sslContext(sslContext));

			webClient = WebClient.builder().baseUrl(authServerUrl).exchangeStrategies(
					ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs()/*.maxInMemorySize(webClientMemorySize)*/).build())
					.clientConnector(new ReactorClientHttpConnector(httpClient)).build();
		} catch (SSLException e) {
			e.printStackTrace();
			throw e;
		}

		this.resourceName = resourceName;
		this.credentialsSecret = credentialsSecret;
	}

	private String getValidAuthorizationHeaderValue() {
		if (webClient != null) {
			MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
			formData.add("client_id", resourceName);
			formData.add("client_secret", credentialsSecret);
			formData.add("grant_type", "client_credentials");

			ResponseEntity<JSONObject> response = webClient.post().uri(GENERATE_TOKEN_URL).header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE).body(BodyInserters.fromFormData(formData)).retrieve()
					.toEntity(JSONObject.class).block();

			if (response.hasBody()) {
				return String.format(AUTHORIZATION_TEMPLATE, (String) response.getBody().get(ACCESS_TOKEN_KEY));
			}
		}

		throw new RuntimeException("Unable to get a valid access token");
	}

	@Override
	public List<GroupRepresentation> getGroups() {
		if (webClient != null) {
			ResponseEntity<GroupRepresentation[]> response = webClient.get().uri(GET_GROUPS_URL).header(HttpHeaders.AUTHORIZATION, getValidAuthorizationHeaderValue()).retrieve().toEntity(GroupRepresentation[].class)
					.block();
			if (response.hasBody()) {
				var groups = response.getBody();
				List<GroupRepresentation> ret = new ArrayList<>();

				for (GroupRepresentation group : groups) {
					fillGroupsRecursivly(ret, group);
				}

				return ret;
			}
		}

		throw new RuntimeException("Unable to get groups");
	}

	private void fillGroupsRecursivly(List<GroupRepresentation> ret, GroupRepresentation group) {
		ret.add(group);
		for (GroupRepresentation subGroup : group.getSubGroups()) {
			fillGroupsRecursivly(ret, subGroup);
		}
	}

	@Override
	public List<UserRepresentation> getUsers() {
		if (webClient != null) {
			ResponseEntity<UserRepresentation[]> response = webClient.get().uri(GET_USERS_URL).header(HttpHeaders.AUTHORIZATION, getValidAuthorizationHeaderValue()).retrieve().toEntity(UserRepresentation[].class)
					.block();
			if (response.hasBody()) {
				return Arrays.asList(response.getBody());
			}
		}

		throw new RuntimeException("Unable to get user");
	}

	@Override
	public List<String> getGroupMembers(String groupId) {
		if (webClient != null) {
			ResponseEntity<UserRepresentation[]> response = webClient.get().uri(String.format(GET_GROUP_MEMBERS_URL, groupId)).header(HttpHeaders.AUTHORIZATION, getValidAuthorizationHeaderValue()).retrieve()
					.toEntity(UserRepresentation[].class).block();
			if (response.hasBody()) {
				return Arrays.asList(response.getBody()).stream().map(a -> a.getUsername()).toList();
			}
		}

		throw new RuntimeException("Unable to get group members");
	}

	@Override
	public UserRepresentation getUser(String userId) {
		try {
			if (webClient != null) {
				ResponseEntity<UserRepresentation> response = webClient.get().uri(String.format(GET_USER_URL, userId)).header(HttpHeaders.AUTHORIZATION, getValidAuthorizationHeaderValue()).retrieve()
						.toEntity(UserRepresentation.class).block();
				if (response.hasBody()) {
					return response.getBody();
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			throw e;
		}

		return null;
	}

	@Override
	public MappingsRepresentation getUserRoles(String userId) {
		try {
			if (webClient != null) {
				ResponseEntity<MappingsRepresentation> response = webClient.get().uri(String.format(GET_USER_ROLE_MAPPINGS_URL, userId)).header(HttpHeaders.AUTHORIZATION, getValidAuthorizationHeaderValue()).retrieve()
						.toEntity(MappingsRepresentation.class).block();
				if (response.hasBody()) {
					return response.getBody();
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			throw e;
		}

		return null;
	}

	@Override
	public List<GroupRepresentation> getUserGroups(String userId) {
		try {
			if (webClient != null) {
				ResponseEntity<GroupRepresentation[]> response = webClient.get().uri(String.format(GET_USER_GROUPS_URL, userId)).header(HttpHeaders.AUTHORIZATION, getValidAuthorizationHeaderValue()).retrieve()
						.toEntity(GroupRepresentation[].class).block();
				if (response.hasBody()) {
					return Arrays.asList(response.getBody());
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			throw e;
		}

		return Arrays.asList();
	}

	@Override
	public MappingsRepresentation getGroupRoles(String groupId) {
		try {
			if (webClient != null) {
				ResponseEntity<MappingsRepresentation> response = webClient.get().uri(String.format(GET_GROUP_ROLE_MAPPINGS_URL, groupId)).header(HttpHeaders.AUTHORIZATION, getValidAuthorizationHeaderValue()).retrieve()
						.toEntity(MappingsRepresentation.class).block();
				if (response.hasBody()) {
					return response.getBody();
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			throw e;
		}

		return null;
	}
}
