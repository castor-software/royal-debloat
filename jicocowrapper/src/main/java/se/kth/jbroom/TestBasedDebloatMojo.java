package se.kth.jbroom;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.xml.sax.SAXException;
import se.kth.jbroom.debloat.AbstractMethodDebloat;
import se.kth.jbroom.debloat.TestBasedMethodDebloat;
import se.kth.jbroom.loader.LoaderCollector;
import se.kth.jbroom.loader.TestBasedClassLoader;
import se.kth.jbroom.util.FileUtils;
import se.kth.jbroom.util.JarUtils;
import se.kth.jbroom.util.MavenUtils;
import se.kth.jbroom.wrapper.InvocationTypeEnum;
import se.kth.jbroom.wrapper.JacocoWrapper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This Mojo instruments the project according to the coverage of its test suite.
 * Probes are inserted in order to keep track of the classes and methods used.
 * Non covered elements are removed from the final jar file.
 */
@Mojo(name = "test-based-debloat", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class TestBasedDebloatMojo extends AbstractMojo {

    private ArrayList<String> tests = new ArrayList<>();

    private File mavenHome = new File("/usr/share/maven");

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() {

        String testOutputDirectory = project.getBuild().getTestOutputDirectory();
        String outputDirectory = project.getBuild().getOutputDirectory();
        File baseDir = project.getBasedir();

        getLog().info("***** STARTING DEBLOAT FROM TEST_DEBLOAT COVERAGE *****");

        MavenUtils mavenUtils = new MavenUtils(mavenHome, baseDir);

        // copy the dependencies
        mavenUtils.copyDependencies(outputDirectory);

        // copy the resources
        mavenUtils.copyResources(outputDirectory);

        // decompress the copied dependencies
        JarUtils.decompressJars(outputDirectory);

        /****************************************************************************************/

        ClassLoader cl = null;
        Class clazz;
        Class junitCore;

        try {
            Class arrayClass = Class.forName("[Ljava.lang.Class;");

            // Create a new class loader with the directory
            cl = new TestBasedClassLoader(testOutputDirectory, outputDirectory, TestBasedDebloatMojo.class.getClassLoader());

            Thread.currentThread().setContextClassLoader(cl);

            ArrayList<String> testsFiles = findTestFiles(testOutputDirectory);

            getLog().info("Running JUnit tests");
            getLog().info("Number of test classes: " + testsFiles.size());

            // TODO improve this by calling the maven surefire plugin directly instead of running the tests one by one
            // Execute all the test files
            for (String test : testsFiles) {
                // Load the test ClassLoader
                clazz = cl.loadClass(test);
                // Invoke the test cases
                junitCore = cl.loadClass("org.junit.runner.JUnitCore");
                Method methodRunClasses = junitCore.getMethod("runClasses", arrayClass);
                methodRunClasses.invoke(null, new Object[]{new Class[]{clazz}});
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                ClassNotFoundException e) {
            getLog().error("Error: " + e);
        }

        Set<String> classesLoaded = new HashSet<>();

        while (cl != null) {
            getLog().info("ClassLoader: " + cl);
            try {
                for (Iterator iter = LoaderCollector.list(cl); iter.hasNext(); ) {
                    String classLoaded = iter.next().toString();
                    classesLoaded.add(classLoaded.split(" ")[1]);

                    if (cl.toString().startsWith("se.kth.jbroom.loader")) {
                        getLog().info("\t" + classLoaded);
                    }

                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                getLog().error("Error: " + e);
            }
            cl = cl.getParent();
        }

        /***************************************************************************/

        JacocoWrapper jacocoWrapper = new JacocoWrapper(project, new File(project.getBasedir().getAbsolutePath() + "/report.xml"), InvocationTypeEnum.TEST_DEBLOAT);
        Map<String, Set<String>> usageAnalysis = null;

        // run the usage analysis
        try {
            usageAnalysis = jacocoWrapper.analyzeUsages();
            // print some results
            getLog().info("#unused classes: " + usageAnalysis.entrySet().stream().filter(e -> e.getValue() == null).count());
            getLog().info("#unused methods: " + usageAnalysis.entrySet().stream().filter(e -> e.getValue() != null).map(e -> e.getValue()).mapToInt(s -> s.size()).sum());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        Set<String> classesUsed = new HashSet<>();

        for (Map.Entry<String, Set<String>> entry : usageAnalysis.entrySet()) {
            if (entry.getValue() != null) {
                classesUsed.add(entry.getKey());
                getLog().info(entry.getKey() + " = " + entry.getValue() + "\n");
            }
        }

        FileUtils fileUtils = new FileUtils(outputDirectory, new HashSet<>(), classesLoaded);
        try {
            fileUtils.deleteUnusedClasses(outputDirectory);
        } catch (IOException e) {
            getLog().error("Error: " + e);
        }

        AbstractMethodDebloat entryPointMethodDebloater = new TestBasedMethodDebloat(outputDirectory, usageAnalysis);
        try {
            entryPointMethodDebloater.removeUnusedMethods();
        } catch (IOException e) {
            getLog().error("Error: " + e);
        }

        getLog().info("Classes used: " + classesUsed.size() + ", " + "Classes unused: " + fileUtils.getNbClassesRemoved() + ", " + "Total: " + classesUsed.size() + fileUtils.getNbClassesRemoved());

        getLog().info("DEBLOAT FROM TESTS SUCCESS");
    }

    /**
     * Recursively search class files in a directory.
     *
     * @param testOutputDirectory
     * @return the name of tests files present in a given directory.
     */
    private ArrayList<String> findTestFiles(String testOutputDirectory) {
        File f = new File(testOutputDirectory);
        File[] list = f.listFiles();
        assert list != null;
        for (File testFile : list) {
            if (testFile.isDirectory()) {
                findTestFiles(testFile.getAbsolutePath());
            } else if (testFile.getName().endsWith(".class")) {
                String testName = testFile.getAbsolutePath();
                // Get the binary name of the test file
                tests.add(testName.replaceAll("/", ".")
                        .substring(project.getBuild().getTestOutputDirectory().length() + 1, testName.length() - 6));
            }
        }
        return tests;
    }
}
