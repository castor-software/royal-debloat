package se.kth.jbroom;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import se.kth.jbroom.callgraph.JCallGraphModified;
import se.kth.jbroom.debloat.AbstractMethodDebloat;
import se.kth.jbroom.debloat.ConservativeMethodDebloat;
import se.kth.jbroom.util.FileUtils;
import se.kth.jbroom.util.JarUtils;
import se.kth.jbroom.util.MavenUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO document this
 */
@Mojo(name = "conservative-debloat", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class ConservativeDebloatMojo extends AbstractMojo {

    //--------------------------------/
    //-------- CLASS FIELD/S --------/
    //------------------------------/

    private static final File mavenHome = new File("/usr/share/maven");

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    //--------------------------------/
    //------- PUBLIC METHOD/S -------/
    //------------------------------/

    @Override
    public void execute() {

        String outputDirectory = project.getBuild().getOutputDirectory();
        File baseDir = project.getBasedir();

        getLog().info("***** STARTING CONSERVATIVE DEBLOAT *****");

        MavenUtils mavenUtils = new MavenUtils(mavenHome, baseDir);

        // copy the dependencies
        mavenUtils.copyDependencies(outputDirectory);

        // copy the resources
        mavenUtils.copyResources(outputDirectory);

        // decompress the copied dependencies
        JarUtils.decompressJars(outputDirectory);

        JCallGraphModified jCallGraphModified = new JCallGraphModified();

        // run de static usage analysis
        Map<String, Set<String>> usageAnalysis = jCallGraphModified.runUsageAnalysis(project.getBuild().getOutputDirectory());
        Set<String> classesUsed = usageAnalysis.keySet();

        // delete unused classes
        FileUtils fileUtils = new FileUtils(outputDirectory, new HashSet<>(), classesUsed);
        try {
            fileUtils.deleteUnusedClasses(outputDirectory);
        } catch (IOException e) {
            getLog().error("Error deleting unused classes: " + e);
        }

        // delete unused methods
        AbstractMethodDebloat conservativeMethodDebloat = new ConservativeMethodDebloat(outputDirectory, usageAnalysis);
        try {
            conservativeMethodDebloat.removeUnusedMethods();
        } catch (IOException e) {
            getLog().error("Error: " + e);
        }

        getLog().info("***** CONSERVATIVE DEBLOAT SUCCESS *****");
    }
}