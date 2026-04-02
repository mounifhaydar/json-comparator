package com.comparator.config.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.comparator.model.config.KeycloakConfiguration;

@Configuration
public class WebSecurityConfig {

	@Bean
	public SecurityConfiguration createSecurityConfiguration() {
		return new comparatorSecurityConfiguration();//DmsSecurityConfiguration
	}

	@EnableWebSecurity
	@EnableMethodSecurity
	private static class comparatorSecurityConfiguration extends SecurityConfiguration {

		@Autowired
		KeycloakConfiguration keycloakConfiguration;

		@SuppressWarnings("unchecked")
		@Override
		protected Jwt2AuthoritiesConverter createAuthoritiesConverter() {

			return jwt -> {
				final var realmAccess = (Map<String, Object>) jwt.getClaims().getOrDefault("realm_access", Map.of());
				final var realmRoles = (Collection<String>) realmAccess.getOrDefault("roles", List.of());

				final var resourceAccess = (Map<String, Object>) jwt.getClaims().getOrDefault("resource_access", Map.of());
				// We assume here you have [client-name] clients configured with "client roles" mapper in Keycloak
				final var clientAccess = (Map<String, Object>) resourceAccess.getOrDefault(keycloakConfiguration.getClient(), Map.of());
				final var clientRoles = (Collection<String>) clientAccess.getOrDefault("roles", List.of());

				return Stream.concat(realmRoles.stream(), clientRoles.stream()).map(SimpleGrantedAuthority::new).toList();
			};
		}

	}
}
