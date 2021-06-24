/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.test.bctest;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 *
 * @author gaian
 */
public class CustomSecureRandom extends SecureRandom {
    @Override
    public synchronized void nextBytes(final byte[] bytes) {
        Arrays.fill(bytes, (byte) 1);
    }
}
