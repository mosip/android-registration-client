package io.mosip.registration.keymanager.util;

import android.util.Log;
import io.mosip.registration.keymanager.entity.CACertificateStore;
import io.mosip.registration.keymanager.exception.KeymanagerServiceException;
import lombok.NonNull;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.*;
import java.time.*;
import java.util.Date;
import java.util.Objects;

public class CertificateManagerUtil {

    private static final String TAG = CertificateManagerUtil.class.getSimpleName();

    public static boolean isValidCertificateData(String certData) {
        return certData != null && !certData.trim().isEmpty();
    }

    public static Certificate convertToCertificate(String certData) {
        if(!isValidCertificateData(certData))
            throw new KeymanagerServiceException(KeyManagerErrorCode.INVALID_CERTIFICATE.getErrorCode(),
                    KeyManagerErrorCode.INVALID_CERTIFICATE.getErrorMessage());

        try {
            StringReader strReader = new StringReader(certData);
            PemReader pemReader = new PemReader(strReader);
            PemObject pemObject = pemReader.readPemObject();
            byte[] certBytes = pemObject.getContent();
            CertificateFactory certFactory = CertificateFactory.getInstance(KeyManagerConstant.CERTIFICATE_TYPE);
            return certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
        } catch(IOException | CertificateException e) {
            Log.e(TAG, "Error Parsing Certificate.", e);
            throw new KeymanagerServiceException(KeyManagerErrorCode.CERTIFICATE_PARSING_ERROR.getErrorCode(),
                    KeyManagerErrorCode.CERTIFICATE_PARSING_ERROR.getErrorMessage() + e.getMessage());
        }
    }

    public static String getCertificateThumbprint(X509Certificate x509Cert) {
        if(x509Cert == null)
            throw new KeymanagerServiceException(KeyManagerErrorCode.CERTIFICATE_THUMBPRINT_ERROR.getErrorCode(),
                    KeyManagerErrorCode.CERTIFICATE_THUMBPRINT_ERROR.getErrorMessage());
        try {
            return DigestUtils.sha1Hex(x509Cert.getEncoded());
        } catch (CertificateEncodingException e) {
            Log.e(TAG, "Error generating certificate thumbprint.", e);
            throw new KeymanagerServiceException(KeyManagerErrorCode.CERTIFICATE_THUMBPRINT_ERROR.getErrorCode(),
                    KeyManagerErrorCode.CERTIFICATE_THUMBPRINT_ERROR.getErrorMessage());
        }
    }

    public  static boolean isCertificateDatesValid(X509Certificate x509Cert) {

        try {
            LocalDateTime localDateTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
            Date currentDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            x509Cert.checkValidity(currentDate);
            return true;
        } catch(CertificateExpiredException | CertificateNotYetValidException exp) {
            Log.e(TAG, "Ignore this exception, the exception thrown when certificate dates are not valid.");
        }
        try {
            // Checking both system default timezone & UTC Offset timezone. Issue found in reg-client during trust validation.
            x509Cert.checkValidity();
            return true;
        } catch(CertificateExpiredException | CertificateNotYetValidException exp) {
            Log.e(TAG, "Ignore this exception, the exception thrown when certificate dates are not valid.");
        }
        return false;
    }

    public static boolean isSelfSignedCertificate(X509Certificate x509Cert) {
        try {
            x509Cert.verify(x509Cert.getPublicKey());
            return true;
        } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException | SignatureException
                 | NoSuchProviderException exp) {
            Log.e(TAG, "Ignore this exception, the exception thrown when signature validation failed.");
        }
        return false;
    }

    /**
     * Function to format X500Principal of certificate.
     *
     * @param certPrincipal String form of X500Principal
     *
     * @return String of Custom format of certificateDN.
     */
    public static String formatCertificateDN(String certPrincipal) {

        X500Name x500Name = new X500Name(certPrincipal);
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getAttributeIfExist(x500Name, BCStyle.CN));
        strBuilder.append(getAttributeIfExist(x500Name, BCStyle.OU));
        strBuilder.append(getAttributeIfExist(x500Name, BCStyle.O));
        strBuilder.append(getAttributeIfExist(x500Name, BCStyle.L));
        strBuilder.append(getAttributeIfExist(x500Name, BCStyle.ST));
        strBuilder.append(getAttributeIfExist(x500Name, BCStyle.C));

        if (strBuilder.length() > 0 && strBuilder.toString().endsWith(",")) {
            return strBuilder.substring(0, strBuilder.length() - 1);
        }
        return strBuilder.toString();
    }

    private static String getAttributeIfExist(X500Name x500Name, ASN1ObjectIdentifier identifier) {
        RDN[] rdns = x500Name.getRDNs(identifier);
        if (rdns.length == 0) {
            return KeyManagerConstant.EMPTY;
        }
        return BCStyle.INSTANCE.oidToDisplayName(identifier) + KeyManagerConstant.EQUALS
                + IETFUtils.valueToString((rdns[0]).getFirst().getValue()) + KeyManagerConstant.COMMA;
    }

    public static boolean isValidTimestamp(LocalDateTime timeStamp, CACertificateStore certStore) {
        LocalDateTime certNotBefore = Instant.ofEpochMilli(certStore.getCertNotBefore()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime certNotAfter = Instant.ofEpochMilli(certStore.getCertNotAfter()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return timeStamp.isEqual(certNotBefore) || timeStamp.isEqual(certNotAfter)
                || (timeStamp.isAfter(certNotBefore) && timeStamp.isBefore(certNotAfter));
    }

    public static byte[] getCertificateThumbprint(Certificate cert) {
        try {
            return DigestUtils.sha256(cert.getEncoded());
        } catch (CertificateEncodingException e) {
            Log.e(TAG, "Error generating certificate thumbprint.", e);
            throw new KeymanagerServiceException(KeyManagerErrorCode.CERTIFICATE_THUMBPRINT_ERROR.getErrorCode(),
                    KeyManagerErrorCode.CERTIFICATE_THUMBPRINT_ERROR.getErrorMessage());
        }
    }

    public static String getCertificateThumbprintInHex(Certificate cert) {
        return Hex.toHexString(getCertificateThumbprint(cert)).toUpperCase();
    }
}
