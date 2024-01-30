/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.dto.SimpleType;
import io.mosip.registration_client.model.DemographicsDataPigeon;

@Singleton
public class DemographicsDetailsApi implements DemographicsDataPigeon.DemographicsApi {
    private final RegistrationService registrationService;
    AuditManagerService auditManagerService;

    @Inject
    public DemographicsDetailsApi(RegistrationService registrationService, AuditManagerService auditManagerService) {
        this.registrationService = registrationService;
        this.auditManagerService = auditManagerService;

    }


    @Override
    public void addDemographicField(@NonNull String fieldId, @NonNull String value, @NonNull DemographicsDataPigeon.Result<String> result) {
        try {
            this.registrationService.getRegistrationDto().addDemographicField(fieldId, value);
            result.success("Ok");
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add field failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void getDemographicField(@NonNull String fieldId, @NonNull DemographicsDataPigeon.Result<String> result) {
        try {
            RegistrationDto registrationDto = this.registrationService.getRegistrationDto();

            registrationDto.getDemographics().forEach((k,v) -> {
                if(k.equals(fieldId)){
                    if(v instanceof String){
                        result.success((String) v);
                        return;
                    }
                }
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Get field failed!" + Arrays.toString(e.getStackTrace()));
        }
        result.success("");
    }

    @Override
    public void getHashValue(@NonNull byte[] bytes, @NonNull DemographicsDataPigeon.Result<String> result) {
        String hashValue=CryptoUtil.computeFingerPrint(bytes,null);
        
        result.success(hashValue);
    }


    @Override
    public void addSimpleTypeDemographicField(@NonNull String fieldId, @NonNull String value, @NonNull String language, @NonNull DemographicsDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().addDemographicField(fieldId, value, language);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add simple type field failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void getSimpleTypeDemographicField(@NonNull String fieldId, @NonNull String language, @NonNull DemographicsDataPigeon.Result<String> result) {
        try {
            RegistrationDto registrationDto = this.registrationService.getRegistrationDto();
            registrationDto.getDemographics().forEach((k,v) -> {
                if(k == fieldId){
                    if((v instanceof String)){
                        result.success((String) v);
                        return;
                    }else if( v instanceof List){
                        ((List<SimpleType>) v).forEach((value) -> {
                            if(value.getLanguage() == language){
                                result.success(value.getValue());
                                return;
                            }
                        });
                    }
                }
            });
            Log.e(getClass().getSimpleName(), "Get field failed!" + this.registrationService.getRegistrationDto().getDemographics());
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Get field failed!" + Arrays.toString(e.getStackTrace()));
        }
        result.success("");
    }

    @Override
    public void setDateField(@NonNull String fieldId, @NonNull String subType, @NonNull String day, @NonNull String month, @NonNull String year, @NonNull DemographicsDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().setDateField(fieldId, subType, day, month, year);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add date field failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void removeDemographicField(@NonNull String fieldId, @NonNull DemographicsDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().removeDemographicField(fieldId);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add date field failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void setConsentField(@NonNull String consentData, @NonNull DemographicsDataPigeon.Result<Void> result) {
        try {
            this.registrationService.getRegistrationDto().setConsent(consentData);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Add consent dto failed!" + Arrays.toString(e.getStackTrace()));
        }
    }
}
