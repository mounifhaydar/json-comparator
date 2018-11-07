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
	public void testCompareStr() {
		String start = "of Current Claim and Drug(s) (";
		String end = ") are members of the same class description and may represent duplicate therapy";
		String e = "\"Drug (1472-4258-011/Triaxone 1G I.V. (Ceftriaxone) Powder For Injection 1 Vial (Dry) + 1 Solvent Ampoule (10Ml)) of Current Claim and Drug(s) ("
				+ "1631-2950-003/Megion 1G (Ceftriaxone) Powder For Injection 1 Glass Vial (Powder) + 1 Plastic Ampoule (10Ml Solvent) of Claim # ECLAIM9299971/1,"
				+ "1472-4258-011/Triaxone 1G I.V. (Ceftriaxone) Powder For Injection 1 Vial (Dry) + 1 Solvent Ampoule (10Ml) of Claim # EA0002988728/1"
				+ ") are members of the same class description and may represent duplicate therapy.\"";
		String a = "\"Drug (1472-4258-011/Triaxone 1G I.V. (Ceftriaxone) Powder For Injection 1 Vial (Dry) + 1 Solvent Ampoule (10Ml)) of Current Claim and Drug(s) ("
				+ "1472-4258-011/Triaxone 1G I.V. (Ceftriaxone) Powder For Injection 1 Vial (Dry) + 1 Solvent Ampoule (10Ml) of Claim # EA0002988728/1,"
				+ "1631-2950-003/Megion 1G (Ceftriaxone) Powder For Injection 1 Glass Vial (Powder) + 1 Plastic Ampoule (10Ml Solvent) of Claim # ECLAIM9299971/1"
				+ ") are members of the same class description and may represent duplicate therapy.\"";

		e = "";
		a = "a";
		//e = e.substring(e.indexOf(start) + start.length(), e.lastIndexOf(end));
		//a = a.substring(a.indexOf(start) + start.length(), a.lastIndexOf(end));

		System.out.println(e);
		System.out.println(a);
		String[] ar1 = e.split(",");
		Arrays.sort(ar1);
		String[] ar2 = a.split(",");
		Arrays.sort(ar2);

		System.out.println(Arrays.equals(ar1, ar2));
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
	public void writeAsStr() {
		String f = "{\"root\":{\"invoices\":[{\"invoices.Item[invoiceSeqid[0]]\":{\"serviceItems\":[{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":145,\"benefitItemId\":944,\"benefitId\":5,\"itemCode\":\"82306\",\"rownbr\":1,\"startDate\":null,\"activityId\":\"1\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":472,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":472,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":472,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":472,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":472,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":472}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":634,\"benefitItemId\":370,\"benefitId\":5,\"itemCode\":\"85651\",\"rownbr\":2,\"startDate\":null,\"activityId\":\"10\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":40,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":40,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":40,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":40,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":40,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":40}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":658,\"benefitItemId\":459,\"benefitId\":5,\"itemCode\":\"86140\",\"rownbr\":3,\"startDate\":null,\"activityId\":\"11\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":96,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":96,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":96,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":96,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":96,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":96}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":506,\"benefitItemId\":219,\"benefitId\":5,\"itemCode\":\"84460\",\"rownbr\":4,\"startDate\":null,\"activityId\":\"12\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":56,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":56,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":56,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":56,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":56,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":3,\"pendingReason\":null,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":56}]\":{\"status\":{\"actual\":\"2\",\"expected\":\"3\"},\"pendingReason\":{\"actual\":\"196\",\"expected\":\"null\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":7,\"benefitItemId\":771,\"benefitId\":5,\"itemCode\":\"80061\",\"rownbr\":5,\"startDate\":null,\"activityId\":\"13\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":170,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":170,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":170,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":170,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":170,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":170}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":264,\"benefitItemId\":141,\"benefitId\":5,\"itemCode\":\"82947\",\"rownbr\":6,\"startDate\":null,\"activityId\":\"2\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":64,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":64,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":64,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":64,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":64,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":64}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":505,\"benefitItemId\":218,\"benefitId\":5,\"itemCode\":\"84450\",\"rownbr\":7,\"startDate\":null,\"activityId\":\"3\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":56,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":56,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":56,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":56,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":56,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":56}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":523,\"benefitItemId\":229,\"benefitId\":5,\"itemCode\":\"84550\",\"rownbr\":8,\"startDate\":null,\"activityId\":\"4\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":60,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":60,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":60,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":60,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":60,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":60}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":276,\"benefitItemId\":153,\"benefitId\":5,\"itemCode\":\"82977\",\"rownbr\":9,\"startDate\":null,\"activityId\":\"5\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":56,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":56,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":56,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":56,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":56,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":56}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":499,\"benefitItemId\":214,\"benefitId\":5,\"itemCode\":\"84439\",\"rownbr\":10,\"startDate\":null,\"activityId\":\"6\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":160,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":160,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":160,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":160,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":160,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":160}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":501,\"benefitItemId\":216,\"benefitId\":5,\"itemCode\":\"84443\",\"rownbr\":11,\"startDate\":null,\"activityId\":\"7\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":192,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":192,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":192,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":192,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":192,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":192}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":511,\"benefitItemId\":223,\"benefitId\":5,\"itemCode\":\"84481\",\"rownbr\":12,\"startDate\":null,\"activityId\":\"8\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":160,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":160,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":160,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":160,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":160,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":160}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}},{\"serviceItems.Item[{\"serviceId\":\"5\",\"specific\":\"NO\",\"standardId\":5,\"stdItemId\":552,\"benefitItemId\":288,\"benefitId\":5,\"itemCode\":\"85025\",\"rownbr\":13,\"startDate\":null,\"activityId\":\"9\",\"activityNet\":0,\"activityType\":null,\"professionalCountryId\":45,\"professionalId\":63038,\"claimedQuantity\":1,\"claimedAmount\":80,\"drugPot\":0,\"drugDd\":0,\"unitPerFrequency\":0,\"frequencyValue\":0,\"frequencyTypeId\":0,\"frequencyType\":\"\",\"toothNumber\":\"\",\"toothArea\":null,\"prescription\":{\"referenceNumber\":\"\",\"activityId\":\"\"},\"eppShare\":0,\"spQuantityApproved\":1,\"spTotalApproved\":80,\"adjustmentReasonId\":0,\"memberLimitShare\":0,\"memberCopartShare\":0,\"memberExcessShare\":0,\"memberShare\":0,\"memberLimitShareItem\":0,\"memberCopartShareItem\":0,\"memberExcessShareItem\":0,\"memberShareItem\":0,\"calculatedMemberShare\":0,\"granularBilling\":0,\"granularQuantityClaimed\":0,\"unitPriceClaimed\":80,\"granularQuantityApproved\":0,\"spUnitPriceApproved\":80,\"declined\":\"NO\",\"discount\":{\"discount\":0,\"otherDiscount\":0,\"earlyPaymentDiscount\":0},\"ppQuantityApproved\":0,\"ppUnitPriceApproved\":null,\"ppTotalApproved\":null,\"ppShare\":0,\"ppShareInPpCurr\":null,\"ppToBeRecovered\":null,\"ppToBeRecoveredInPpCurr\":null,\"paidThenRecovered\":0,\"percentageRecovery\":null,\"priorApproval\":0,\"serviceTax\":0,\"payerShareTax\":0,\"taxPercentage\":0,\"penalty\":0,\"adjustment\":0,\"dmpPayerShare\":0,\"providerShare\":80,\"professionalShare\":0,\"businessConsideration\":0,\"documentSubmissionId\":null,\"status\":2,\"pendingReason\":197,\"remainingDays\":null,\"approvalItemId\":null,\"payerShare\":80}]\":{\"pendingReason\":{\"actual\":\"198\",\"expected\":\"197\"}}}],\"claimValidations\":{\"actual\":\"Array size: 1\",\"expected\":\"Array is empty\"}}}]}}\"";
		System.out.println(f);
		try {
			f = CompareUtils.writeJsonField(f);
			System.out.println(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
