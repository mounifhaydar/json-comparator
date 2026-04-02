//package com.comparator.permissions.action.filter;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.lang.reflect.ParameterizedType;
//import java.time.ZonedDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.util.ClassUtils;
//
//import com.comparator.core.security.domain.CustomBasePermission;
//import com.comparator.core.security.domain.CustomDefaultPermissionFactory;
//import com.comparator.core.security.domain.CustomObjectIdentityRetrievalStrategyImpl;
//import com.comparator.core.security.domain.CustomPermissionFactory;
//import com.comparator.core.security.domain.DefinedPermissions;
//import com.comparator.core.security.model.CustomObjectIdentity;
//import com.comparator.core.security.model.CustomPermission;
//import com.comparator.manager.db.permissions.CustomResult;
//import com.comparator.manager.db.permissions.DataPermissions;
//import com.comparator.manager.model.db.redis.RedisUserProfile;
//import com.comparator.manager.service.redis.impl.RedisUserProfileService;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//
//public class AclPermissionEvaluator {
//	
//	Logger logger = LoggerFactory.getLogger(AclPermissionEvaluator.class);	
//	
//	private CustomObjectIdentityRetrievalStrategyImpl objectIdentityRetrievalStrategy = new CustomObjectIdentityRetrievalStrategyImpl();
//	
//	private CustomPermissionFactory permissionFactory = new CustomDefaultPermissionFactory();
//	
//	private boolean disableSecurity = false;
//	
//	@Autowired
//	RedisUserProfileService redisUserProfileUtils;
//	
//	static ObjectMapper mapper = new ObjectMapper();
//	static {
//		mapper.registerModule(new JavaTimeModule());
//	}
//
//	public AclPermissionEvaluator(boolean disableSecurity) {
//		this.disableSecurity = disableSecurity;
//	}
//
////	public boolean hasPermission(Object domainObject, Object permission, Class<?> valueType) throws Exception {	
////		if(disableSecurity) return true;
////		
////		if (domainObject == null) {
////			this.logger.error("domainObject is null");
////			return false;
////		}
////		
////		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////		
////		if(authentication == null) {
////			this.logger.error("authentication is null");
////			return false;
////		}	
////				
////		Object object = mapper.readValue(mapper.writeValueAsString(domainObject), valueType);
////		return checkPermission(authentication, object, DefinedPermissions.ADMINISTRATION) || checkPermission(authentication, object, permission);
////	}
//	
//	public boolean hasPermission(Object domainObject, Object permission) throws Exception {	
//		if(disableSecurity) return true;
//		
//		if (domainObject == null) {
//			this.logger.error("domainObject is null");
//			return false;
//		}
//		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		
//		if(authentication == null) {
//			this.logger.error("authentication is null");
//			return false;
//		}	
//				
//		return checkPermission(authentication, domainObject, DefinedPermissions.ADMINISTRATION) || checkPermission(authentication, domainObject, permission);
//	}
//	
////	public List<?> filterList(List<?> domainObjects, Object permission, Class<?> valueType) throws Exception {
////		List<Object> result = new ArrayList<Object>();
////		
////		if(disableSecurity) {
////			for (Object domainObject : domainObjects) {
////				Object object = mapper.readValue(mapper.writeValueAsString(domainObject), valueType);
////				result.add(object);
////			}
////			return result;
////		}
////		
////		if (domainObjects == null) {
////			return new ArrayList<Object>();
////		}
////		
////		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////		
////		if(authentication == null)
////			return new ArrayList<Object>();
////		
////		for (Object domainObject : domainObjects) {
////			Object object = mapper.readValue(mapper.writeValueAsString(domainObject), valueType);
////			if (checkPermission(authentication, object, DefinedPermissions.ADMINISTRATION) || checkPermission(authentication, object, permission))
////				result.add(object);
////		}
////		
////		return result;
////	}
//	
//	public List<?> filterList(List<?> domainObjects, Object permission) throws Exception {
//		List<Object> result = new ArrayList<Object>();
//		
//		if(disableSecurity) {
//			result.addAll(domainObjects);
//			return result;
//		}
//		
//		if (domainObjects == null) {
//			return new ArrayList<Object>();
//		}
//		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		
//		if(authentication == null)
//			return new ArrayList<Object>();
//		
//		for (Object domainObject : domainObjects) {
//			if (checkPermission(authentication, domainObject, DefinedPermissions.ADMINISTRATION) || checkPermission(authentication, domainObject, permission))
//				result.add(domainObject);
//		}
//		
//		return result;
//	}
//	
//	public ResponseEntity<?> putNonAccessiblePropertiesNullOnRead(Object domainObject, Class<?> valueType, String objectId) throws Exception {
//		if(disableSecurity)
//			try {
//				Object object = mapper.readValue(mapper.writeValueAsString(domainObject), valueType); 
//				return new ResponseEntity<Object>(object, HttpStatus.OK);
//			} catch (Exception e) {
//				return null;
//			} 
//		
//		if (domainObject == null) {
//			this.logger.error("domainObject is null");
//			return new ResponseEntity<Object>(HttpStatus.FORBIDDEN);
//		}
//		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		
//		if(authentication == null) {
//			this.logger.error("authentication is null");
//			return new ResponseEntity<Object>(HttpStatus.FORBIDDEN);
//		}
//		
//		Object object = mapper.readValue(mapper.writeValueAsString(domainObject), valueType); 
//		
//		CustomResult result = putNonAccessiblePropertiesNullOnRead(authentication, object, DefinedPermissions.PROPSADMINISTRATION, objectId);
//		if (result.withError) {
//			this.logger.error("{}", result.message);
//			result = putNonAccessiblePropertiesNullOnRead(authentication, object, DefinedPermissions.PROPSREAD, objectId);
//			if (result.withError) {
//				this.logger.error("{}", result.message);
//				return new ResponseEntity<Object>(HttpStatus.FORBIDDEN);
//			}
//		}
//		
//		return new ResponseEntity<Object>(object, HttpStatus.OK);
//	}
//	
//	public Object putObjectNonAccessiblePropertiesNullOnRead(Object domainObject, Class<?> valueType, String objectId) {
//		if(disableSecurity)
//			try {
//				Object object = mapper.readValue(mapper.writeValueAsString(domainObject), valueType); 
//				return object;
//			} catch (Exception e) {
//				return null;
//			} 
//		
//		if (domainObject == null) {
//			this.logger.error("domainObject is null");
//			return null;
//		}
//		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		
//		if(authentication == null) {
//			this.logger.error("authentication is null");
//			return null;
//		}
//		
//		try {
//			Object object = mapper.readValue(mapper.writeValueAsString(domainObject), valueType); 
//
//			CustomResult result = putNonAccessiblePropertiesNullOnRead(authentication, object, DefinedPermissions.PROPSADMINISTRATION, objectId);
//			if (result.withError) {
//				this.logger.error("{}", result.message);
//				result = putNonAccessiblePropertiesNullOnRead(authentication, object, DefinedPermissions.PROPSREAD, objectId);
//				if (result.withError) {
//					this.logger.error("{}", result.message);
//					return null;
//				}
//			}
//			
//			return object;
//		} catch (Exception e) {
//			return null;
//		} 
//	}
//	
//	public List<?> putNonAccessiblePropertiesNullOnRead(List<?> domainObjects, Class<?> valueType) throws Exception {
//		List<Object> result = new ArrayList<Object>();
//		
//		if(disableSecurity) {
//			for (Object domainObject : domainObjects) {
//				Object object = mapper.readValue(mapper.writeValueAsString(domainObject), valueType);
//				result.add(object);
//			}
//			return result;
//		}
//		
//		if (domainObjects == null) {
//			this.logger.error("domainObject is null");
//			return new ArrayList<Object>();
//		}
//		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		
//		if(authentication == null) {
//			this.logger.error("authentication is null");
//			return new ArrayList<Object>();
//		}
//		
//		for (Object domainObject : domainObjects) {
//			CustomObjectIdentity oid = this.objectIdentityRetrievalStrategy.getObjectIdentity(domainObject);
//			Object object = mapper.readValue(mapper.writeValueAsString(domainObject), valueType);
//			CustomResult customResult = putNonAccessiblePropertiesNullOnRead(authentication, object, DefinedPermissions.PROPSADMINISTRATION, (String) oid.getIdentifier());
//			if (customResult.withError) {
//				this.logger.error("{} {}", object, customResult.message);	
//				customResult = putNonAccessiblePropertiesNullOnRead(authentication, object, DefinedPermissions.PROPSREAD, (String) oid.getIdentifier());
//				if (customResult.withError) {
//					this.logger.error("{} {}", object, customResult.message);	
//				} else {
//					result.add(object);
//				}
//			} else {
//				result.add(object);
//			}
//		}
//		
//		return result;
//	}
//	
////	public boolean hasPropertiesPermissionOnWrite(Object domainObject) {
////		if (domainObject == null) {
////			this.logger.error("domainObject is null");
////			return false;
////		}
////		
////		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////		
////		if(authentication == null) {
////			this.logger.error("authentication is null");
////			return false;
////		}
////		
////		CustomResult result = checkPropertiesPermission(authentication, domainObject, DefinedPermissions.WRITE);
////		if (result.withError) {
////			this.logger.error("{}", result.message);
////			return false;
////		}
////		
////		return true;
////	}
//	
//	public CustomResult hasPropertiesPermissionOnWrite(Object domainObject, Class<?> valueType, String objectId) {
//		if(disableSecurity) return new CustomResult("", false);
//		
//		if (domainObject == null) {
//			this.logger.error("domainObject is null");
//			return new CustomResult("domainObject is null", true);
//		}
//		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		
//		if(authentication == null) {
//			this.logger.error("authentication is null");
//			return new CustomResult("authentication is null", true);
//		}
//		
//		CustomResult result = checkPropertiesPermission(authentication, domainObject, DefinedPermissions.PROPSADMINISTRATION, valueType, objectId);
//		if (result.withError) {
//			this.logger.error("{}", result.message);
//			result = checkPropertiesPermission(authentication, domainObject, DefinedPermissions.PROPSWRITE, valueType, objectId);
//			if (result.withError) {
//				this.logger.error("{}", result.message);
//				return result;
//			}
//		}	
//		
//		return new CustomResult(result.message, false);
//	}
//	
//	private CustomResult putNonAccessiblePropertiesNullOnRead(Authentication authentication, Object object, Object permission, String objectId) {
//		String username = null;
//		String userKeycloakId = null;
//		if (authentication != null &&  authentication instanceof JwtAuthenticationToken) {
////			username = authentication.getName();	
//			JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
//			username = auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME);
//			userKeycloakId = auth.getToken().getClaimAsString(StandardClaimNames.SUB);
//		}
//
//		if(username == null) return new CustomResult("username is null", true);
//		
//		List<CustomPermission> requiredPermission = resolvePermission(permission);		
//		
//		if (requiredPermission.isEmpty()) 
//			return new CustomResult("requiredPermission is empty", true);
//		
//		RedisUserProfile rdsStore = redisUserProfileUtils.getProfileFromCache(userKeycloakId);	
//		
//		Class<?> typeClass = ClassUtils.getUserClass(object.getClass());
//		
//		this.logger.info("Object '{}' {}", object, typeClass);
//		
//		String fieldName = "";
//		
//		try {		
//			Field[] fields = typeClass.getFields();
//			for (Field field : fields) {
//				fieldName = field.getName();
//				Class<?> typeField = field.getType();
//				
//				if(field.get(object) != null) {
//					if(isPrimitiveType(typeField) || isArrayOfPrimitiveType(typeField)) {						
//						if(isAccessibleProperty(typeClass, fieldName, typeField.getTypeName(), rdsStore.dataPermissions, requiredPermission, objectId)) {
//							this.logger.info(">>>>>>>>>>>>>>>> Accessible field  '{}' '{}'", fieldName, typeField.getTypeName());		
//						} else {
////							this.logger.info("Non accessible field  '{}' '{}'", fieldName, typeField.getTypeName());
//							field.set(object, null);
//						}
//					} else if(typeField.getTypeName().equalsIgnoreCase(List.class.getTypeName()) || typeField.getTypeName().equalsIgnoreCase(ArrayList.class.getTypeName())
//									|| typeField.getTypeName().equalsIgnoreCase(Set.class.getTypeName()) || typeField.getTypeName().equalsIgnoreCase(HashSet.class.getTypeName())
//									|| typeField.getTypeName().equalsIgnoreCase(Collection.class.getTypeName())) {
//						ParameterizedType pt = (ParameterizedType)object.getClass().getDeclaredField(fieldName).getGenericType();
//				        Class<?> listFieldClass = (Class<?>) pt.getActualTypeArguments()[0];
//				        if(isPrimitiveType(listFieldClass)) {
//				        	if(isAccessibleProperty(typeClass, fieldName, typeField.getTypeName(), rdsStore.dataPermissions, requiredPermission, objectId)) {
//								this.logger.info(">>>>>>>>>>>>>>>> Accessible field  '{}' '{}' {}", fieldName, typeField.getTypeName(), listFieldClass);		
//							} else {
////								this.logger.info("Non accessible field  '{}' '{}' {}", fieldName, typeField.getTypeName(), listFieldClass);
//								field.set(object, null);
//							}
//				        } else {
//				        	@SuppressWarnings("unchecked")
//							Collection<Object> newList = (Collection<Object>) field.get(object);
//							for (Object entry : newList) {
//								if(isPrimitiveType(entry.getClass())) {
//									if(isAccessibleProperty(typeClass, fieldName, typeField.getTypeName(), rdsStore.dataPermissions, requiredPermission, objectId)) {
//										this.logger.info(">>>>>>>>>>>>>>>> Accessible field  '{}' '{}'", fieldName, typeField.getTypeName());		
//									} else {
////										this.logger.info("Non accessible field  '{}' '{}'", fieldName, typeField.getTypeName());
//										field.set(object, null);
//									}
//									break;
//								} else {
//									CustomResult error = putNonAccessiblePropertiesNullOnRead(authentication, entry, permission, objectId);
//									if (error.withError)
//										return new CustomResult(error.message, true);	
//								}
//							}
//				        }	
//					} else if(typeField.getTypeName().equalsIgnoreCase(Map.class.getTypeName()) || typeField.getTypeName().equalsIgnoreCase(HashMap.class.getTypeName())) {
//						ParameterizedType pt = (ParameterizedType)object.getClass().getDeclaredField(fieldName).getGenericType();
//						Class<?> listFieldValueClass = (Class<?>) pt.getActualTypeArguments()[1];		
//				        if(isPrimitiveType(listFieldValueClass)) {
//				        	if(isAccessibleProperty(typeClass, fieldName, typeField.getTypeName(), rdsStore.dataPermissions, requiredPermission, objectId)) {
//								this.logger.info(">>>>>>>>>>>>>>>> Accessible field  '{}' '{}' {}", fieldName, typeField.getTypeName(), listFieldValueClass);		
//							} else {
////								this.logger.info("Non accessible field  '{}' '{}' {}", fieldName, typeField.getTypeName(), listFieldValueClass);
//								field.set(object, null);
//							}
//				        } else {
//							@SuppressWarnings("unchecked")
//							Map<Object, Object> newList = (Map<Object, Object>) field.get(object);
//							for (Entry<Object, Object> entry : newList.entrySet()) {
//								if(isPrimitiveType(entry.getValue().getClass())) {
//									if(isAccessibleProperty(typeClass, fieldName, typeField.getTypeName(), rdsStore.dataPermissions, requiredPermission, objectId)) {
//										this.logger.info(">>>>>>>>>>>>>>>> Accessible field  '{}' '{}'", fieldName, typeField.getTypeName());		
//									} else {
////										this.logger.info("Non accessible field  '{}' '{}'", fieldName, typeField.getTypeName());
//										field.set(object, null);										
//									}
//									break;
//								} else {
//									CustomResult error = putNonAccessiblePropertiesNullOnRead(authentication, entry.getValue(), permission, objectId);
//									if (error.withError)
//										return new CustomResult(error.message, true); 
//								}
//							}
//				        }
//					} else if(typeField.getTypeName().equalsIgnoreCase(ArrayNode.class.getTypeName())) {
//						ArrayNode arrayNode = (ArrayNode) field.get(object);
//						Iterator<JsonNode> nodeIterator = arrayNode.iterator();
//				        while (nodeIterator.hasNext()) {
//				          JsonNode elementNode = nodeIterator.next();
//				          CustomResult error = putNonAccessibleJsonPropertiesNullOnRead(authentication, elementNode, permission, objectId);
//				          if (error.withError)
//				        	  return new CustomResult(error.message, true); 
//				        }
//					} else if(typeField.getTypeName().equalsIgnoreCase(ObjectNode.class.getTypeName())) {
//						ObjectNode jsonNode = (ObjectNode) field.get(object);
//						Iterator<Entry<String, JsonNode>> iter = jsonNode.fields();
//						while (iter.hasNext()) {
//							Entry<String, JsonNode> entry = iter.next();
//							if (entry.getValue().isObject()) {
//								CustomResult error = putNonAccessibleJsonPropertiesNullOnRead(authentication, entry.getValue(), permission, objectId);
//						          if (error.withError)
//						        	  return new CustomResult(error.message, true); 
//							} else if (entry.getValue().isArray()) {
//								ArrayNode arrayNode = (ArrayNode) entry.getValue();
//								Iterator<JsonNode> nodeIterator = arrayNode.iterator();
//						        while (nodeIterator.hasNext()) {
//						          JsonNode elementNode = nodeIterator.next();
//						          CustomResult error = putNonAccessibleJsonPropertiesNullOnRead(authentication, elementNode, permission, objectId);
//						          if (error.withError)
//						        	  return new CustomResult(error.message, true); 
//						        }
//							} else if (entry.getValue().isValueNode()) {
//								if(isAccessibleProperty(typeClass, entry.getKey(), entry.getClass().getTypeName(), rdsStore.dataPermissions, requiredPermission, objectId)) {
//									this.logger.info(">>>>>>>>>>>>>>>> Accessible field  '{}' '{}'", entry.getKey(), entry.getClass().getTypeName());		
//								} else {
////									this.logger.info("Non accessible field  '{}' '{}'", entry.getKey(), entry.getClass().getTypeName());
//									jsonNode.putNull(entry.getKey());
//								}
//							}
//						}	
//					} else {
//						CustomResult error = putNonAccessiblePropertiesNullOnRead(authentication, field.get(object), permission, objectId);
//						if (error.withError)
//							return new CustomResult(error.message, true); 
//					}
//				}
//			}
//		} catch (Exception ex) {
//			this.logger.error("{} object {} : {}", fieldName, object, ex);
//			return new CustomResult(ex.toString(), true);
//		}
//		
//		return new CustomResult("", false);
//	}
//	
//	private CustomResult putNonAccessibleJsonPropertiesNullOnRead(Authentication authentication, Object object, Object permission, String objectId) {
//		String username = null;
//		if (authentication != null &&  authentication instanceof JwtAuthenticationToken) {	
//			JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
//			username = auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME);
//		}
//
//		if(username == null) return new CustomResult("username is null", true);
//		
//		List<CustomPermission> requiredPermission = resolvePermission(permission);		
//		
//		if(requiredPermission.isEmpty()) return new CustomResult("requiredPermission is empty", true);
//		
//		RedisUserProfile rdsStore = redisUserProfileUtils.getProfileFromCache(username);	
//		
//		Class<?> typeClass = ClassUtils.getUserClass(object.getClass());
//		
//		this.logger.info("Object '{}' {}", object, typeClass);
//		
//		try {
//			if(typeClass.getTypeName().equalsIgnoreCase(ArrayNode.class.getTypeName())) {
//				ArrayNode arrayNode = (ArrayNode) object;
//				Iterator<JsonNode> nodeIterator = arrayNode.iterator();
//		        while (nodeIterator.hasNext()) {
//		          JsonNode elementNode = nodeIterator.next();
//		          CustomResult error = putNonAccessibleJsonPropertiesNullOnRead(authentication, elementNode, permission, objectId);
//		          if (error.withError)
//		        	  return new CustomResult(error.message, true); 
//		        }
//			} else if(typeClass.getTypeName().equalsIgnoreCase(ObjectNode.class.getTypeName())) {
//				ObjectNode jsonNode = (ObjectNode) object;
//				Iterator<Entry<String, JsonNode>> iter = jsonNode.fields();
//				while (iter.hasNext()) {
//					Entry<String, JsonNode> entry = iter.next();
//					if (entry.getValue().isObject()) {
//						CustomResult error = putNonAccessibleJsonPropertiesNullOnRead(authentication, entry.getValue(), permission, objectId);
//				          if (error.withError)
//				        	  return new CustomResult(error.message, true); 
//					} else if (entry.getValue().isArray()) {
//						ArrayNode arrayNode = (ArrayNode) entry.getValue();
//						Iterator<JsonNode> nodeIterator = arrayNode.iterator();
//				        while (nodeIterator.hasNext()) {
//				          JsonNode elementNode = nodeIterator.next();
//				          CustomResult error = putNonAccessibleJsonPropertiesNullOnRead(authentication, elementNode, permission, objectId);
//				          if (error.withError)
//				        	  return new CustomResult(error.message, true);
//				        }
//					} else if (entry.getValue().isValueNode()) {
//						if(isAccessibleProperty(typeClass, entry.getKey(), entry.getClass().getTypeName(), rdsStore.dataPermissions, requiredPermission, objectId)) {
//							this.logger.info(">>>>>>>>>>>>>>>> Accessible field  '{}' '{}'", entry.getKey(), entry.getClass().getTypeName());		
//						} else {
////							this.logger.info("Non accessible field  '{}' '{}'", entry.getKey(), entry.getClass().getTypeName());
//							jsonNode.putNull(entry.getKey());
//						}
//					}
//				}
//			}
//		} catch (Exception ex) {
//			this.logger.error("object {} : {}", object, ex);
//			return new CustomResult(ex.toString(), true);
//		}
//		
//		return new CustomResult("", false);
//	}
//	
//	private CustomResult checkPropertiesPermission(Authentication authentication, Object object, Object permission, Class<?> valueType, String objectId) {
//		String username = null;
//		if (authentication != null &&  authentication instanceof JwtAuthenticationToken) {	
//			JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
//			username = auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME);
//		}
//
//		if(username == null) return new CustomResult("username is null", true);
//		
//		List<CustomPermission> requiredPermission = resolvePermission(permission);		
//		
//		if(requiredPermission.isEmpty()) return new CustomResult("requiredPermission is empty", true);		
//		
//		RedisUserProfile rdsStore = redisUserProfileUtils.getProfileFromCache(username);
//		
//		this.logger.info("Object '{}'", object);
//		
//		Class<?> typeClass = ClassUtils.getUserClass(object.getClass());
//		String fieldName = "";
//		
//		try {		
//			Field[] fields = typeClass.getFields();
//			for (Field field : fields) {
//				if(field.get(object) != null ) {
//					fieldName = field.getName();
//					Class<?> typeField = field.getType();
//					
////					if(!isAccessibleProperty(typeClass, fieldName, typeField.getTypeName(), rdsStore.dataPermissions, requiredPermission)) {
//					if(!isAccessibleProperty(valueType, fieldName, typeField.getTypeName(), rdsStore.dataPermissions, requiredPermission, objectId)) {
////						this.logger.info("Non accessible field  '{}'", fieldName);
//						return new CustomResult("Non accessible field " + fieldName, true);
//					} else {
//						this.logger.info(">>>>>>>>>>>>>>>> Accessible field  '{}'", fieldName);					
//					}
//				}
//			}
//		} catch (Exception ex) {
//			this.logger.error("{} object {} : {}", fieldName, object, ex);
//			return new CustomResult("Non accessible field " + fieldName, true);
//		}
//		
//		return new CustomResult("", false);
//	}
//	
//	private boolean isAccessibleProperty(Class<?> typeClass, String fieldName, String fieldType, List<DataPermissions> dataPermissions, List<CustomPermission> permission, String entryId) {
//		if(dataPermissions != null) {
//			for(CustomPermission p : permission) {
//				this.logger.info("'{}' 'Type:{}' ==> '{}' '{}'", p.getCode(), typeClass, fieldName, fieldType);
//				List<DataPermissions> filteredDataPermissions = dataPermissions.stream()
//																				.filter(dataPermission -> (dataPermission.mask == p.getMask() && dataPermission.resources.equals(typeClass.getName()) && dataPermission.property.equalsIgnoreCase(fieldName) && dataPermission.filterValue.equals(entryId)))
//																				.toList();
//				
//				if(filteredDataPermissions.size() == 0) {
//					return false;
//				}
//			}
//		} else
//			return false;
//		
//		return true;
//	}
//	
//	private boolean isPrimitiveType(Class<?> typeField) {
//		if(typeField.getTypeName().equals(Byte.class.getTypeName()) || typeField.getTypeName().equals(byte.class.getTypeName())
//				|| typeField.getTypeName().equals(Short.class.getTypeName()) || typeField.getTypeName().equals(short.class.getTypeName())
//				|| typeField.getTypeName().equals(Integer.class.getTypeName()) || typeField.getTypeName().equals(int.class.getTypeName())
//				|| typeField.getTypeName().equals(Long.class.getTypeName()) || typeField.getTypeName().equals(long.class.getTypeName())
//				|| typeField.getTypeName().equals(Float.class.getTypeName()) || typeField.getTypeName().equals(float.class.getTypeName())
//				|| typeField.getTypeName().equals(Double.class.getTypeName()) || typeField.getTypeName().equals(double.class.getTypeName())
//				|| typeField.getTypeName().equals(Boolean.class.getTypeName()) || typeField.getTypeName().equals(boolean.class.getTypeName())
//				|| typeField.getTypeName().equals(Character.class.getTypeName()) || typeField.getTypeName().equals(char.class.getTypeName())
//				|| typeField.getTypeName().equals(String.class.getTypeName())
//				|| typeField.getTypeName().equals(Date.class.getTypeName()) || typeField.getTypeName().equals(ZonedDateTime.class.getTypeName()))
//			return true;
//		return false;
//	}
//	
//	private boolean isArrayOfPrimitiveType(Class<?> typeField) {
//		if(typeField.getTypeName().equals(Byte[].class.getTypeName()) || typeField.getTypeName().equals(byte[].class.getTypeName())
//				|| typeField.getTypeName().equals(Short[].class.getTypeName()) || typeField.getTypeName().equals(short[].class.getTypeName())
//				|| typeField.getTypeName().equals(Integer[].class.getTypeName()) || typeField.getTypeName().equals(int[].class.getTypeName())
//				|| typeField.getTypeName().equals(Long[].class.getTypeName()) || typeField.getTypeName().equals(long[].class.getTypeName())
//				|| typeField.getTypeName().equals(Float[].class.getTypeName()) || typeField.getTypeName().equals(float[].class.getTypeName())
//				|| typeField.getTypeName().equals(Double[].class.getTypeName()) || typeField.getTypeName().equals(double[].class.getTypeName())
//				|| typeField.getTypeName().equals(Boolean[].class.getTypeName()) || typeField.getTypeName().equals(boolean[].class.getTypeName())
//				|| typeField.getTypeName().equals(Character[].class.getTypeName()) || typeField.getTypeName().equals(char[].class.getTypeName())
//				|| typeField.getTypeName().equals(String[].class.getTypeName())
//				|| typeField.getTypeName().equals(Date[].class.getTypeName()) || typeField.getTypeName().equals(ZonedDateTime[].class.getTypeName()))
//			return true;
//		return false;
//	}
//	
//	private boolean checkPermission(Authentication authentication, Object domainObject, Object permission) {
//		String username = null;
//		String userKeycloakId = null;
//		if (authentication != null &&  authentication instanceof JwtAuthenticationToken) {	
//			JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
//			username = auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME);
//			userKeycloakId = auth.getToken().getClaimAsString(StandardClaimNames.SUB);
//		}
//
//		if(username == null) return false;
//		
//		RedisUserProfile rdsStore = redisUserProfileUtils.getProfileFromCache(userKeycloakId);
//		
//		List<CustomPermission> requiredPermission = resolvePermission(permission);		
//		try {
//			CustomObjectIdentity oid;
//			
//			if (requiredPermission.isEmpty()) 
//				return false;
//			else if (requiredPermission.size() == 1 && requiredPermission.contains(CustomBasePermission.CREATE))
//				oid = this.objectIdentityRetrievalStrategy.createObjectIdentity(0l, ClassUtils.getUserClass(domainObject.getClass()).getName());
//			else
//				oid = this.objectIdentityRetrievalStrategy.getObjectIdentity(domainObject);
//			
//			if(Collections.singletonList(oid).isEmpty()) return false;
//			
//			for (CustomPermission p : requiredPermission) {
//				if (!isGranted(oid, p, username, rdsStore.dataPermissions, domainObject)) {
//					return false;
//				}
//			}
//		}
//		catch (Exception e) {
//			this.logger.error(e.toString());
//			return false;
//		}
//		return true;
//	}
//
////	private boolean isGrantedByProfile(CustomObjectIdentity oid ,List<CustomPermission> permission, String username, List<EntitiesPermissions> recordsPermissions) {
////		this.logger.info("?granting by profile '{}' 'Type:{} Identifier:{}' ==> '{}'", permission, oid.getType(), oid.getIdentifier(), username);
////		
////		recordsPermissions.forEach(p -> this.logger.info("recordsPermissions >> {} {} {} {} {}", p.entityTypename.typename, p.mask ,p.granting ,p.entityId ,p.entityPermissionAssignments.size()));	
////		
////		for (CustomPermission p : permission) {
////			List<EntitiesPermissions> permissions = filterRecordsPermissions(recordsPermissions, (Long) oid.getIdentifier(), true, p.getMask(), oid.getType());//entitiesPermissionsRepository.findByEntityIdAndGrantingAndMaskAndEntityPermissionAssignmentsUsernameAndEntityTypenameTypename((Long) oid.getIdentifier(), true, p.getMask(), username, oid.getType());
////			
////			permissions.forEach(fp -> this.logger.info("filteredPermissions >> {} {} {} {} {}", fp.entityTypename.typename, fp.mask , fp.granting , fp.entityId , fp.entityPermissionAssignments.size()));
////			
////			if(permissions.size() > 0) {
////				this.logger.info("granted by profile '{}' 'Type:{} Identifier:{}' ==> '{}'", p, oid.getType(), oid.getIdentifier(), username);
////			} else {
////				this.logger.info("Not granted by profile '{}' 'Type:{} Identifier:{}' ==> '{}'", p, oid.getType(), oid.getIdentifier(), username);
////				return false;
////			}
////		}
////		
////		return true;
////	}
//	
//	private boolean isGranted(CustomObjectIdentity oid ,CustomPermission permission, String username, List<DataPermissions> dataPermissions, Object object) {
//		this.logger.info("granted????? '{}' 'Type:{} Identifier:{}' ==> '{}'", permission.getCode(), oid.getType(), oid.getIdentifier(), username);
//		
//		if(dataPermissions == null) return false;
//		
//		Class<?> typeClass = ClassUtils.getUserClass(object.getClass());
//		
//		String fieldName = "";
//		try {
//			Field[] fields = typeClass.getFields();
//			for (Field field : fields) {
//				fieldName = field.getName();
//				Class<?> typeField = field.getType();
//				
//				List<DataPermissions> filteredDataPermissions = dataPermissions.stream()
//						.filter(dataPermission -> (dataPermission.mask == permission.getMask() && dataPermission.resources.equals(oid.getType()) && dataPermission.property.equalsIgnoreCase(field.getName())))
//						.toList();				
//				
//				for (DataPermissions dataPermission : filteredDataPermissions) {
//					this.logger.info("Field:{} Value:{} Type:{} Property:{} FilterValue:{}", fieldName, field.get(object), typeField.getTypeName(), dataPermission.property, dataPermission.filterValue);
//					if(isPrimitiveType(typeField)) {
//						if(entryEqualsFilterValue(typeField, dataPermission.filterValue, field.get(object), username, fieldName, permission, field.get(object)))
//							return true;
//					} else if(isArrayOfPrimitiveType(typeField)) {
//						for(Object entry : (Object[]) field.get(object)) {
//							if(entryEqualsFilterValue(entry.getClass(), dataPermission.filterValue, entry, username, fieldName, permission, field.get(object)))
//								return true;
//						}	
//					} else if(typeField.getTypeName().equalsIgnoreCase(List.class.getTypeName()) || typeField.getTypeName().equalsIgnoreCase(ArrayList.class.getTypeName())
//							|| typeField.getTypeName().equalsIgnoreCase(Set.class.getTypeName()) || typeField.getTypeName().equalsIgnoreCase(HashSet.class.getTypeName())
//							|| typeField.getTypeName().equalsIgnoreCase(Collection.class.getTypeName())) {								
//						ParameterizedType pt = (ParameterizedType)object.getClass().getDeclaredField(fieldName).getGenericType();
//				        Class<?> listFieldClass = (Class<?>) pt.getActualTypeArguments()[0];
//				        if(isPrimitiveType(listFieldClass)) {
//				        	@SuppressWarnings("unchecked")
//							Collection<Object> newList = (Collection<Object>) field.get(object);
//							for (Object entry : newList) {
//								if(entryEqualsFilterValue(entry.getClass(), dataPermission.filterValue, entry, username, fieldName, permission, field.get(object)))
//									return true;
//							}	
//				        } else {
//				        	@SuppressWarnings("unchecked")
//							Collection<Object> newList = (Collection<Object>) field.get(object);
//							for (Object entry : newList) {
//								if(isPrimitiveType(entry.getClass())) {
//									if(entryEqualsFilterValue(entry.getClass(), dataPermission.filterValue, entry, username, fieldName, permission, field.get(object)))
//										return true;
//								} else {
//									Object ob = mapper.readValue(dataPermission.filterValue, entry.getClass());
//									if(ob.equals(entry)) {
//										this.logger.info("granted {} '{}' 'Field:{} Value:{}'", username , permission.getCode(), fieldName, field.get(object));
//										return true;
//									}
//								}
//							}
//				        }	
//					} else if(typeField.getTypeName().equalsIgnoreCase(Map.class.getTypeName()) || typeField.getTypeName().equalsIgnoreCase(HashMap.class.getTypeName())) {		
//						@SuppressWarnings("unchecked")
//						Map<Object, Object> newList = (Map<Object, Object>) field.get(object);
//						for (Entry<Object, Object> entry : newList.entrySet()) {
//							@SuppressWarnings("unchecked")
//							Map<Object, Object> ob = mapper.readValue(dataPermission.filterValue, Map.class); 
//							for (Entry<Object, Object> entry1 : ob.entrySet()) {
//							    if(entry1.getKey().equals(entry.getKey()) && entry1.getValue().equals(entry.getValue())) {
//									this.logger.info("granted {} '{}' 'Field:{} Value:{}'", username , permission.getCode(), fieldName, field.get(object));
//									return true;
//								}
//							}
//						}
//					} else {
//						Object ob = mapper.readValue(dataPermission.filterValue, typeField);
//						if(ob.equals(field.get(object))) {
//							this.logger.info("granted {} '{}' 'Field:{} Value:{}'", username , permission.getCode(), fieldName, field.get(object));
//							return true;
//						}
//					}
//				}
//			}								
//		} catch (Exception ex) {
//			this.logger.error("{} object {} : {}", fieldName, object, ex);	
//		}
//		
//		return false;
//	}
//	
//	private boolean entryEqualsFilterValue(Class<?> typeField, String filterValue, Object entry, String username, String fieldName, CustomPermission permission, Object object) throws Exception  {
//		Constructor<?> cons = typeField.getConstructor(String.class);
//	    Object ob =  cons.newInstance(filterValue);
//		if(ob.equals(entry)) {
//			this.logger.info("granted {} '{}' 'Field:{} Value:{}'", username , permission.getCode(), fieldName, object);
//			return true;
//		}
//		
//		return false;
//	}
//
//	private List<CustomPermission> resolvePermission(Object permission) {
//		if (permission instanceof Integer) {
//			return Arrays.asList(this.permissionFactory.buildFromMask((Integer) permission));
//		}
//		if (permission instanceof CustomPermission) {
//			return Arrays.asList((CustomPermission) permission);
//		}
//		if (permission instanceof CustomPermission[]) {
//			return Arrays.asList((CustomPermission[]) permission);
//		}
//		if (permission instanceof String) {
//			String permString = (String) permission;
//			CustomPermission p = buildPermission(permString);
//			if (p != null) {
//				return Arrays.asList(p);
//			}
//		}
//		
//		this.logger.error("Unsupported permission: {}", permission);
//		
//		return new ArrayList<>();
//	}
//
//	private CustomPermission buildPermission(String permString) {
//		try {
//			return this.permissionFactory.buildFromName(permString);
//		}
//		catch (IllegalArgumentException notfound) {
//			return this.permissionFactory.buildFromName(permString.toUpperCase(Locale.ENGLISH));
//		}
//	}
//	
//	public void setObjectIdentityRetrievalStrategy(CustomObjectIdentityRetrievalStrategyImpl objectIdentityRetrievalStrategy) {
//		this.objectIdentityRetrievalStrategy = objectIdentityRetrievalStrategy;
//	}
//	
//	public void setPermissionFactory(CustomPermissionFactory permissionFactory) {
//		this.permissionFactory = permissionFactory;
//	}
//	
//}
