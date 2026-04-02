package com.comparator.keycloak;

import java.util.List;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public interface IKeyCloackAdminAdapter {

	List<GroupRepresentation> getGroups();

	List<UserRepresentation> getUsers();

	List<String> getGroupMembers(String groupId);

	UserRepresentation getUser(String userId);

	MappingsRepresentation getUserRoles(String userId);

	List<GroupRepresentation> getUserGroups(String userId);

	MappingsRepresentation getGroupRoles(String groupId);

}
