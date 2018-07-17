package com.comparator.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import org.threeten.extra.Days;

public class DateUtils {

	public static final String	DATE_FORMAT_DEFAULT			= "dd-MM-yyyy";
	public static final String	DATE_TIME_FORMAT_DEFAULT	= "dd-MM-yyyy HH:mm:ss";

	private DateUtils() {
	}

	public static LocalDate addMonths(LocalDate localDate, int numberOfMonths) {
		return localDate.plusMonths(numberOfMonths);
	}

	public static LocalDate lastDayOfMonth(LocalDate localDate) {
		return localDate.with(TemporalAdjusters.lastDayOfMonth());
	}

	public static LocalDate firstDayOfMonth(LocalDate localDate) {
		return localDate.with(TemporalAdjusters.firstDayOfMonth());
	}

	public static LocalDate min(LocalDate... dates) {
		LocalDate min = null;
		for (LocalDate localDate : dates) {
			if (min == null || localDate.isBefore(min)) {
				min = localDate;
			}
		}
		return min;
	}

	public static long monthsBetween(LocalDate firstDate, LocalDate secondDate) {
		return ChronoUnit.MONTHS.between(firstDate, secondDate);
	}

	public static long approximateMonthsBetween(LocalDate firstDate, LocalDate secondDate) {
		double days = Days.between(firstDate, secondDate).getAmount();
		double result = (double) days / 30;
		return (long) Math.ceil(result);
	}

	public static boolean isGreaterOrEqulas(LocalDate firstDate, LocalDate secondDate) {
		return firstDate.isAfter(secondDate) || firstDate.isEqual(secondDate);
	}

	public static boolean isLessOrEqulas(LocalDate firstDate, LocalDate secondDate) {
		return firstDate.isBefore(secondDate) || firstDate.isEqual(secondDate);
	}

}
