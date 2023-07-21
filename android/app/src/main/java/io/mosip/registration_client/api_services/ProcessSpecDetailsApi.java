package io.mosip.registration_client.api_services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration_client.model.ProcessSpecPigeon;

@Singleton
public class ProcessSpecDetailsApi implements ProcessSpecPigeon.ProcessSpecApi {

    Context context;
    IdentitySchemaRepository identitySchemaRepository;
    GlobalParamRepository globalParamRepository;

    RegistrationService registrationService;

    @Inject
    public ProcessSpecDetailsApi(Context context,
                                 IdentitySchemaRepository identitySchemaRepository,
                                 GlobalParamRepository globalParamRepository,
                                 RegistrationService registrationService) {

        this.context = context;
        this.identitySchemaRepository = identitySchemaRepository;
        this.globalParamRepository = globalParamRepository;
        this.registrationService=registrationService;
    }

    @Override
    public void getUISchema(@NonNull ProcessSpecPigeon.Result<String> result) {
        try{
            String schemaJson = identitySchemaRepository.getSchemaJson(context,
                    identitySchemaRepository.getLatestSchemaVersion());
            result.success(schemaJson);
            return;
        }catch (Exception e){
            Log.e(getClass().getSimpleName(), "Error in getUISchema", e);
        }
        result.success("");
    }

    @Override
    public void getStringValueGlobalParam(@NonNull String key, @NonNull ProcessSpecPigeon.Result<String> result) {
        try{
            String cachedString = globalParamRepository.getCachedStringGlobalParam(key);
            result.success(cachedString);
            return;
        }catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getStringValueGlobalParam", e);
        }
        result.success("");
    }

    @Override
    public void getNewProcessSpec(@NonNull ProcessSpecPigeon.Result<List<String>> result) {
        ObjectWriter ow;
        List<String> processSpecList=new ArrayList<>();
        try{
            ProcessSpecDto processSpecDto = identitySchemaRepository.getNewProcessSpec(context,
                    identitySchemaRepository.getLatestSchemaVersion());
            ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            if(processSpecDto != null) {
                String json = ow.writeValueAsString(processSpecDto);
                processSpecList.add(json);
            }
        }catch (Exception e){
            Log.e(getClass().getSimpleName(), "Error in getNewProcessSpec", e);
        }
        result.success(processSpecList);
    }
}
