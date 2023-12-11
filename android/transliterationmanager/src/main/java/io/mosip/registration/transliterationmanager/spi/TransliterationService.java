package io.mosip.registration.transliterationmanager.spi;

public interface TransliterationService {
    String transliterate(String inputCode,String outputCode, String input);
}
