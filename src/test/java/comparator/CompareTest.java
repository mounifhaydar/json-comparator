package comparator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.springframework.util.comparator.Comparators;

import com.comparator.model.JsonDiff;
import com.comparator.utils.CompareUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class CompareTest {

	//@Test
	public void testCleanNode() {
		//\"Unable to Load Conditions, Error Ocured:External ID not found : \\\"250.0\\\".\"
		JsonNode n = JsonNodeFactory.instance.textNode("\"Unable to Load Conditions, Error Ocured:External ID not found : \\\"J09.X2\\\".\"");
		String ret = null;//CompareUtils.cleanNode(n);
		//ret = ret.substring(0, ret.indexOf("Unable to Load Conditions") > -1 ? ret.indexOf("Unable to Load Conditions") : 0);
		System.out.println(ret);
	}

	
	//@Test
	public void testRegEX() {
		String reg1 = "([A-Za-z0-9_]+)/([0-9]+),";
		String reg2 = "([0-9]+)/([0-9]+)\\)";

		String var1 = "e EA0002965877/11212121212121,0219-142903-0852/Cefix";
		String var2 = "EA0002965877/11212121212121)0219-142903-0852/Cefix";

		System.out.println(var1.replaceAll(reg1, "Y/X,"));
		System.out.println(var2.replaceAll(reg2, "Y/X,"));

	}

	//@Test
	public void testOrder() {
		List<JsonDiff> l1 = new ArrayList<JsonDiff>();

		l1.add(new JsonDiff(new StringBuilder(""), 7, 2));
		l1.add(new JsonDiff(new StringBuilder(""), 6, 1));

		//l1.sort(c);
		//Collections.sort((List<T>) l1);
		//	Collections.SO
		//l1.sort(Comparator.comparingInt(JsonDiff::getEqualCounter).reversed());
		//Collections.sort(l1);
		l1.forEach(System.out::println);
	}

	//@Test
	public void testEncoding() throws UnsupportedEncodingException {
		String s = "\u0027";
		String s2 = "’";
		String s3 = "\u2019";

		System.out.println(s);
		System.out.println(s.equals(s2));

		System.out.println(s2);
		byte[] xxx = s2.getBytes("UTF-8");

		for (byte x : xxx) {
			System.out.println(Integer.toHexString(x));
		}

		System.out.println(s3);
		System.out.println(s3.equals(s2));

	}

	//@Test
	public void testAllowedDiff() throws UnsupportedEncodingException {
		System.out.println(Math.pow(10, -0.01));
	}

	//@Test
	public void testDoubleTimes() {
		double r = 16.65d * 30d / 100d;
		System.out.println(r);
	}

	

	//@Test
	public void mapEA() {
		Map<String, String> dictionaryExpectedActual = new HashMap<>();
		dictionaryExpectedActual.put("Aa", "bb");
		dictionaryExpectedActual.put("Aa", "cc");
		dictionaryExpectedActual.put("Af", null);
		System.out.println(dictionaryExpectedActual.get("Aa"));
		System.out.println(dictionaryExpectedActual.get("Aa2"));
		System.out.println("-------------------------------------------");
		for (Entry<String, String> d : dictionaryExpectedActual.entrySet()) {
			if(d.getKey().toLowerCase().equals("Aa".toLowerCase())) {
				d.setValue("yes");
			}
		}
		System.out.println(dictionaryExpectedActual.get("Aa"));
	}
	
	@Test
	public void sub() {
		System.out.println("1".substring(0, 1));
		System.out.println("231.2".toString());
	}
	
}
