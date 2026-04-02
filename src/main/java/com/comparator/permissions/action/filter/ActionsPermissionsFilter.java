package com.comparator.permissions.action.filter;

import java.io.IOException;

import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;

import com.comparator.keycloak.impl.KeyCloackAdminAdapter;
//import com.comparator.manager.db.permissions.ActionsPermissions;
//import com.comparator.manager.model.db.redis.RedisUserProfile;
//import com.comparator.manager.service.redis.IRedisUserProfileService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ActionsPermissionsFilter implements Filter {
	Logger							logger	= LoggerFactory.getLogger(ActionsPermissionsFilter.class);

	private DispatcherServlet		dispatcherServlet;

//	private IRedisUserProfileService	redisUserProfileUtils;

	private KeyCloackAdminAdapter	keyCloackAdminAdapter;

	private boolean					disableActionsPermissionsSecurity;

	public ActionsPermissionsFilter(DispatcherServlet dispatcherServlet/*, IRedisUserProfileService redisUserProfileUtils*/, KeyCloackAdminAdapter keyCloackAdminAdapter, boolean disableActionsPermissionsSecurity) {
		this.dispatcherServlet = dispatcherServlet;
//		this.redisUserProfileUtils = redisUserProfileUtils;
		this.keyCloackAdminAdapter = keyCloackAdminAdapter;
		this.disableActionsPermissionsSecurity = disableActionsPermissionsSecurity;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			var httpServletRequest = (HttpServletRequest) request;
			String method = httpServletRequest.getMethod();
			String uri = httpServletRequest.getRequestURI().toString();

			ServletRequestPathUtils.parseAndCache(httpServletRequest);
			for (HandlerMapping mapping : this.dispatcherServlet.getHandlerMappings()) {
				HandlerExecutionChain handler;
				try {
					handler = mapping.getHandler(httpServletRequest);
					if (handler != null)
						break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String pathTemplate = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

			if (uri.contains("/profile") || uri.contains("/v3/api-docs") || uri.contains("/swagger-ui/") || uri.equals("/") || uri.contains("/shared-links/getSharedLink")
					|| uri.contains("/shared-links/getSharedLinkContent") || uri.contains("/shared-links/getSharedLinkRenditions") || uri.contains("/shared-links/getSharedLinkRenditionContent") || uri.equals("/login")) {
				filterchain.doFilter(request, response);
				return;
			}

			var authenticationToken = SecurityContextHolder.getContext().getAuthentication();
			if (authenticationToken != null && authenticationToken instanceof JwtAuthenticationToken) {

				JwtAuthenticationToken auth = (JwtAuthenticationToken) authenticationToken;
				String username = auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME);
				String userKeycloakId = auth.getToken().getClaimAsString(StandardClaimNames.SUB);

				if (userKeycloakId == null) {
					((HttpServletResponse) response).sendError(401);
					return;
				}

				//TODO to check user profile from database once the table is synced with keycloack
				UserRepresentation user = keyCloackAdminAdapter.getUser(userKeycloakId);

				if (user == null) {
					((HttpServletResponse) response).sendError(401);
					return;
				}

				if (disableActionsPermissionsSecurity) {
					logger.info("Actions Permissions security disabled");
					filterchain.doFilter(request, response);
					return;
				}

//				redisUserProfileUtils.setKeyCloackAdminAdapter(keyCloackAdminAdapter);
//				RedisUserProfile rdsStore = redisUserProfileUtils.getProfileFromCache(userKeycloakId);

//				if (rdsStore.actionsPermissions != null) {
//					for (ActionsPermissions actionsPermissions : rdsStore.actionsPermissions) {
//						if (pathTemplate.equalsIgnoreCase(actionsPermissions.resourceName) && actionsPermissions.actionName.equalsIgnoreCase(method)) {
//							//TODO HERE: check permission if granted to USER - REQUEST-METHOD
//							logger.info("Access granted " + username + " " + userKeycloakId);
//							filterchain.doFilter(request, response);
//							return;
//						}
//					}
//				}
			}

			((HttpServletResponse) response).sendError(403);
		}
	}

}
