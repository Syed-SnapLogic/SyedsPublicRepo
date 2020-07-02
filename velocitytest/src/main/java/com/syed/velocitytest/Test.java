/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.velocitytest;


import javax.xml.soap.*;
import java.util.Iterator;

/**
 *
 * @author gaian
 */
public class Test {
    public static void main(String i[]) throws Exception {
        System.out.println("java.version:" + System.getProperty("java.version"));
        System.out.println(System.getProperties());
        MessageFactory messageFactory = MessageFactory.newInstance("SOAP 1.1 Protocol");
        System.out.println(messageFactory.getClass().getName());
        System.out.println("superb");
        SOAPMessage sm = messageFactory.createMessage();
        SOAPHeader sh = sm.getSOAPHeader();
        Iterator<Node> nodeIterator = sh.getChildElements();
        System.out.println(nodeIterator.hasNext());
    }
}
