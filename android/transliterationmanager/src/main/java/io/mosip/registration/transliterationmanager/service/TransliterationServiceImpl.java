package io.mosip.registration.transliterationmanager.service;

import com.ibm.icu.text.Transliterator;

import io.mosip.registration.transliterationmanager.spi.TransliterationService;

public class TransliterationServiceImpl implements TransliterationService {
    @Override
    public String transliterate(String inputCode,String outputCode, String input) {
        if(inputCode == outputCode){
            return input;
        }
        Transliterator transliterator = Transliterator.getInstance(inputCode+"-"+outputCode);
        String transliteratedResult = transliterator.transliterate(input);
        if(input.equalsIgnoreCase(transliteratedResult)){
            return "";
        }
        return transliteratedResult;
    }
}
