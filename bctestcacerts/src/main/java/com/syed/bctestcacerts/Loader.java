/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.bctestcacerts;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

/**
 *
 * @author gaian
 */
public class Loader {

    public static final String cacerts = "/home/gaian/SyedsWorld/Java11/AdoptOpenJDK11.0.8_10/lib/security/cacerts";
    public static final String passphrase = "changeit";

    public static void main(String s[]) throws Exception {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        Security.insertProviderAt(new BouncyCastleJsseProvider(), 2);
        Provider[] p = Security.getProviders();
        for (Provider P : p) {
            System.out.println(P.getName());
        }
        System.out.println("type: " + KeyStore.getDefaultType());
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        System.out.println(keystore.getClass().getName());

        char[] passphraseArray = null;
        if (null != passphrase) {
            passphraseArray = passphrase.toCharArray();
        }
        keystore.load(new FileInputStream(new File(cacerts)),
                passphraseArray);
        System.out.println("successful!!");
    }
}
