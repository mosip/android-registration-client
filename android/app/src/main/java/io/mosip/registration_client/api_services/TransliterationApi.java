package io.mosip.registration_client.api_services;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import io.mosip.registration.transliterationmanager.spi.TransliterationService;
import io.mosip.registration_client.model.TransliterationPigeon;


@Singleton
public class TransliterationApi implements TransliterationPigeon.TransliterationApi{

    TransliterationService transliterationService;

    @Inject
    public TransliterationApi(TransliterationService transliterationService) {
        this.transliterationService = transliterationService;
    }

    @Override
    public void transliterate(@NonNull TransliterationPigeon.TransliterationOptions options, @NonNull TransliterationPigeon.Result<String> result) {
        String input = options.getInput();
        String inputLang = options.getSourceLanguage();
        String outputLang = options.getTargetLanguage();

        String transliteratedResult = transliterationService.transliterate(inputLang,outputLang,input);
        result.success(transliteratedResult);
    }
}

