/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.jrubytest;

import com.syed.jrubytest.ScriptHook;

/**
* ScriptHookOutput is the outbound view wrapper passed into the script.
*
* @author ksubramanian
* @since 2015
*/
public class ScriptHookOutput implements ScriptHook.Output, ScriptHook.Error {

    @Override
    public void write(final Object data) {
        System.out.println("data:\n" + data);
    }

    @Override
    public void write(final Object incomingData, final Object data) {
        System.out.println("incomingData:\n" + incomingData + "\ndata:\n" + data);
    }
}
