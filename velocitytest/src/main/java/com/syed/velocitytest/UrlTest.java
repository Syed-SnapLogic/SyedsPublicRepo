/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.velocitytest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author gaian
 */
public class UrlTest {

    static final ConcurrentMap<URL, byte[]> dataMap = new ConcurrentHashMap<>();
    static final ConcurrentMap<URL, Map<String, List<String>>> headerMap
            = new ConcurrentHashMap<>();

    static {
        URL.setURLStreamHandlerFactory(new UrlStreamFactoryImpl());
        System.out.println("done with setting url stream handler factory");
    }
    
    public static void main(String s[]) throws Exception {
        URL url = new URL("file:///tmp/a.txt");
        System.out.println(url.openConnection().getClass().getName());
    }
}

class UrlStreamFactoryImpl implements URLStreamHandlerFactory {

    @Override
    public URLStreamHandler createURLStreamHandler(final String protocol) {
        System.out.println("in createURLStreamHandler()");
        return new MockUrlStreamHandler();
    }
}

class MockUrlStreamHandler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        System.out.println("url: " + u.toString());
        if ("sldb".equalsIgnoreCase(u.getProtocol())) {
            // Sldb urls are added fake host to make it look real and get around some weird
            // url parsing issue when no host is set.
            return null;
        }
        return new MockUrlConnection(u);
    }
}

class MockUrlConnection extends URLConnection {

    private final URL url;

    public MockUrlConnection(final URL url) {
        super(url);
        this.url = url;
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        return UrlTest.headerMap.getOrDefault(url, Collections.EMPTY_MAP);
    }

    @Override
    public String getHeaderField(final String name) {
        List<String> list = UrlTest.headerMap.getOrDefault(url, Collections.emptyMap()).get(name);

        if (list != null) {
            return list.get(0);
        }

        return super.getHeaderField(name);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        byte[] data = UrlTest.dataMap.get(url);
        if (data != null) {
            return new ByteArrayInputStream(data);
        }
        try {
            Path path = Paths.get(this.url.toURI());
            return Files.newInputStream(path);
        } catch (Exception e) {
            // ignore
        }
        return new ByteArrayInputStream("".getBytes());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new ProxyOutputStream(this.url);
    }

    @Override
    public void connect() throws IOException {
    }
}

class ProxyOutputStream extends ByteArrayOutputStream {

    final private byte[] bytes = "[{}]".getBytes();
    private final URL url;

    public ProxyOutputStream(URL url) {
        this.url = url;
    }

    @Override
    public synchronized byte[] toByteArray() {
        byte[] byteArray = super.toByteArray();
        if (byteArray.length == 0) {
            return bytes;
        }
        return byteArray;
    }

    @Override
    public void close() throws IOException {
        super.close();
        UrlTest.dataMap.put(url, toByteArray());
    }
}
