import java.util.*;
import java.io.*;

public class GetDependencies {
	private static final String MVN_COMMAND = "mvn com.github.ferstl:depgraph-maven-plugin:3.0.1:graph --pl=%s -DshowVersions -DshowConflicts -DshowDuplicates";
	private static final Runtime RUNTIME = Runtime.getRuntime();

	public static void main(String T[]) throws Exception {
		File currDir = new File(".");
		if (!currDir.isDirectory()) {
			throw new RuntimeException("go to hell");
		}
		else {
			System.out.println("Working on curr dir " + currDir.getName());
			Thread.sleep(5000);
		}
		File[] contents = currDir.listFiles();
		Set<String> list = new TreeSet<>();
		String currPath = ".";
		getSnappacks(currPath, list, contents);
		System.out.println("\n\n\n============================\n\tSnap packs\n============================");
		for (String sp : list) {
			System.out.println("\t" + sp);
		}
		Thread.sleep(5000);
		System.out.println("\n\nFollowing are the users of org.xerial.snappy:snappy-java.....\n");
		for (String sp : list) {
			List<String> l = usesSnappy(sp);
			if (!l.isEmpty()) {
				System.out.println(sp);
				for (String t : l) {
					System.out.println("\t" + t);
				}
			}
		}
	}

	private static List<String> usesSnappy(String snapPack) throws Exception {
		String mvnCommand = String.format(MVN_COMMAND, snapPack);
		System.out.println("\n\nStarting mvn on " + snapPack);
		Process p = RUNTIME.exec(mvnCommand);
		p.waitFor();
		System.out.println("Finished executing mvn on " + snapPack);
		List<String> list = new ArrayList<>();
		try {
         	       BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(snapPack + File.separator + "target/dependency-graph.dot")));
		       String line;
		       while ((line = br.readLine()) != null) {
			       if (line.contains("org.xerial.snappy:snappy-java")) {
				      list.add(line); 
			       }
		       }
		} catch (Exception x) {
			System.out.println("\n\n***** Error for " + snapPack);
		}
	       return list;
	}

	private static void getSnappacks(String currPath, Set<String> list, File[] contents) throws Exception {
		for (File content : contents) {
		        boolean foundSnappack = false;
			boolean continueFlag = false;
			if (content.isDirectory()) {
	                	if (content.getName().equals("japis") || content.getName().equals("travis")) {
	 				Thread.sleep(5000);
					continue;
				}
				File[] subContents = content.listFiles();
				String[] names = content.list();
				if (arrayContainsItem(names, "pom.xml")) {
					for (File subContent : subContents) {
						if (subContent.isDirectory()) {
							File[] subSubContents = subContent.listFiles();
							String[] subNames = subContent.list();
							if (arrayContainsItem(subNames, "pom.xml")) {
								for (int i = 0; i < names.length; i++) {
								    getSnappacks(currPath + File.separator + content.getName(), list, subContents);
								    continueFlag = true;
								    break;
								}

							} else {
								foundSnappack = true;
								break;
							}
						}
					}

				}

		      }
		      if (continueFlag) {
			      continue;
		      }
		      if (foundSnappack) {
			    list.add(currPath + File.separator + content.getName());
		      }
		}
	}

	private static boolean arrayContainsItem(Object[] arr, Object item) {
		for (Object obj : arr) {
			if (obj.equals(item)) {
				return true;
			}
		}
		return false;
	}
}
