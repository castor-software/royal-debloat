package se.kth.jbroom.debloat;

import org.junit.Before;
import org.xml.sax.SAXException;
import se.kth.jbroom.util.JarUtils;
import se.kth.jbroom.wrapper.JacocoWrapper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MethodDebloaterTest {

    File baseDir;
    String outputDirectory;

    @Before
    public void setUp() throws Exception {
        baseDir = new File("/home/cesarsv/Documents/papers/2019_papers/royal-debloat/jicocowrapper/experiments/dummy-project");
        outputDirectory = "/home/cesarsv/Documents/papers/2019_papers/royal-debloat/jicocowrapper/experiments/dummy-project/target/classes";
    }

    public void removeUnusedMethods() {
        JacocoWrapper jacocoWrapper = new JacocoWrapper(baseDir, new File(baseDir.getAbsolutePath() + "/report.xml"));
        Map<String, Set<String>> usageAnalysis = null;

        // run the usage analysis
        try {
            usageAnalysis = jacocoWrapper.analyzeUsages();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        // decompress the jar files in the output directory
        JarUtils.decompressJars(outputDirectory);

        Set<String> classesUsed = new HashSet<>();

        for (Map.Entry<String, Set<String>> entry : usageAnalysis.entrySet()) {
            if (entry.getValue() != null) {
                classesUsed.add(entry.getKey());
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
        }

        MethodDebloater methodDebloater = new MethodDebloater(outputDirectory, usageAnalysis);
        try {
            methodDebloater.removeUnusedMethods();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // print some results
        System.out.println("#unused classes: " + usageAnalysis.entrySet().stream().filter(e -> e.getValue() == null).count());
        System.out.println("#unused methods: " + usageAnalysis.entrySet().stream().filter(e -> e.getValue() != null).map(e -> e.getValue()).mapToInt(s -> s.size()).sum());

    }
}