package io.mosip.registration.clientmanager.service.external;

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

    PreRegistrationDto encryptAndSavePreRegPacket(String preRegistrationId, byte[] preRegPacket)
            throws Exception;

    String storePreRegPacketToDisk(String preRegistrationId, byte[] encryptedPacket) throws RegBaseCheckedException, RegBaseUncheckedException;


    byte[] decryptPreRegPacket(String symmetricKey, byte[] encryptedPacket);

}