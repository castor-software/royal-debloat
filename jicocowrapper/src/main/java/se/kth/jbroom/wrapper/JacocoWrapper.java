package se.kth.jbroom.wrapper;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;
import se.kth.jbroom.reflection.MethodInvoker;
import se.kth.jbroom.util.MavenUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class JacocoWrapper {

    static File mavenHome = new File("/usr/share/maven");

    private File baseDir;
    private File report;

    private InvocationType invocationType;

    private ClassLoader cl;
    private String entryClass;
    private String entryMethod;
    private String entryParameters;

    /**
     * Constructor
     */
    public JacocoWrapper(File baseDir, File report, InvocationType invocationType) {
        this.baseDir = baseDir;
        this.report = report;
        this.invocationType = invocationType;

        if (report.exists()) {
            FileUtils.deleteQuietly(report);
        }
    }

    public JacocoWrapper(File baseDir, File report, InvocationType invocationType, String entryClass, String entryMethod, String entryParameters) {
        this.baseDir = baseDir;
        this.report = report;
        this.invocationType = invocationType;
        this.entryClass = entryClass;
        this.entryMethod = entryMethod;
        this.entryParameters = entryParameters;

        if (report.exists()) {
            FileUtils.deleteQuietly(report);
        }
    }

    public Map<String, Set<String>> analyzeUsages() throws IOException, ParserConfigurationException, SAXException {

//        runMaven(Arrays.asList("clean", "compile"), null);

        MavenUtils mavenUtils = new MavenUtils(mavenHome, baseDir);

        Properties pro2 = new Properties();
        pro2.setProperty("mdep.outputFile", baseDir.getAbsolutePath() + "/test-classpath");
        pro2.setProperty("scope", "test");
        mavenUtils.runMaven(Collections.singletonList("dependency:build-classpath"), pro2);


        Properties pro = new Properties();
        pro.setProperty("outputDirectory", baseDir.getAbsolutePath() + "/target/classes");
        pro.setProperty("includeScope", "compile");

//        mavenUtils.runMaven(Collections.singletonList("dependency:copy-dependencies"), pro );
//        JarUtils.decompressJars(baseDir.getAbsolutePath() + "/target/classes");

        mavenUtils.runMaven(Collections.singletonList("org.jacoco:jacoco-maven-plugin:0.8.4:instrument"), null);

        switch (invocationType) {
            case TEST:
                mavenUtils.runMaven(Collections.singletonList("test"), null);
                break;
            case ENTRY_POINT:
                try {
                    URLClassLoader urlClassLoader = createClassLoader(new File(baseDir, "test-classpath"));
                    MethodInvoker.invokeMethod(urlClassLoader, entryClass, entryMethod, entryParameters);
                } catch (IOException e) {
                    System.err.println("ERRRRRRRRRRORRRRRRRRRRR");
                    e.printStackTrace();
                }
                break;
        }

        FileUtils.moveFile(new File(baseDir, "jacoco.exec"), new File(baseDir, "target/jacoco.exec"));

        mavenUtils.runMaven(Arrays.asList(
                "org.jacoco:jacoco-maven-plugin:0.8.4:restore-instrumented-classes",
                "org.jacoco:jacoco-maven-plugin:0.8.4:report"), null);

        FileUtils.moveFile(new File(baseDir, "target/site/jacoco/jacoco.xml"), report);

        JacocoReportReader reportReader = new JacocoReportReader();

        return reportReader.getUnusedClassesAndMethods(report);
    }

    private URLClassLoader createClassLoader(File in) throws IOException {
        System.out.println("Absolute: " + in.getAbsolutePath());

        BufferedReader buffer = new BufferedReader(new FileReader(in));
        String rawFile = baseDir.getAbsolutePath() + "/target/classes/:";
        String line = null;
        while ((line = buffer.readLine()) != null) {
            rawFile += line;
        }
        URL[] urls = Arrays.stream(rawFile.split(":"))
                .map(str -> {
                    try {
                        //System.out.println("add to classpath: " + str);
                        return new URL("file://" + str);
                    } catch (MalformedURLException e) {
                        System.err.println("failed to add to classpath: " + str);
                        return null;
                    }
                })
                .filter(url -> url != null)
                .toArray(URL[]::new);

        for(int i =0; i < urls.length; i++) System.out.println("url: " + urls[i].getPath());


        return new URLClassLoader(urls);
    }


}
