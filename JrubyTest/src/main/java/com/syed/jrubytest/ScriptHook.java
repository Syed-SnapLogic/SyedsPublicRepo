/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.jrubytest;

import java.util.Iterator;

/**
 * ScriptHook is the interface that should be implemented as a callback mechanism for
 * ExecuteScript snap to call into the script.
 *
 * @author ksubramanian
 * @since 2013
 */
public interface ScriptHook {

    /**
     * Scripts should implement this method to provide application logic.
     */
    void execute();

    /**
     * Input is interface that is used by the script to read input from the snap's input view.
     */
    public static interface Input extends Iterator<Object> {
    }

    /**
     * Output is interface that is used by the script to send output data to the snap output view.
     */
    public static interface Output {

        /**
         * Write the data to the snap output.
         *
         * @param data
         */
        void write(Object data);

        /**
         * Write the data that was generated for the given incoming data to the snap output.
         * This method carries the lineage data forward.
         *
         * @param incomingData
         * @param data
         */
        void write(Object incomingData, Object data);
    }

    /**
     * Error is interface that is used by the script to send error data to snap error view.
     */
    public static interface Error extends Output {
    }
}