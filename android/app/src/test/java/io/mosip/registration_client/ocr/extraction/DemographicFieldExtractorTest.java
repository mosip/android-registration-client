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

    @Test
    public void extractsPassportFieldsCorrectlyWithoutLeakingDigitsIntoName() {
        String rawOcrText =
                "HTRT TUTRTGY REPUBILIC OF\n"
                + "g ehe / Country Code\n"
                + "IND\n"
                + "R-S. Rakshan'\n"
                + "enta/ Type\n"
                + "UwIS/ Surnane\n"
                + "DOE\n"
                + "n R/Glven Name(s)\n"
                + "JoCELYN MICHELLE\n"
                + "n / Nationality\n"
                + "INDIAN\n"
                + "r/Place of Birth\n"
                + "GUNDUGOLANU\n"
                + "f /Sex\n"
                + "F\n"
                + "Grt wct eu/ Place of tssue\n"
                + "1 HYDERABAD\n"
                + "unt w fefls /Date of issue\n"
                + "11/10/2011\n"
                + "P<DOE<<joCELYN<MICHELLE<<<<\n"
                + "j8369854<4IND5909234F2110101<\n"
                + "INDIA\n"
                + "weie / Passport No.\n"
                + "J8369854\n"
                + "ff / Date of Birth\n"
                + "23/09/1959\n"
                + "wrftr feflu /Date of Expiry\n"
                + "10/10/2021\n"
                + "(<<\n"
                + "<<<<8";

        List<FieldSpec> spec = Arrays.asList(
                new FieldSpec("fullName", "simpleType", "textbox", "name"),
                new FieldSpec("dateOfBirth", "string", "ageDate", "dateOfBirth"),
                new FieldSpec("gender", "simpleType", "dropdown", "gender")
        );

        Map<String, String> result = extractor.extract(rawOcrText, "passport", spec);

        assertEquals("Jocelyn Michelle Doe", result.get("fullName"));
        assertEquals("1959/09/23", result.get("dateOfBirth"));
        assertEquals("F", result.get("gender"));
    }

    @Test
    public void rejectsDigitsInNameEvenWhenGroupedOnSameGeometryRow() {
        String rawOcrText =
                "UwIS/ Surnane\n"
                + "DOE\n"
                + "n R/Glven Name(s)  J8369854\n"
                + "JoCELYN MICHELLE\n"
                + "f /Sex: F\n"
                + "ff / Date of Birth: 23/09/1959";

        List<FieldSpec> spec = Arrays.asList(
                new FieldSpec("fullName", "simpleType", "textbox", "name"),
                new FieldSpec("dateOfBirth", "string", "ageDate", "dateOfBirth"),
                new FieldSpec("gender", "simpleType", "dropdown", "gender")
        );

        Map<String, String> result = extractor.extract(rawOcrText, "passport", spec);

        assertEquals("Jocelyn Michelle Doe", result.get("fullName"));
        assertEquals("1959/09/23", result.get("dateOfBirth"));
        assertEquals("F", result.get("gender"));
    }

    @Test
    public void extractsTabularFormWithSpaceSeparatedColumnsAndParentheticalLabels() {
        String rawOcrText =
                "A1\n"
                + "1  Field  Value\n"
                + "2  Full Name  Aisha Rahman\n"
                + "3  Date of Birth (DOB)  16-05-2009\n"
                + "4  Gender  Female\n"
                + "5  Address  12 Green Park Road, Salt Lake City, Kolkata - 700091, West Bengal, India\n"
                + "6  Post  Student\n"
                + "7  Phone  98765 43210\n"
                + "8  Email  aisha.rahman09@example.com";

        List<FieldSpec> spec = Arrays.asList(
                new FieldSpec("fullName", "simpleType", "textbox", "name"),
                new FieldSpec("dateOfBirth", "string", "ageDate", "dateOfBirth"),
                new FieldSpec("gender", "simpleType", "dropdown", "gender"),
                new FieldSpec("addressLine1", "simpleType", "textbox", "address"),
                new FieldSpec("postalCode", "string", "textbox", "postalCode"),
                new FieldSpec("phone", "string", "textbox", "phone"),
                new FieldSpec("email", "string", "textbox", "email")
        );

        Map<String, String> result = extractor.extract(rawOcrText, "UNKNOWN", spec);

        assertEquals("Aisha Rahman", result.get("fullName"));
        assertEquals("2009/05/16", result.get("dateOfBirth"));
        assertEquals("Female", result.get("gender"));
        assertEquals("12 Green Park Road, Salt Lake City, Kolkata - 700091, West Bengal, India", result.get("addressLine1"));
        assertEquals("700091", result.get("postalCode"));
        assertEquals("9876543210", result.get("phone"));
        assertEquals("aisha.rahman09@example.com", result.get("email"));
    }

    @Test
    public void phoneRegexFallbackDoesNotGrabDateString() {
        String rawOcrText =
                "Application Date: 16-05-2009\n"
                + "Emergency Line: 98765 43210";

        List<FieldSpec> spec = Arrays.asList(
                new FieldSpec("phone", "string", "textbox", "phone")
        );

        Map<String, String> result = extractor.extract(rawOcrText, "UNKNOWN", spec);

        assertEquals("9876543210", result.get("phone"));
    }

    private static FieldSpec field(String id) {
        return new FieldSpec(id, "string", "textbox", id);
    }
}
