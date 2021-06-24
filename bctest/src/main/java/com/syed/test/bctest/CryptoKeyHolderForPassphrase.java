/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.test.bctest;

import com.google.common.collect.ImmutableMap;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoKeyHolderForPassphrase  {
    private static final Map<String, Object> DEFAULT_PARAMETERS = ImmutableMap.<String,
            Object>builder()
            .put("key_gen_iterations", 10000)
            .put("key_gen_algorithm", "PBKDF2WithHmacSHA1")
            .build();
    public static final String KEY_SIZE = "key_size";
    public static final String KEY_SALT = "key_salt";
    public static final String PASSPHRASE = "passphrase";
    public static final String KEY_GEN_ALGORITHM = "key_gen_algorithm";
    public static final String KEY_GEN_ITERATIONS = "key_gen_iterations";
    private final Map<String, Object> parameters = new HashMap<>();
    private final String passPhrase;
    private final SecureRandom secureRandom;

    public CryptoKeyHolderForPassphrase(final String passPhrase, final String algorithm, final
    Number keySize, final SecureRandom secureRandom) {
        this.passPhrase = passPhrase;
        this.secureRandom = secureRandom;
        parameters.putAll(DEFAULT_PARAMETERS);
        parameters.put("key_algorithm", algorithm);
        parameters.put(KEY_SIZE, keySize);
        byte[] salt = new byte[20];
        secureRandom.nextBytes(salt);
        String saltString = new String(GetCipherFromPlainText.encodeToChar(salt, false));
        parameters.put(KEY_SALT, saltString);
    }

    public void updateParameters(final Map<String, Object> parameters) {
        this.parameters.putAll(CoercionUtils.getMap(parameters, PASSPHRASE));
    }

     public Map<String, Object> getParameters() {
        Map<String, Object> retval = new HashMap<>(1);
        retval.put(PASSPHRASE, new HashMap<>(parameters));
        return retval;
    }

    public Key getKey() {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CoercionUtils.getString(
                    parameters, KEY_GEN_ALGORITHM));
            Number keySize = CoercionUtils.getNumber(parameters, KEY_SIZE);
            Number keyIterations = CoercionUtils.getNumber(parameters, KEY_GEN_ITERATIONS);
            byte[] salt = CoercionUtils.getByteArray(parameters, KEY_SALT);
            try {
                PBEKeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), salt, keyIterations
                        .intValue(), keySize.intValue());
                try {
                    SecretKey secretKey = keyFactory.generateSecret(spec);
                    return new SecretKeySpec(secretKey.getEncoded(),
                            CoercionUtils.getString(parameters, "key_algorithm"));
                } finally {
                    spec.clearPassword();
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                throw new RuntimeException("illegal arg");
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("gen exception");
        }
    }
}
