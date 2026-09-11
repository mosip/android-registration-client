package io.mosip.registration_client.ocr.extraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.mosip.registration_client.ocr.models.FieldSpec;

/** Regression tests for flat-text parsing; geometry uses the same core parser. */
public class DemographicFieldExtractorTest {

    private DemographicFieldExtractor extractor;

    @Before
    public void setUp() {
        extractor = new DemographicFieldExtractor(new ExtractionConfig());
    }

    @Test
    public void extractsIndependentPairsWhenSeveralFieldsShareOneOcrRow() {
        Map<String, String> result = extractor.extract(
                "Father Name: Ramesh Kumar  Name: Asha Devi  Gender: Female\n"
                        + "Date of Birth: 03-01-1990 Email: ASHA@EXAMPLE.COM\n"
                        + "Mobile: +91 98765 43210 PIN: 560001",
                "UNKNOWN",
                Arrays.asList(
                        field("firstName"), field("fatherName"), field("gender"),
                        field("dateOfBirth"), field("email"), field("phone"), field("postalCode")));

        assertEquals("Asha Devi", result.get("firstName"));
        assertEquals("Ramesh Kumar", result.get("fatherName"));
        assertEquals("Female", result.get("gender"));
        assertEquals("1990/01/03", result.get("dateOfBirth"));
        assertEquals("asha@example.com", result.get("email"));
        assertEquals("+919876543210", result.get("phone"));
        assertEquals("560001", result.get("postalCode"));
    }

    @Test
    public void doesNotTreatIdAsPartOfAnUnrelatedWord() {
        Map<String, String> result = extractor.extract(
                "Identity Card: National identity document\nID: AB-12345",
                "UNKNOWN",
                Arrays.asList(field("documentNumber")));

        assertEquals("AB-12345", result.get("documentNumber"));
        assertFalse(result.get("documentNumber").contains("National"));
    }

    @Test
    public void resolvesConfigurationKeysRegardlessOfCaseOrSeparators() {
        Map<String, String> result = extractor.extract(
                "Date Of Birth: 1 Jan 2000",
                "UNKNOWN",
                Arrays.asList(new FieldSpec("date_of_birth", "string", "textbox", "DATE_OF_BIRTH")));

        assertEquals("2000/01/01", result.get("date_of_birth"));
    }

    @Test
    public void extractsPanCardPreciseFieldsWithoutLeakingOrHallucinating() {
        String rawOcrText =
                "INCOME TAX DEPARTMENT\n"
                + "T4 Name\n"
                + "RAHUL MISHRA\n"
                + "Permanent Account Number Card\n"
                + "ELWPM8089J\n"
                + "fyar T4| Father's Name\n"
                + "SATENDRA MISHRA\n"
                + "GOVT. OF INDIA\n"
                + "H 5 arta/ Date of Birth\n"
                + "30/01/1997\n"
                + "BEHTAT/ Signature\n"
                + "17022018";

        List<FieldSpec> spec = Arrays.asList(
                new FieldSpec("fullName", "simpleType", "textbox", "name"),
                new FieldSpec("dateOfBirth", "string", "ageDate", "dateOfBirth"),
                new FieldSpec("gender", "simpleType", "dropdown", "gender"),
                new FieldSpec("addressLine1", "simpleType", "textbox", "address"),
                new FieldSpec("postalCode", "string", "textbox", "postalCode"),
                new FieldSpec("phone", "string", "textbox", "phone"),
                new FieldSpec("email", "string", "textbox", "email"),
                new FieldSpec("introducerName", "simpleType", "textbox", "fatherName"),
                new FieldSpec("documentNumber", "string", "textbox", "documentNumber")
        );

        Map<String, String> result = extractor.extract(rawOcrText, "UNKNOWN", spec);

        assertEquals("Rahul Mishra", result.get("fullName"));
        assertEquals("Satendra Mishra", result.get("introducerName"));
        assertEquals("1997/01/30", result.get("dateOfBirth"));
        assertEquals("ELWPM8089J", result.get("documentNumber"));
        assertNull(result.get("postalCode"));
        assertNull(result.get("addressLine1"));
        assertNull(result.get("gender"));
    }

    private static FieldSpec field(String id) {
        return new FieldSpec(id, "string", "textbox", id);
    }
}
