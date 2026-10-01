package io.mosip.registration.transliterationmanager.service;

import com.ibm.icu.text.Transliterator;

import io.mosip.registration.transliterationmanager.spi.TransliterationService;

public class TransliterationServiceImpl implements TransliterationService {
    @Override
    public String transliterate(String inputCode, String outputCode, String input) {
        if (input == null || input.isEmpty() || inputCode == null || outputCode == null || inputCode.equalsIgnoreCase(outputCode)) {
            return input;
        }
        try {
            Transliterator transliterator = Transliterator.getInstance(inputCode + "-" + outputCode);
            return transliterator.transliterate(input);
        } catch (Exception e) {
            return input;
        }
    }
}
