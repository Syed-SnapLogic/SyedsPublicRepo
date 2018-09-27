/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.jmstest;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Syed
 */
public class ConnectionsTest extends Thread {
    private static final LoadingCache<Map<String, String>, Map<String, Integer>> CACHE;

    private Map<String, String> config;
    private int id;

    static {
        CACHE = CacheBuilder.newBuilder()
                .build(
                        new CacheLoader<Map<String, String>, Map<String, Integer>>() {
                            @Override
                            public Map<String, Integer> load(Map p) throws Exception {
                                return new ConcurrentHashMap<>();
                            }
                        }
                );
    }

    public ConnectionsTest(Map<String, String> config, int id) {
        this.config = config;
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("\nThread with id:" + id + " has entered run()\n");
        Map<String, Integer> connections;
        try {
            connections = CACHE.get(config);
        } catch (java.util.concurrent.ExecutionException e1) {
            System.err.println("Error accessing cache");
            e1.printStackTrace();
            throw new RuntimeException("can't proceed further");
        }
        
        List<String> keyList = null;
        int index = 0;
        int totalCount = 0;
        String key;
        
        while (true) {
            synchronized (connections) {
                System.out.println("Thread with id: " + id + " entered sync block");
                if (keyList == null) {
                    System.out.println("keyList is null");
                    keyList = new ArrayList<>(connections.keySet());
                    totalCount = keyList.size();
                    System.out.println("keyList set to: " + keyList);
                }
                
                if (index == totalCount) {
                    System.out.println("creating new entry");
                    connections.put((key = UUID.randomUUID().toString()),
                            1 + ((int) (Math.random() * 10)));
                    System.out.println("successfully creted new entry with key: " + key);
                } else {
                   key = keyList.get(index++); 
                   System.out.println("used existing entry with key: " + key);                   
                }
                try {
                    Thread.sleep(5000);
                } catch (Exception x) {
                    // no op
                }
                System.out.println("Thread with id: " + id + " finished sync block\n");
                break;
            }
        }
    }
    
    public static void main(String str[]) {
        System.out.println("Testing the approach used in JMS snaps");
        try {
            Thread.sleep(15000);
        } catch (Exception x) {
            // NO OP
        }
        testForSingleConfig();
        try {
            Thread.sleep(5000);
        } catch (Exception x) {
            // NO OP
        }
        //testForMultipleConfigs();
    }
    
    private static void testForSingleConfig() {
        Map<String, String> map = new ImmutableMap.Builder<String, String>()
                .put("config", "c1")
                .build();

        System.out.println("Test with five threads having same config\n");
        Thread t[] = new Thread[5];
        for (int i = 1; i < 6; i++) {
            t[i-1] = new ConnectionsTest(map, i);
            t[i-1].start();
        }
        for (int i = 1; i < 6; i++) {
            try {
                t[i-1].join();
            } catch (Exception x) {
                // NO OP
            }
        }
        System.out.println("Finished test with same config\n\n");
    }
    
    private static void testForMultipleConfigs() {
        Map<String, String> map1 = new ImmutableMap.Builder<String, String>()
                .put("config", "c1")
                .build();
        Map<String, String> map2 = new ImmutableMap.Builder<String, String>()
                .put("config", "c2")
                .build();

        System.out.println("\nTest with five threads having config c1 and "
                + "five other threads having config c2\n");
        Thread t[] = new Thread[10];
        for (int i = 1; i < 11; i++) {
            t[i-1] = new ConnectionsTest(i < 6 ? map1 : map2, i);
            t[i-1].start();
        }
        for (int i = 1; i < 11; ++i) {
            try {
                t[i-1].join();
            } catch (Exception x) {
                // NO OP
            }
        }
        System.out.println("Finished testing with multiple configs...");
    }
}
