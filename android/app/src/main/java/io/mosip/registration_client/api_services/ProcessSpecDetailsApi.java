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
import io.mosip.registration_client.model.ProcessSpecPigeon;

@Singleton
public class ProcessSpecDetailsApi implements ProcessSpecPigeon.ProcessSpecApi {

    Context context;
    IdentitySchemaRepository identitySchemaRepository;
    GlobalParamRepository globalParamRepository;

    @Inject
    public ProcessSpecDetailsApi(Context context,
                                 IdentitySchemaRepository identitySchemaRepository,
                                 GlobalParamRepository globalParamRepository) {

        this.context = context;
        this.identitySchemaRepository = identitySchemaRepository;
        this.globalParamRepository = globalParamRepository;
    }

    @Override
    public void getUISchema(@NonNull ProcessSpecPigeon.Result<String> result) {
        try{
            String schemaJson = identitySchemaRepository.getSchemaJson(context,
                    identitySchemaRepository.getLatestSchemaVersion());
            result.success(schemaJson);
        }catch (Exception e){
            Log.e(getClass().getSimpleName(), "Error in getUISchema", e);
        }
    }

    @Override
    public void getStringValueGlobalParam(@NonNull String key, @NonNull ProcessSpecPigeon.Result<String> result) {
        try{
            String cachedString = globalParamRepository.getCachedStringGlobalParam(key);
            result.success(cachedString);
        }catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error in getStringValueGlobalParam", e);
        }
    }

    @Override
    public void getNewProcessSpec(@NonNull ProcessSpecPigeon.Result<List<String>> result) {
        ObjectWriter ow;
        try{
            ProcessSpecDto processSpecDto = identitySchemaRepository.getNewProcessSpec(context,
                    identitySchemaRepository.getLatestSchemaVersion());
            ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(processSpecDto);
            List<String> processSpecList=new ArrayList<String>();
            processSpecList.add(json);
            result.success(processSpecList);
        }catch (Exception e){
            Log.e(getClass().getSimpleName(), "Error in getNewProcessSpec", e);
        }
    }
}
