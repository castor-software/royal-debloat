package se.kth.jbroom.wrapper;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class JacocoWrapper {

    static File mavenHome = new File("/usr/share/maven");
    static int testExecutionTimeOut = 10 * 60; // 10 minutes in seconds

    File workingDir;
    File report;

    public JacocoWrapper(File workingDir, File report) {
        this.workingDir = workingDir;
        this.report = report;

        if (report.exists()) {
            FileUtils.deleteQuietly(report);
        }
    }

    public Map<String, Set<String>> analyzeUsages() throws IOException, ParserConfigurationException, SAXException {

//        runMaven(Arrays.asList("clean", "compile"), null);

        Properties pro = new Properties();
        pro.setProperty("outputDirectory", workingDir.getAbsolutePath() + "/target/classes");
        pro.setProperty("includeScope", "compile");

//        runMaven(Collections.singletonList("dependency:copy-dependencies"), pro );
//        JarUtils.decompressJars(workingDir.getAbsolutePath() + "/target/classes");

        runMaven(Collections.singletonList("org.jacoco:jacoco-maven-plugin:0.8.4:instrument"), null);
        runMaven(Collections.singletonList("test"), null);

        FileUtils.moveFile(new File(workingDir, "jacoco.exec"), new File(workingDir, "target/jacoco.exec"));

        runMaven(Arrays.asList(
                "org.jacoco:jacoco-maven-plugin:0.8.4:restore-instrumented-classes",
                "org.jacoco:jacoco-maven-plugin:0.8.4:report"), null);

        FileUtils.moveFile(new File(workingDir, "target/site/jacoco/jacoco.xml"), report);

        JacocoReportReader reportReader = new JacocoReportReader();

        return reportReader.getUnusedClassesAndMethods(report);
    }

    private boolean runMaven(List<String> goals, Properties properties) {
        File pomFile = new File(workingDir, "pom.xml");
        InvocationRequest request = new DefaultInvocationRequest();
        request.setBatchMode(true);
        request.setPomFile(pomFile);
        if (properties != null)
            request.setProperties(properties);
        request.setGoals(goals);
        request.getOutputHandler(s -> System.out.println(s));
        request.getErrorHandler(s -> System.out.println(s));
        request.setTimeoutInSeconds(testExecutionTimeOut);

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
