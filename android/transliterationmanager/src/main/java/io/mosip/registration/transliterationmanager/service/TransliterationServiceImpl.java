package io.mosip.registration.transliterationmanager.service;

import android.icu.text.Transliterator;

import io.mosip.registration.transliterationmanager.spi.TransliterationService;

public class TransliterationServiceImpl implements TransliterationService {
    @Override
    public String transliterate(String code, String input) {
        Transliterator transliterator = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            transliterator = Transliterator.getInstance(code);

        }
        String transliteratedResult = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            transliteratedResult = transliterator.transliterate(input);
        }
        return transliteratedResult;
    }
}
