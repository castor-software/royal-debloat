package se.kth.jbroom;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.xml.sax.SAXException;
import se.kth.jbroom.debloat.MethodDebloater;
import se.kth.jbroom.loader.EntryPointClassLoader;
import se.kth.jbroom.loader.LoaderCollector;
import se.kth.jbroom.util.FileUtils;
import se.kth.jbroom.util.JarUtils;
import se.kth.jbroom.util.MavenUtils;
import se.kth.jbroom.wrapper.InvocationType;
import se.kth.jbroom.wrapper.JacocoWrapper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * <p>
 * TODO write mojo description
 * </p>
 */
@Mojo(name = "entry-point-debloat", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class EntryPointDebloaterMojo extends AbstractMojo {

    private File mavenHome = new File("/usr/share/maven");

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(property = "entry.class", name = "entryClass", required = true)
    private String entryClass = "";

    @Parameter(property = "entry.method", name = "entryMethod", required = true)
    private String entryMethod = "";

    @Parameter(property = "entry.parameters", name = "entryParameters", defaultValue = " ")
    private String entryParameters = null;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        String outputDirectory = project.getBuild().getOutputDirectory();
        File baseDir = project.getBasedir();

        getLog().info("***** STARTING DEBLOAT FROM ENTRY POINT *****");

        MavenUtils mavenUtils = new MavenUtils(mavenHome, baseDir);

        // copy the dependencies
        Properties copyDependenciesProperties = new Properties();
        copyDependenciesProperties.setProperty("outputDirectory", outputDirectory);
        copyDependenciesProperties.setProperty("includeScope", "compile");
        mavenUtils.runMaven(Collections.singletonList("dependency:copy-dependencies"), copyDependenciesProperties);

        // copy the resources
        Properties copyResourcesProperties = new Properties();
        copyResourcesProperties.setProperty("outputDirectory", outputDirectory + "/resources");
        mavenUtils.runMaven(Collections.singletonList("resources:resources"), copyResourcesProperties);

        JarUtils.decompressJars(outputDirectory);

        /****************************************************************************************/

        ClassLoader entryPointClassLoader = null;
        try {
            entryPointClassLoader = new EntryPointClassLoader(outputDirectory, EntryPointClassLoader.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(entryPointClassLoader);

            String className = entryClass;
            Class entryClassLoaded = entryPointClassLoader.loadClass(className);

            Method entryMethodLoaded = entryClassLoaded.getDeclaredMethod(entryMethod, String[].class);

            if (this.entryParameters != null) {
                String[] parameters = entryParameters.split(" ");

                // start of logging block
                getLog().info("Invoking method {" + entryMethodLoaded.getName() + "} in class {" + entryClassLoaded.getName() + "} with parameters {");
                for (int i = 0; i < parameters.length - 1; i++) {
                    getLog().info(parameters[i] + ", ");
                }
                getLog().info(parameters[parameters.length - 1] + "}\n");
                // end of logging block

                entryMethodLoaded.invoke(null, (Object) parameters);
            } else {
                entryMethodLoaded.invoke(null, new Object[]{});
            }
        } catch (IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                ClassNotFoundException e) {
            getLog().error(e);
        }

        Set<String> classesLoaded = new HashSet<>();

        while (entryPointClassLoader != null) {
            getLog().info("ClassLoader: " + entryPointClassLoader);
            try {
                for (Iterator iter = LoaderCollector.list(entryPointClassLoader); iter.hasNext(); ) {
                    String classLoaded = iter.next().toString();
                    classesLoaded.add(classLoaded.split(" ")[1]);

                    if (entryPointClassLoader.toString().startsWith("se.kth.jbroom.loader")) {
                        getLog().info("\t" + classLoaded);
                    }

                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                getLog().error("Error: " + e);
            }
            entryPointClassLoader = entryPointClassLoader.getParent();
        }

        /***************************************************************************/

        JacocoWrapper jacocoWrapper = new JacocoWrapper(project.getBasedir(),
                new File(project.getBasedir().getAbsolutePath() + "/report.xml"),
                InvocationType.ENTRY_POINT,
                entryClass,
                entryMethod,
                entryParameters);

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

        FileUtils fileUtils = new FileUtils(outputDirectory, new HashSet<String>(), classesLoaded);
        try {
            fileUtils.deleteUnusedClasses(outputDirectory);
        } catch (IOException e) {
            getLog().error("Error: " + e);
        }

        MethodDebloater methodDebloater = new MethodDebloater(outputDirectory, usageAnalysis);
        try {
            methodDebloater.removeUnusedMethods();
        } catch (IOException e) {
            getLog().error("Error: " + e);
        }

        getLog().info("Classes used: " + classesUsed.size() + ", " + "Classes unused: " + fileUtils.getNbClassesRemoved() + ", " + "Total: " + ((int) (classesUsed.size() + fileUtils.getNbClassesRemoved())));

        getLog().info("***** DEBLOAT FROM FROM ENTRY POINT SUCCESS *****");

    }


}
