package se.kth.jbroom.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class MavenUtils {

    //--------------------------------/
    //-------- CLASS FIELD/S --------/
    //------------------------------/

    private static final int TEST_EXECUTION_TIMEOUT = 10 * 60; // 10 minutes in seconds
    private File mavenHome;
    private File workingDir;


    private static final Logger LOGGER = LogManager.getLogger(MavenUtils.class.getName());


    //--------------------------------/
    //-------- CONSTRUCTOR/S --------/
    //------------------------------/

    public MavenUtils(File mavenHome, File workingDir) {
        this.mavenHome = mavenHome;
        this.workingDir = workingDir;
    }

    //--------------------------------/
    //------- PUBLIC METHOD/S -------/
    //------------------------------/

    public void copyDependencies(String outputDirectory) {
        Properties copyDependenciesProperties = new Properties();
        copyDependenciesProperties.setProperty("outputDirectory", outputDirectory);
        copyDependenciesProperties.setProperty("includeScope", "compile");
        runMaven(Collections.singletonList("dependency:copy-dependencies"), copyDependenciesProperties);
    }

    public void copyResources(String outputDirectory) {
        Properties copyResourcesProperties = new Properties();
        copyResourcesProperties.setProperty("outputDirectory", outputDirectory + "/resources");
        runMaven(Collections.singletonList("resources:resources"), copyResourcesProperties);
    }

    public void runMaven(List<String> goals, Properties properties) {
        File pomFile = new File(workingDir, "pom.xml");
        InvocationRequest request = new DefaultInvocationRequest();
        request.setBatchMode(true);
        request.setPomFile(pomFile);
        if (properties != null)
            request.setProperties(properties);
        request.setGoals(goals);
        // request.getOutputHandler(s -> System.out.println(s));
        // request.getErrorHandler(s -> System.out.println(s));
        request.setTimeoutInSeconds(TEST_EXECUTION_TIMEOUT);

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(mavenHome);
        invoker.setWorkingDirectory(workingDir);
        // invoker.setErrorHandler(s -> System.out.println(s));
        // invoker.setOutputHandler(s -> System.out.println(s));
        try {
            InvocationResult result = invoker.execute(request);
            result.getExitCode();
        } catch (MavenInvocationException e) {
            LOGGER.error("Unable to run Maven: " + e);
        }
    }
}
