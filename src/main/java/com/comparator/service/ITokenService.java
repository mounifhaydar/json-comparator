package com.comparator.service;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;

/**
 * @author Mounif.Haydar
 *
 */
public interface ITokenService {

	Long findUserId(String token) throws ParseException, JOSEException;
}
