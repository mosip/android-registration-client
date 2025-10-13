package io.mosip.registration.clientmanager.service.external;

import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.exception.InvalidMachineSpecIDException;
import io.mosip.registration.clientmanager.exception.RegBaseCheckedException;
import io.mosip.registration.clientmanager.exception.RegBaseUncheckedException;

/**
 * Interface to handles the Pre-Registration Packet data.
 *
 * @author balamurugan ramamoorthy
 * @since 1.0.0
 */
public interface PreRegZipHandlingService {

    RegistrationDto extractPreRegZipFile(byte[] preRegZipFile) throws Exception;

    PreRegistrationDto encryptAndSavePreRegPacket(String preRegistrationId, String preRegPacket, CenterMachineDto centerMachineDto) ;

    String storePreRegPacketToDisk(String preRegistrationId, byte[] encryptedPacket, CenterMachineDto centerMachineDto) throws RegBaseUncheckedException;


    byte[] decryptPreRegPacket(String symmetricKey, byte[] encryptedPacket) throws Exception;

}