/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.test.bctest;

import java.io.CharArrayReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.Map;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jcajce.provider.symmetric.AES;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;

public class CoercionUtils {
    private static final char CHAR_SLASH = '/';

    private CoercionUtils() {
    }

    public static String toString(Object value, Object src) {
        if (!(value instanceof String)) {
            throw new RuntimeException("not string");
        }
        return (String) value;
    }

    public static String getString(Map<String, Object> map, String key) {
        return toString(map.get(key), key);
    }

    public static byte[] toByteArray(Object value, Object src) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        byte[] retval;
        if (!(value instanceof String) || (retval = Base64.decode((String) value)) == null) {
            throw new RuntimeException("not a string");
        }
        return retval;
    }

    public static byte[] getByteArray(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return toByteArray(value, key);
    }

    public static Number getNumber(Map<String, Object> map, String key) {
        Object retval = ObjectType.attemptToConvertToBigDecimal(map.get(key));
        if (!(retval instanceof BigDecimal)) {
            throw new RuntimeException("not a big decimal");
        }
        return ((Number) retval);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (!(value instanceof Map)) {
            return Collections.emptyMap();
        }
        return ((Map<String, Object>) value);
    }

    public static String getAlgorithmFromTransform(Object transformation) {
        if (transformation == null || "auto".equals(transformation) ||
                "".equals(transformation)) {
            return "AES";
        }
        String transformationString = String.valueOf(transformation);
        int index = transformationString.indexOf(CHAR_SLASH);
        if (index == -1) {
            return transformationString;
        }
        return transformationString.substring(0, index);
    }

    /**
     * Get PrivateKey object from PEM format string
     * @param privateKeyPEM private key PEM string
     * @return PrivateKey object
     */
    public static PrivateKey privateKeyFrom(String privateKeyPEM) {
        if (privateKeyPEM == null || privateKeyPEM.length() == 0) {
            throw new RuntimeException("per null or empty");
        }
        PEMParser pemParser = new PEMParser(new CharArrayReader(privateKeyPEM.toCharArray()));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider
                .PROVIDER_NAME);
        try {
            Object object = pemParser.readObject();
            if (object instanceof PEMKeyPair) {
                KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
                return kp.getPrivate();
            } else if (object instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo)object);
            } else {
                throw new RuntimeException("unsupported pem format");
            }
        } catch (PEMException ex) {
            throw new RuntimeException("key pair error");
        } catch (IOException ex) {
            throw new RuntimeException("some error");
        }
    }
}
