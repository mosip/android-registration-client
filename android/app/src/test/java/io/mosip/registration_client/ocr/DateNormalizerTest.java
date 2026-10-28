package io.mosip.registration_client.ocr;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import io.mosip.registration_client.ocr.extraction.DateNormalizer;

public class DateNormalizerTest {

    @Test
    public void testValidDateFormats() {
        assertEquals("2023/05/12", DateNormalizer.toAgeDateFormat("12/05/2023"));
        assertEquals("2023/05/12", DateNormalizer.toAgeDateFormat("12-05-2023"));
        assertEquals("2023/05/12", DateNormalizer.toAgeDateFormat("12.05.2023"));
        assertEquals("2023/05/12", DateNormalizer.toAgeDateFormat("2023-05-12"));
        assertEquals("2023/05/12", DateNormalizer.toAgeDateFormat("2023/05/12"));
        assertEquals("2023/05/12", DateNormalizer.toAgeDateFormat("20230512"));
    }

    @Test
    public void testSingleDigitDayAndMonth() {
        assertEquals("2023/02/01", DateNormalizer.toAgeDateFormat("1/2/2023"));
        assertEquals("2023/02/01", DateNormalizer.toAgeDateFormat("1-2-2023"));
    }

    @Test
    public void testStrictCalendarValidation_RejectsImpossibleDates() {
        assertNull("February 31 must be rejected", DateNormalizer.toAgeDateFormat("31/02/2023"));
        assertNull("April 31 must be rejected", DateNormalizer.toAgeDateFormat("31/04/2023"));
        assertNull("February 29 in non-leap year must be rejected", DateNormalizer.toAgeDateFormat("29/02/2023"));
        assertNull("February 29 in non-leap year (yyyy-MM-dd) must be rejected", DateNormalizer.toAgeDateFormat("2023-02-29"));
        assertNull("February 31 in yyyyMMdd must be rejected", DateNormalizer.toAgeDateFormat("20230231"));
    }

    @Test
    public void testStrictCalendarValidation_AcceptsLeapYear() {
        assertEquals("2024/02/29", DateNormalizer.toAgeDateFormat("29/02/2024"));
        assertEquals("2024/02/29", DateNormalizer.toAgeDateFormat("2024-02-29"));
        assertEquals("2024/02/29", DateNormalizer.toAgeDateFormat("20240229"));
    }

    @Test
    public void testAmbiguousTwoDigitYears_Rejected() {
        assertNull("Ambiguous 2-digit year 23 must be rejected", DateNormalizer.toAgeDateFormat("12/05/23"));
        assertNull("Ambiguous 2-digit year 85 must be rejected", DateNormalizer.toAgeDateFormat("12/05/85"));
        assertNull("Ambiguous 2-digit year 99 must be rejected", DateNormalizer.toAgeDateFormat("01-01-99"));
    }

    @Test
    public void testOcrSubstitutions() {
        assertEquals("2023/05/12", DateNormalizer.toAgeDateFormat("12/O5/2023"));
        assertEquals("2023/05/12", DateNormalizer.toAgeDateFormat("12/05/2O23"));
        assertEquals("2023/05/12", DateNormalizer.toAgeDateFormat("I2/05/2023"));
    }

    @Test
    public void testMonthNameParsing() {
        Set<String> months = new HashSet<>();
        months.add("jan");
        months.add("january");
        months.add("feb");
        months.add("february");
        months.add("may");

        assertEquals("1990/01/15", DateNormalizer.toAgeDateFormat("15 Jan 1990", months));
        assertEquals("1990/01/15", DateNormalizer.toAgeDateFormat("15-January-1990", months));
        assertEquals("1985/05/20", DateNormalizer.toAgeDateFormat("20 May 1985", months));

        assertNull("31 Feb 1990 must be rejected", DateNormalizer.toAgeDateFormat("31 Feb 1990", months));
        assertNull("29 Feb 2023 must be rejected", DateNormalizer.toAgeDateFormat("29 Feb 2023", months));
    }
}
