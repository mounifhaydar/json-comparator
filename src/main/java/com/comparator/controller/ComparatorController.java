package com.comparator.controller;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.comparator.model.Compare;
import com.comparator.service.IComparatorService;
import com.fasterxml.jackson.core.JsonParseException;

@RestController
@RequestMapping("/comparator")
public class ComparatorController {
	@Autowired
	private IComparatorService comparatorService;

	@RequestMapping(value = "/compare", method = RequestMethod.POST)
	public ResponseEntity<String> runCompare(@RequestBody Compare compare) {
		String diff = "";

		try {
			diff = comparatorService.compareJson(compare.getActual(), compare.getExpected());
		} catch (JsonParseException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (ParseException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>(diff, HttpStatus.OK);
	}
}
