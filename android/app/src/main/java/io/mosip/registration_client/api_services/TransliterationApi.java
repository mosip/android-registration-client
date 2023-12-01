package io.mosip.registration_client.api_services;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

import com.ibm.icu.text.Transliterator;

import io.mosip.registration_client.model.TransliterationPigeon;


@Singleton
public class TransliterationApi implements TransliterationPigeon.TransliterationApi{

    @Override
    public void transliterate(@NonNull TransliterationPigeon.TransliterationOptions options, @NonNull TransliterationPigeon.Result<TransliterationPigeon.TransliterationResult> result) {
        String input = options.getInput();
        String inputLang = options.getSourceLanguage();
        String outputLang = options.getTargetLanguage();

        Transliterator transliterator = Transliterator.getInstance(inputLang+"-"+outputLang);
        String transliteratedResult = transliterator.transliterate(input);

        TransliterationPigeon.TransliterationResult response = new TransliterationPigeon.TransliterationResult();
        response.setOutput(transliteratedResult);
        result.success(response);
    }
}

