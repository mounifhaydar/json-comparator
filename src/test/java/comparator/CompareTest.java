package comparator;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Optional;

import org.junit.Test;

import com.comparator.model.JsonDiff;
import com.comparator.model.Regex;
import com.comparator.utils.CompareUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;

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

		l1.add(new JsonDiff(null, 7, 2));
		l1.add(new JsonDiff(null, 6, 1));

		//l1.sort(c);
		//Collections.sort((List<T>) l1);
		//	Collections.SO
		//l1.sort(Comparator.comparingInt(JsonDiff::getEqualCounter).reversed());
		//Collections.sort(l1);
		l1.forEach(System.out::println);
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
			if (d.getKey().toLowerCase().equals("Aa".toLowerCase())) {
				d.setValue("yes");
			}
		}
		System.out.println(dictionaryExpectedActual.get("Aa"));
	}

	//@Test
	public void sub() {
		System.out.println("1".substring(0, 1));
		System.out.println("231.2".toString());

	}

	@Test
	public void replace() {
		String input1 = ".0 \"Billing:\\rDirect Billing\\rReimbursement: Elective(UCR 2 with penalty 100.0%) Emergency(UCR 2 with penalty 100.0%) \\rReimbursement customize for: \\r - Oman: Elective(UCR 2 with penalty 0.0%) Emergency(UCR 2 with penalty 0.0%)\"";
		String input2 = "0 \"Billing:\\rDirect Billing\\rReimbursement: Elective(UCR 2 with penalty 100%) Emergency(UCR 2 with penalty 100%) \\rReimbursement customize for: \\r - Oman: Elective(UCR 2 with penalty 0%) Emergency(UCR 2 with penalty 0%) \"";
		
	
		

		
		//String[] constraintsDirtyClean = { "may represent a duplication in therapy", "may represent duplicate therapy", "?", "'", "\u2019", "&quot;", "&apos;", "null", ".0%)", "%)", ".0 ", " 0 ", "#", " ", "." };
		String[] constraintsDirtyClean = { "may represent a duplication in therapy", "may represent duplicate therapy", "?", "'", "\u2019", "&quot;", "&apos;",
				"null"/* , ".0%)", "%)" , ".0 " , " 0 " */, /* ". ", */ "#", " ", "." };

		Map<String, String> constraintsTOB2ConditionDescRegex = Stream.of(new String[][] { { "\\,(\\d+)", "$1" /*
			 * "750,000"
			 * =>
			 * "750000"
			 */ }, { "(\\d+)\\. ", "$1" }, /*
											 * "1,000,000. "
											 * =>
											 * "1,000,000"
											 */
{ "(^|\\D+)(\\.0+)(\\D+|$)", "$10$3" }/* replace .0 by 0 */, { "(^|\\d)(\\.0*)(\\D+|$)", "$1$3" }/*
				 * remove
				 * trailing
				 * zeros
				 * after
				 * dot:
				 * 12
				 * .
				 * 00
				 */, { "(\\.\\d*?[1-9])(0+)", "$1" }, /*
														 * remove
														 * trailing
														 * zeros
														 * after
														 * number
														 * 12
														 * .
														 * 0020
														 * =>
														 * 12
														 * .
														 * 002
														 */
{ "(^|\\D+)(\\.\\d+)", "$10$2" },/* replace DOT by 0.d */
}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
		

		Regex reg = new Regex(constraintsTOB2ConditionDescRegex);

		

		String[] cleanNode = CompareUtils.cleanNode((new TextNode(input1)).asText(), true, constraintsDirtyClean, constraintsTOB2ConditionDescRegex, null);
		Optional<String> reduce = Arrays.asList(cleanNode).stream().reduce((a, b) -> {
			return a + b;
		});
		System.out.println(reduce.get());

		cleanNode = CompareUtils.cleanNode((new TextNode(input2)).asText(), true, constraintsDirtyClean, constraintsTOB2ConditionDescRegex, null);
		reduce = Arrays.asList(cleanNode).stream().reduce((a, b) -> {
			return a + b;
		});
		System.out.println(reduce.get());

		System.out.println(CompareUtils.isEqual("", new TextNode(input1), new TextNode(input2), false, false, 6, true, constraintsDirtyClean, constraintsTOB2ConditionDescRegex, null,false));
	}
}
