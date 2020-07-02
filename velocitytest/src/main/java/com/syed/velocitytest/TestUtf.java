/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.velocitytest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
//import org.apache.velocity.VelocityContext;
//import org.apache.velocity.app.VelocityEngine;

/**
 *
 * @author gaian
 */
public class TestUtf {
    public static void main(String str[]) throws Exception {
        SOAPMessage msg = MessageFactory.newInstance().createMessage();
        SOAPHeader sh = msg.getSOAPHeader();
        //Iterator<SOAPElement> nodeIterator = sh.getChildElements();
        String template = "Hi $name";
        Map<String, Object> values = new HashMap<>();
        values.put("name", "dirparty_SL_3câêîôûñ&ç¡¿ÉÙ#ËÇ¨°¸ðø©¢¾A²½µ®§÷¶þß𝘩");
        //VelocityContext vc = new VelocityContext(values);
        System.out.println("velocity context created");
        /*Object[] keys = vc.getKeys();
        for (Object o : keys) {
            System.out.println(vc.get(o.toString()));
        }
        
        Properties p = new Properties();
        p.put("input.encoding", "UTF-8");
        p.put("output.encoding", "UTF-8");
        p.put("eventhandler.referenceinsertion.class",
                "com.syed.velocitytest.MyHandler");
        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        System.out.println("velocity engine created");
        
        Writer w = new StringWriter();
        ve.evaluate(vc, w, "LOG LABEL", template);
        System.out.println("Final output:\n" + w.toString());
        w.close();*/
    }
}
