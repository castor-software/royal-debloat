package se.kth.jbroom.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.*;
import org.xml.sax.SAXException;
import se.kth.jbroom.debloat.MethodDebloater;
import se.kth.jbroom.loader.TestBasedClassLoader;
import se.kth.jbroom.util.FileUtils;
import se.kth.jbroom.util.JarUtils;
import se.kth.jbroom.wrapper.JacocoWrapper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>
 * This Mojo instruments the project according to the coverage of its test suite.
 * Probes are inserted in order to keep track of the classes and methods used.
 * Non covered elements are removed from the final jar file.
 * </p>
 */
@Mojo(name = "test-based-debloat", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class TestBasedDebloaterMojo extends AbstractMojo {

    private ArrayList<String> tests = new ArrayList<>();

    private File mavenHome = new File("/usr/share/maven");

    private static final String INSTRUMENTED_SUFFIX = "-instrumented";
    private static final String DEBLOATED_SUFFIX = "-debloated";

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() {

        String testOutputDirectory = project.getBuild().getTestOutputDirectory();
        String outputDirectory = project.getBuild().getOutputDirectory();

        getLog().info("***** DEBLOAT FROM TEST COVERAGE STARTED *****");
        // get the list of classes loaded
        // java -verbose:class -jar target/clitools-1.0.0-SNAPSHOT-jar-with-dependencies.jar whoami | grep "\[Loaded " | grep -v " from /usr/lib" | cut -d ' ' -f2 | sort > loaded-classes

        Properties pro = new Properties();
        pro.setProperty("outputDirectory", outputDirectory);
        pro.setProperty("includeScope", "compile");

        runMaven(Collections.singletonList("dependency:copy-dependencies"), pro, project.getBasedir());

        JarUtils.decompressJars(outputDirectory);

        /***************************************************************************/

        JacocoWrapper jacocoWrapper = new JacocoWrapper(project.getBasedir(), new File(project.getBasedir().getAbsolutePath() + "/report.xml"));
        Map<String, Set<String>> usageAnalysis = null;

        // run the usage analysis
        try {
            usageAnalysis = jacocoWrapper.analyzeUsages();
            // print some results
            System.out.println("#unused classes: " + usageAnalysis.entrySet().stream().filter(e -> e.getValue() == null).count());
            System.out.println("#unused methods: " + usageAnalysis.entrySet().stream().filter(e -> e.getValue() != null).map(e -> e.getValue()).mapToInt(s -> s.size()).sum());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        Set<String> classesUsed = new HashSet<>();

        for (Map.Entry<String, Set<String>> entry : usageAnalysis.entrySet()) {
            if (entry.getValue() != null) {
                classesUsed.add(entry.getKey());
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
        }

        /****************************************************************************************/

        ClassLoader cl = null;
        Class clazz;
        Class junitCore;

        try {
            Class arrayClass = Class.forName("[Ljava.lang.Class;");

            // Create a new class loader with the directory
            cl = new TestBasedClassLoader(testOutputDirectory, outputDirectory, TestBasedDebloaterMojo.class.getClassLoader());

            Thread.currentThread().setContextClassLoader(cl);

            ArrayList<String> testsFiles = findTestFiles(testOutputDirectory);

            System.out.println("Number of test classes: " + testsFiles.size());

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
            System.out.println("Error: " + e);
        }

        Set<String> classesLoaded = new HashSet<>();

        while (cl != null) {
            System.out.println("ClassLoader: " + cl);
            try {
                for (Iterator iter = list(cl); iter.hasNext(); ) {
                    String classLoaded = iter.next().toString();
                    classesLoaded.add(classLoaded.split(" ")[1]);
                    System.out.println("\t" + classLoaded);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            cl = cl.getParent();
        }

        FileUtils fileUtils = new FileUtils(outputDirectory, new HashSet<String>(), classesLoaded);
        try {
            fileUtils.deleteUnusedClasses(outputDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

       /* List<String> cmdList;
        try {
            Arrays.asList("java", "-verbose:class");

            cmdList = new ArrayList();
            cmdList.add("C:\\Program Files\\Java\\jdk1.8.0_111\\bin\\javap.exe");
            cmdList.add("-c");
            cmdList.add("D:\\First.class");

            // Constructing ProcessBuilder with List as argument
            ProcessBuilder pb = new ProcessBuilder(cmdList);

            Process p = pb.start();
            p.waitFor();
            InputStream fis = p.getInputStream();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }*/

        MethodDebloater methodDebloater = new MethodDebloater(outputDirectory, usageAnalysis);
        try {
            methodDebloater.removeUnusedMethods();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Classes used: " + classesUsed.size() + ", " + "Classes unused: " + fileUtils.getNbClassesRemoved() + ", " + "Total: " + classesUsed.size() + fileUtils.getNbClassesRemoved());

//
//        // Logs to standard output
//        System.out.println("Number of classes instrumented: " + "x");
//        System.out.println("Number of classes removed: " + "x");
//        System.out.println("Number of methods removed: " + "X");
//
//        // Delete the classesUsed directory
//        FileUtils.renameFolder(inputDirectory, inputDirectory + "-original");
//
//        // Rename the classesUsed-debloated directory to classesUsed
//        FileUtils.renameFolder(inputDirectory + DEBLOATED_SUFFIX, inputDirectory);

        getLog().info("DEBLOAT FROM TESTS SUCCESS");
    }

    private Iterator list(ClassLoader classLoader) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Class CL_class = classLoader.getClass();
        while (CL_class != java.lang.ClassLoader.class) {
            CL_class = CL_class.getSuperclass();
        }
        java.lang.reflect.Field ClassLoader_classes_field = CL_class
                .getDeclaredField("classes");
        ClassLoader_classes_field.setAccessible(true);
        Vector classes = (Vector) ClassLoader_classes_field.get(classLoader);
        return classes.iterator();
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

    private boolean runMaven(List<String> goals, Properties properties, File workingDir) {
        File pomFile = new File(workingDir, "pom.xml");
        InvocationRequest request = new DefaultInvocationRequest();
        request.setBatchMode(true);
        request.setPomFile(pomFile);
        if (properties != null)
            request.setProperties(properties);
        request.setGoals(goals);
        request.getOutputHandler(s -> System.out.println(s));
        request.getErrorHandler(s -> System.out.println(s));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(mavenHome);
        invoker.setWorkingDirectory(workingDir);
        invoker.setErrorHandler(s -> System.out.println(s));
        invoker.setOutputHandler(s -> System.out.println(s));
        try {
            InvocationResult result = invoker.execute(request);
            return result.getExitCode() == 0;
        } catch (MavenInvocationException e) {
            return false;
        }
    }
}
