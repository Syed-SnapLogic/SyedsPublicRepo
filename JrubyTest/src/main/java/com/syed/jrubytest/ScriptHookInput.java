/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.jrubytest;


import java.util.Iterator;
import java.util.Map;

/**
* ScriptHookInput is the input view wrapper passed into the script.
*
* @author ksubramanian
* @since 2015
*/
public class ScriptHookInput implements ScriptHook.Input {
    private final Iterator<Map> documentIterator;
    private Map document;

    public ScriptHookInput(Iterator<Map> documentIterator) {
        this.documentIterator = documentIterator;
    }

    @Override
    public boolean hasNext() {
        return documentIterator.hasNext();
    }

    @Override
    public Object next() {
        this.document = documentIterator.next();
        return document;
    }

    @Override
    public void remove() {
        // Unsupported.
    }

    /**
     * Returns the incoming document. If there is no incoming data object, returns null.
     *
     * @return incomingDocument or null
     */
    public Map getInputDocument() {
        return this.document;
    }
}

