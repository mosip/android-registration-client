package io.mosip.registration.keymanager.spi;

import io.mosip.registration.keymanager.dto.CryptoManagerRequestDto;
import io.mosip.registration.keymanager.dto.CryptoManagerResponseDto;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;

public interface CryptoManagerService {

    KeyGenerator generateAESKey(int keyLength) throws NoSuchAlgorithmException;

    CryptoManagerResponseDto encrypt(CryptoManagerRequestDto cryptoRequestDto) throws Exception;

    byte[] symmetricEncryptWithRandomIV(SecretKey secretKey, byte[] data, byte[] aad) throws Exception;

    byte[] asymmetricEncrypt(PublicKey publicKey, byte[] data) throws Exception;

    Certificate convertToCertificate(String certData) throws Exception;

    byte[] getCertificateThumbprint(Certificate cert) throws Exception;

}
