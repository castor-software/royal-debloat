package se.kth.jbroom;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.xml.sax.SAXException;
import se.kth.jbroom.debloat.AbstractMethodDebloat;
import se.kth.jbroom.debloat.EntryPointMethodDebloat;
import se.kth.jbroom.util.ClassesLoadedSingleton;
import se.kth.jbroom.util.FileUtils;
import se.kth.jbroom.util.JarUtils;
import se.kth.jbroom.util.MavenUtils;
import se.kth.jbroom.wrapper.InvocationTypeEnum;
import se.kth.jbroom.wrapper.JacocoWrapper;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This Maven mojo instruments the project according to an entry point given as parameters in its configuration.
 * Probes are inserted in order to keep track of the classes and methods used.
 * Non covered elements are removed from the final jar file.
 */
@Mojo(name = "entry-point-debloat", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class EntryPointDebloatMojo extends AbstractMojo {

    //--------------------------------/
    //-------- CLASS FIELD/S --------/
    //------------------------------/

    private static final File mavenHome = new File("/usr/share/maven");

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(property = "entry.class", name = "entryClass", required = true)
    private String entryClass = "";

    @Parameter(property = "entry.method", name = "entryMethod", required = true)
    private String entryMethod = "";

    @Parameter(property = "entry.parameters", name = "entryParameters", defaultValue = " ")
    private String entryParameters = null;

    //--------------------------------/
    //------- PUBLIC METHOD/S -------/
    //------------------------------/

    @Override
    public void execute() {

        String outputDirectory = project.getBuild().getOutputDirectory();
        File baseDir = project.getBasedir();

        getLog().info("***** STARTING DEBLOAT FROM ENTRY POINT *****");

        MavenUtils mavenUtils = new MavenUtils(mavenHome, baseDir);

        // copy the dependencies
        mavenUtils.copyDependencies(outputDirectory);

        // copy the resources
        mavenUtils.copyResources(outputDirectory);

        // decompress the copied dependencies
        JarUtils.decompressJars(outputDirectory);

        // getting the used methods
        JacocoWrapper jacocoWrapper = new JacocoWrapper(
                project,
                new File(project.getBasedir().getAbsolutePath() + "/target/report.xml"),
                InvocationTypeEnum.ENTRY_POINT,
                entryClass,
                entryMethod,
                entryParameters,
                mavenHome);

        Map<String, Set<String>> usageAnalysis = null;

        // run the usage analysis
        try {
            usageAnalysis = jacocoWrapper.analyzeUsages();
            // print some results
            getLog().info("#Unused classes: " + usageAnalysis.entrySet().stream().filter(e -> e.getValue() == null).count());
            getLog().info("#Unused methods: " + usageAnalysis.entrySet().stream().filter(e -> e.getValue() != null).map(Map.Entry::getValue).mapToInt(Set::size).sum());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            getLog().error(e);
        }

        // delete unused classes
        FileUtils fileUtils = new FileUtils(outputDirectory, new HashSet<>(), ClassesLoadedSingleton.INSTANCE.getClassesLoaded());
        try {
            fileUtils.deleteUnusedClasses(outputDirectory);
        } catch (IOException e) {
            getLog().error("Error: " + e);
        }

        // delete unused methods
        AbstractMethodDebloat entryPointMethodDebloater = new EntryPointMethodDebloat(outputDirectory, usageAnalysis);
        try {
            entryPointMethodDebloater.removeUnusedMethods();
        } catch (IOException e) {
            getLog().error("Error: " + e);
        }


/*
        // getting the loaded classes
        CmdExec cmdExec = new CmdExec();
        getLog().info("Output directory: " + outputDirectory);
        getLog().info("entryClass: " + entryClass);
        getLog().info("entryParameters: " + entryParameters);
        Set<String> classesLoaded = cmdExec.execProcess(outputDirectory, entryClass, entryParameters.split(" "));

        // delete unused classes
        FileUtils fileUtils = new FileUtils(outputDirectory, new HashSet<>(), classesLoaded);
        try {
            fileUtils.deleteUnusedClasses(outputDirectory);
        } catch (IOException e) {
            getLog().error("Error: " + e);
        }*/

        getLog().info("***** DEBLOAT FROM FROM ENTRY POINT SUCCESS *****");
    }
}