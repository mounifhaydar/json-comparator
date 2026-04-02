package com.comparator.config.security;

import java.util.Collection;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.servlet.DispatcherServlet;

import com.comparator.keycloak.IKeyCloackAdminAdapter;
import com.comparator.keycloak.impl.KeyCloackAdminAdapter;
import com.comparator.permissions.action.filter.ActionsPermissionsFilter;
import com.comparator.model.config.KeycloakConfiguration;
//import com.comparator.manager.model.prop.comparatorAppProperties;
//import com.comparator.manager.service.redis.IRedisUserProfileService;

/**
 * child class must be annotated
 * 
 * @EnableWebSecurity
 * @EnableMethodSecurity
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 * @see org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
 */
public abstract class SecurityConfiguration {

	@Autowired
	KeycloakConfiguration	keycloakConfiguration;

//	@Autowired
//	comparatorAppProperties	comparatorAppProperties;

	@Bean
	public IKeyCloackAdminAdapter keyCloackAdminAdapter() throws SSLException {
		return new KeyCloackAdminAdapter(keycloakConfiguration.getRealm(),
				keycloakConfiguration.getKeycloakBaseUrl()/*, comparatorAppProperties.getWebClientMemorySize()*/, keycloakConfiguration.getClient(),
				keycloakConfiguration.getKeycloakClientSecretKeyPlain());
	}

	@Autowired
	DispatcherServlet			dispatcherServlet;

//	@Autowired
//	IRedisUserProfileService	redisUserProfileUtils;

	@Lazy
	@Autowired
	KeyCloackAdminAdapter		keyCloackAdminAdapter;

	/**
	 * 
	 * @return List of GrantedAuthority parsed from token
	 * @example return jwt -> return new ArrayList<>();
	 * @example return jwt -> { final var realmAccess = (Map<String, Object>)
	 *          jwt.getClaims().getOrDefault("realm_access", Map.of()); final
	 *          var realmRoles = (Collection<String>)
	 *          realmAccess.getOrDefault("roles", List.of());
	 * 
	 *          final var resourceAccess = (Map<String, Object>)
	 *          jwt.getClaims().getOrDefault("resource_access", Map.of()); // We
	 *          assume here you have [client-name] clients configured with
	 *          "client roles" mapper in Keycloak final var clientAccess =
	 *          (Map<String, Object>) resourceAccess.getOrDefault([client-name],
	 *          Map.of()); final var clientRoles = (Collection<String>)
	 *          clientAccess.getOrDefault("roles", List.of());
	 * 
	 *          return
	 *          Stream.concat(realmRoles.stream(),clientRoles.stream()).map(SimpleGrantedAuthority::new).toList();
	 *          };
	 */
	protected abstract Jwt2AuthoritiesConverter createAuthoritiesConverter();

	public interface Jwt2AuthoritiesConverter extends Converter<Jwt, Collection<? extends GrantedAuthority>> {
	}

	@Bean
	Jwt2AuthoritiesConverter authoritiesConverter() {
		return createAuthoritiesConverter();
	}

	interface Jwt2AuthenticationConverter extends Converter<Jwt, JwtAuthenticationToken> {
	}

	@Bean
	Jwt2AuthenticationConverter authenticationConverter(Converter<Jwt, Collection<? extends GrantedAuthority>> authoritiesConverter) {
		return jwt -> new JwtAuthenticationToken(jwt, authoritiesConverter.convert(jwt));
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, Converter<Jwt, ? extends AbstractAuthenticationToken> authenticationConverter, ServerProperties serverProperties) throws Exception {

		http.addFilterAfter(new ActionsPermissionsFilter(dispatcherServlet/*, redisUserProfileUtils*/, keyCloackAdminAdapter, false/* comparatorAppProperties.isDisableActionsPermissionsSecurity()*/), AuthorizationFilter.class);

		// Enable OAuth2 with custom authorities mapping
		http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(authenticationConverter);

		//        Enable and configure CORS
		//        http.cors().configurationSource(corsConfigurationSource());

		// State-less session (state in access-token only)
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Disable CSRF because of state-less session-management
		http.csrf().disable();

		// Return 401 (unauthorized) instead of 302 (redirect to login) when
		// authorization is missing or invalid
		http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
			response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Restricted Content\"");
			response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
		});

		// If SSL enabled, disable http (https only)
		if (serverProperties.getSsl() != null && serverProperties.getSsl().isEnabled()) {
			http.requiresChannel().anyRequest().requiresSecure();
		}

		// Route security: authenticated to all routes but actuator and Swagger-UI
		// @formatter:off
		http.authorizeHttpRequests()
				.requestMatchers("/actuator/health/readiness", "/actuator/health/liveness", "/v3/api-docs", "/v3/api-docs/**", "/profile", "/swagger-ui/**", "/login", "/shared-links/getSharedLink",
						"/shared-links/getSharedLinkContent", "/shared-links/getSharedLinkRenditions", "/shared-links/getSharedLinkRenditionContent")
				.permitAll().requestMatchers(HttpMethod.GET, "/actuator/**").hasAuthority("OBSERVABILITY:read").requestMatchers("/actuator/**").hasAuthority("OBSERVABILITY:write").anyRequest().authenticated();
		// @formatter:on

		return http.build();
	}
}
