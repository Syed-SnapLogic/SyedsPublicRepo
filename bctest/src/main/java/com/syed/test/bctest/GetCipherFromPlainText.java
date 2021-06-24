/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.test.bctest;

import java.io.CharArrayReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;

/**
 *
 * @author gaian
 */
public class GetCipherFromPlainText {
    private static final String CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDwTCCAqmgAwIBAgIJAOFhl+uHcsFaMA0GCSqGSIb3DQEBCwUAMHcxCzAJBgNV\n" +
            "BAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRIwEAYDVQQHDAlTYW4gTWF0ZW8x\n" +
            "EjAQBgNVBAoMCVNuYXBMb2dpYzEOMAwGA1UECwwFU25hcHMxGzAZBgNVBAMMEnRl\n" +
            "c3Quc25hcGxvZ2ljLmNvbTAeFw0xNzAzMDYxNjQzMjVaFw0yNzAzMDQxNjQzMjVa\n" +
            "MHcxCzAJBgNVBAYTAlVTMRMwEQYDVQQIDApDYWxpZm9ybmlhMRIwEAYDVQQHDAlT\n" +
            "YW4gTWF0ZW8xEjAQBgNVBAoMCVNuYXBMb2dpYzEOMAwGA1UECwwFU25hcHMxGzAZ\n" +
            "BgNVBAMMEnRlc3Quc25hcGxvZ2ljLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEP\n" +
            "ADCCAQoCggEBANeu+4v29k8LQGuk+4VMVtCgH9AYEfKHUgWUwObtd++u4S7LP4Ms\n" +
            "GjSHljb+LMF5cDutHw8jVZgdJjlWq3aXk1pqSo0w1t+0DM/r4UKCZu2Ss9/jOwsd\n" +
            "jKbK5L5TBm4Kjz/+xwP4q27wTj8xTvD+MIaoUnEEz6hy+1QN55pFRzrGC8Q7YsAK\n" +
            "OPaQWIc84dtu1P3z55FJEmt1f6q37MOBwy7elCrIt+zAAjTE6MgqZ4GbLk0MiYeJ\n" +
            "nfqOzH4NGS8cBUP7e3MMXXPzV6i7CE7JOTcpxKCI2NtWpGHMLWB522qsrvkhHKUz\n" +
            "N+XpMsAvDD79ygxLn8iCQP6aqSKNzRF7Qg8CAwEAAaNQME4wHQYDVR0OBBYEFJK8\n" +
            "j+3uFD/HNkxJ8hC6psY+uxtaMB8GA1UdIwQYMBaAFJK8j+3uFD/HNkxJ8hC6psY+\n" +
            "uxtaMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBADWW6LHxXjP4sHEv\n" +
            "BxsYuYuZo5I2wZvpKERQwHB/z79bqxMHMcS7W8yNfSPrqTTDHJTLHuMr8poKdL/z\n" +
            "kBlFXUjOvfHAchzWJ53taHq585z7AzVALSQiwzAKiA1AbvsoE3n6J0mKr7TqzXnC\n" +
            "RXfxG62nXLiojgT/FirakbdYx/9WEQQPS4Ki8zYcpGNx2xoup5NUB2XOkV7uZ2/4\n" +
            "/sZQlZXplD4kT4UHlz/uwevhOSq4k+5znRxpJzE3QyZnbKqspaYaZaunnzRHGn8U\n" +
            "VKzo4ackJ+o/af7YVw2CFhdOgFYKmXwspG4kCkgPMO0k1AnbGMej6BGvciY1/2Dy\n" +
            "+1PM3hQ=\n" +
            "-----END CERTIFICATE-----\n";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
    private static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final int[] IA = new int[256];

    public static final char[] encodeToChar(byte[] sArr, boolean lineSep) {
        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0) {
            return new char[0];
        } else {
            int eLen = sLen / 3 * 3;
            int cCnt = (sLen - 1) / 3 + 1 << 2;
            int dLen = cCnt + (lineSep ? (cCnt - 1) / 76 << 1 : 0);
            char[] dArr = new char[dLen];
            int left = 0;
            int d = 0;
            int cc = 0;

            while(left < eLen) {
                int i = (sArr[left++] & 255) << 16 | (sArr[left++] & 255) << 8 | sArr[left++] & 255;
                dArr[d++] = CA[i >>> 18 & 63];
                dArr[d++] = CA[i >>> 12 & 63];
                dArr[d++] = CA[i >>> 6 & 63];
                dArr[d++] = CA[i & 63];
                if (lineSep) {
                    ++cc;
                    if (cc == 19 && d < dLen - 2) {
                        dArr[d++] = '\r';
                        dArr[d++] = '\n';
                        cc = 0;
                    }
                }
            }

            left = sLen - eLen;
            if (left > 0) {
                d = (sArr[eLen] & 255) << 10 | (left == 2 ? (sArr[sLen - 1] & 255) << 2 : 0);
                dArr[dLen - 4] = CA[d >> 12];
                dArr[dLen - 3] = CA[d >>> 6 & 63];
                dArr[dLen - 2] = left == 2 ? CA[d & 63] : 61;
                dArr[dLen - 1] = '=';
            }

            return dArr;
        }
    }

    public static X509Certificate convertPEM(String pemCertificateString)
            throws IOException, CertificateException {
        char[] pemCertificate = pemCertificateString.toCharArray();
        PEMParser pemParser = new PEMParser(new CharArrayReader(pemCertificate));

        X509Certificate cert;
        try {
            X509CertificateHolder bcCert = (X509CertificateHolder)pemParser.readObject();
            X509Certificate certificate = converter.setProvider("BC").getCertificate(bcCert);
            cert = certificate;
        } catch (Throwable t1) {
            try {
                pemParser.close();
            } catch (Throwable t2) {
                t1.addSuppressed(t2);
            }
            throw t1;
        }

        pemParser.close();
        return cert;
    }

    public static void main(String str[]) throws Exception {
        Security.insertProviderAt(new BouncyCastleProvider(), 0);
        String plainText = "this is a version test of bouncy castle library";
        byte[] plainTextBytes = plainText.getBytes(StandardCharsets.UTF_8);
        //Key key = convertPEM(CERT).getPublicKey();
        Key key = new CryptoKeyHolderForPassphrase("thisissecret",
                "AES", BigInteger.valueOf(128), new CustomSecureRandom())
                .getKey();
        System.out.println(key.getFormat());
        System.out.println(key.getAlgorithm());
        Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key, new CustomSecureRandom());
        byte[] cipherBytes = cipher.doFinal(plainTextBytes);
        String cipherText = new String(encodeToChar(cipherBytes, true));
        System.out.println("PlainText:<" + plainText + ">");
        System.out.println("CipherText:<" + cipherText + ">");
        System.out.println("IV:<" + new String(encodeToChar(cipher.getIV(), true)) + ">");
    }
}
