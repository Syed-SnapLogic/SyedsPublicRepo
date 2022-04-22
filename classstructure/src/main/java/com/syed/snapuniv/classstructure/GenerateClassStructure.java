package com.syed.snapuniv.classstructure;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateClassStructure {
    private static final String EXTENDS_PATTERN_STR = "public\\s+[abstract\\s+]*class\\s+\\w+\\s+extends\\s+\\w+[{\\s]";
    private static final String PACKAGE_PATTERN_STR = "package\\s+[0-9a-zA-Z\\.]+;";
    private static final String IMPORT_PATTERN_STR = "[\\s;]*import\\s+[0-9a-zA-Z\\.]+SimpleSnap\\s*;";
    private static Pattern EXTENDS_PATTERN;
    private static Pattern PACKAGE_PATTERN;
    private static Pattern IMPORT_PATTERN;
    private static final String SOURCE_FILES_LOCATION = "src/main/java/com/snaplogic/snaps/";


    public static void main(String str[]) throws Exception {
        EXTENDS_PATTERN = Pattern.compile(EXTENDS_PATTERN_STR);
        PACKAGE_PATTERN = Pattern.compile(PACKAGE_PATTERN_STR);
        IMPORT_PATTERN = Pattern.compile(IMPORT_PATTERN_STR);
        if (str.length < 1) {
            throw new RuntimeException("Usage:\n java GenerateClassStructure <maven project path>");
        }

        String sourceLoc = str[0];
        if (!sourceLoc.startsWith(File.separator)) {
            sourceLoc = "." + File.separator + sourceLoc;
        }
        if (!sourceLoc.endsWith(File.separator)) {
            sourceLoc += File.separator;
        }
        sourceLoc = sourceLoc + SOURCE_FILES_LOCATION;

        File source = new File(sourceLoc);
        if (!source.exists() || !source.isDirectory()) {
            throw new RuntimeException("Given code base " + source.getAbsolutePath() + " doesn't seem to a valid snappack");
        }

        String[] filesList = source.list();
        Map<String, String> map = new TreeMap<>();
        traverse(sourceLoc, filesList, map);
        System.out.println("Class Hierarchy\n" + map);
        generateGraph(map);
    }

    static List<String> isSnap(String clazz, Map<String, String> map) throws Exception {
        List<String> list = new ArrayList<>();
        if (map.keySet().contains(clazz)) {
            while (true) {
                list.add(clazz);
                clazz = map.get(clazz);
                if (clazz == null) {
                    return null;
                } else if (clazz.equals("com.snaplogic.snap.api.SimpleSnap.SimpleSnap")) {
                    list.add(clazz);
                    break;
                }
            }
            return list;
        } else {
            return null;
        }
    }

    static void generateGraph(Map<String, String> map) throws Exception {
        Map<String, String> nodes = new TreeMap<>();
        List<String> edges = new ArrayList<>();
        int nodeCounter = 1;

        Collection<String> keys = map.keySet();
        for (String key : keys) {
            List<String> hierarchy = isSnap(key, map);
            if (hierarchy != null && !hierarchy.isEmpty()) {
                int count = hierarchy.size() - 1;
                for (int i = 0; i < count; i++) {
                    String start = hierarchy.get(i);
                    String end = hierarchy.get(i + 1);
                    if (!nodes.containsKey(start)) {
                        nodes.put(start, "Node" + (nodeCounter++) +
                                "[label=\"" + start + "\",color=\"black\",shape=\"box\"]");
                    }
                    if (!nodes.containsKey(end)) {
                        nodes.put(end, "Node" + (nodeCounter++) +
                                "[label=\"" + end + "\",color=\"black\",shape=\"box\"]");
                    }
                    String startNode = nodes.get(start);
                    startNode = startNode.substring(0, startNode.indexOf("["));
                    String endNode = nodes.get(end);
                    endNode = endNode.substring(0, endNode.indexOf("["));
                    if (!edges.contains(startNode + "->" + endNode + "[color=\"green\"]")) {
                        edges.add(startNode + "->" + endNode + "[color=\"green\"]");
                    }
                }
            }
        }
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (!tmpDir.endsWith(File.separator)) {
            tmpDir += File.separator;
        }
        File dotFile = new File(tmpDir + "Graph" + System.nanoTime() + ".dot");
        String pngFile = tmpDir + "Graph" + System.nanoTime() + ".png";
        PrintStream printStream = new PrintStream(dotFile);
        printStream.println("digraph snaplogic {");
        Collection<String> finalNodes = nodes.values();
        for (String node : finalNodes) {
            printStream.println("\t" + node);
        }
        for (String edge : edges) {
            printStream.println("\t" + edge);
        }
        printStream.println("}");
        printStream.close();

        try {
            createPng(dotFile, pngFile);
            System.out.println("Successfully generated graph file at " + pngFile);
        } catch (Exception e) {
            System.out.println("Unable to create PNG file.  Ensure graphviz is installed and \"dot\" utility is set on System Path");
            System.out.println("Dot file available here: " + dotFile.getAbsolutePath());
            System.out.println("Use http://www.webgraphviz.com/ to upload the above dot file and see the graph");
        }
    }

    static void createPng(File dotFile, String pngFile) throws Exception {
        String command = "dot -Tpng " + dotFile.getAbsolutePath();
        System.out.println("Executing command: " + command);
        Process process = Runtime.getRuntime().exec(command);
        InputStream inputStream = process.getInputStream();
        PrintStream printStream = new PrintStream(new File(pngFile));
        IOUtils.copy(inputStream, printStream);
        inputStream.close();
        printStream.close();
        process.waitFor();
    }

    static void traverse(String base, String[] filesList, Map<String, String> map) throws Exception {
        for (String file : filesList) {
            String base1 = base;
            if (!base.endsWith(File.separator)) {
                base1  = base + File.separator;
            }
            base1 = base1 + file;
            File file1 = new File(base1);
            if (file1.isDirectory()) {
                traverse(base1, file1.list(), map);
            } else {
                if (file1.getAbsolutePath().endsWith(".java")) {
                    parseJavaSource(file1, map, filesList);
                }
            }
        }
    }

    static void parseJavaSource(File file, Map<String, String> map, String[] filesList) throws Exception {
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        Matcher matcher = EXTENDS_PATTERN.matcher(content);
        if (matcher.find()) {
            String group = matcher.group();
            String splits[] = group.split("\\s+extends\\s+");
            String superClass = splits[1].trim();
            String subClass = splits[0].trim();
            String subClassSplits[] = subClass.split("\\s+class\\s+");
            subClass = subClassSplits[1].trim();
            if (true /*superClass.equals("SimpleSnap")*/) {
                matcher = PACKAGE_PATTERN.matcher(content);
                if (!matcher.find()) {
                    throw new RuntimeException("No package stmt found in " + file.getAbsolutePath());
                }
                group = matcher.group();
                String[] pkgGroups = group.split("package\\s+");
                String pkgName = pkgGroups[1].substring(0, pkgGroups[1].length() - 1).trim();
                subClass = pkgName + "." + subClass;
                matcher = IMPORT_PATTERN.matcher(content);
                if (!matcher.find()) {
                    if (ArrayUtils.contains(filesList, superClass + ".java")) {
                        superClass = pkgName + "." + superClass;
                    } else {
                        throw new RuntimeException("No import stmt found for " + superClass);
                    }
                } else {
                    group = matcher.group();
                    String[] importGropus = group.split("import\\s+");
                    String importPkg = importGropus[1].substring(0, importGropus[1].length() - 1).trim();
                    superClass = importPkg + "." + superClass;
                }
                map.put(subClass, superClass);
            }
        }
    }
}
