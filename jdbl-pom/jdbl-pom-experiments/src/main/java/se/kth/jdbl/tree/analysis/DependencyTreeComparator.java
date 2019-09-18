package se.kth.jdbl.tree.analysis;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DependencyTreeComparator {

    public static void printMap(Map<String, DtType> mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DtType> pair = (Map.Entry<String, DtType>) it.next();

            int heightDebloated = pair.getValue().getHeightDebloated();
            int heightOriginal = pair.getValue().getHeightOriginal();

            if (heightDebloated != heightOriginal) {

                System.out.println(pair.toString());
            }
        }
    }

    public static void main(String[] args) {
        File dir = new File("/home/cesarsv/Documents/xperiments/notebooks/Data/trees");
        File[] directoryListing = dir.listFiles();
        Map<String, DtType> dtMap = new HashMap<>();

        if (directoryListing != null) {
            for (File child : directoryListing) {

                if (child.getName().endsWith("debloated.txt")) {
                    try {
                        DependencyTreeAnalyzer dependencyTreeAnalyzer = new DependencyTreeAnalyzer(child.getAbsolutePath());
                        String libraryName = child.getName().substring(0, child.getName().length() - 14);
                        int heightDebloated = dependencyTreeAnalyzer.heightOfDependencyTree();
                        if (dtMap.containsKey(libraryName)) {
                            dtMap.get(libraryName).setHeightDebloated(heightDebloated);
                        } else {
                            DtType dtType = new DtType();
                            dtType.setHeightDebloated(heightDebloated);
                            dtMap.put(libraryName, dtType);
                        }
                    } catch (Exception e) {
                        continue;
                    }

                } else { // is the original dependency tree
                    try {
                        DependencyTreeAnalyzer dependencyTreeAnalyzer = new DependencyTreeAnalyzer(child.getAbsolutePath());
                        String libraryName = child.getName().substring(0, child.getName().length() - 13);
                        int heightOriginal = dependencyTreeAnalyzer.heightOfDependencyTree();
                        if (dtMap.containsKey(libraryName)) {
                            dtMap.get(libraryName).setHeightOriginal(heightOriginal);
                        } else {
                            DtType dtType = new DtType();
                            dtType.setHeightOriginal(heightOriginal);
                            dtMap.put(libraryName, dtType);
                        }
                    } catch (Exception e) {
                        continue;
                    }

                }
            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }

        printMap(dtMap);

    }

    static class DtType {
        int heightOriginal = -1;
        int heightDebloated = -1;

        public int getHeightOriginal() {
            return heightOriginal;
        }

        public void setHeightOriginal(int heightOriginal) {
            this.heightOriginal = heightOriginal;
        }

        public int getHeightDebloated() {
            return heightDebloated;
        }

        public void setHeightDebloated(int heightDebloated) {
            this.heightDebloated = heightDebloated;
        }

        @Override
        public String toString() {
            return "{" +
                    "heightOriginal=" + heightOriginal +
                    ", heightDebloated=" + heightDebloated +
                    '}';
        }
    }

}
