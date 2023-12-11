package io.mosip.registration.transliterationmanager.spi;

public interface TransliterationService {
    String transliterate(String code, String input);
}
