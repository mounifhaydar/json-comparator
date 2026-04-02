package com.comparator.service.impl;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.devkit.service.ITokenUtilService;
import com.comparator.service.ITokenService;
import com.nimbusds.jose.JOSEException;

/**
 * @author Mounif.Haydar
 *
 */
//@Service
public class TokenService implements ITokenService {

//	@Autowired
//	private ITokenUtilService tokenUtilService;

	@Override
	public Long findUserId(String token) throws ParseException, JOSEException {
		return null;//tokenUtilService.findUserId(token);
	}

}
