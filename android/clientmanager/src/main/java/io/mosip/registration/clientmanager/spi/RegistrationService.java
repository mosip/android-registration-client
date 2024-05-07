package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;

import java.util.List;

public interface RegistrationService {

    void approveRegistration(Registration registration);

    void rejectRegistration(Registration registration);

    RegistrationDto startRegistration(List<String> languages) throws Exception;

    RegistrationDto getRegistrationDto() throws Exception;

    void submitRegistrationDto(String makerName) throws Exception;

    void clearRegistration();

    BIR buildBIR(BiometricsDto biometricsDto);
}
