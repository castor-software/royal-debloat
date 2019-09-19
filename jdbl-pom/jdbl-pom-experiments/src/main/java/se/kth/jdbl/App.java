package se.kth.jdbl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.test.plugin.BuildTool;
import org.apache.maven.shared.test.plugin.ProjectTool;
import org.apache.maven.shared.test.plugin.RepositoryTool;
import org.apache.maven.shared.test.plugin.TestToolsException;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import se.kth.jdbl.analysis.ClassFileVisitorUtils;
import se.kth.jdbl.analysis.ProjectDependencyAnalysis;
import se.kth.jdbl.analysis.ProjectDependencyAnalyzer;
import se.kth.jdbl.analysis.ProjectDependencyAnalyzerException;
import se.kth.jdbl.analysis.asm.DependencyClassFileVisitor;
import se.kth.jdbl.counter.ClassMembersVisitorCounter;
import se.kth.jdbl.tree.StandardTextVisitor;
import se.kth.jdbl.tree.analysis.DependencyTreeAnalyzer;
import se.kth.jdbl.util.*;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends PlexusTestCase {

    //-------------------------------/
    //-------- CLASS FIELD/S --------/
    //-------------------------------/

    private static BuildTool buildTool;
    private static File localRepo;
    private static ProjectTool projectTool;
    private static ProjectDependencyAnalyzer analyzer;
    private static String dependenciesDir;

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println("\t arg[0]: Path to file containing a list of GAVs");
        System.out.println("\t arg[1]: Path to directory to put the dependencies");
        System.out.println("\t arg[2]: Path to directory to put the results");
        System.out.println("\t arg[3]: Path to temporal directory used for analysis");
        System.out.println("\t arg[4]: r flag to restart the analysis form the last processed artifact");
    }

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    //-------------------------------/
    //------- PUBLIC METHOD/S -------/
    //-------------------------------/

    public static void main(String[] args) throws Exception {

        if (args[0].equals("-help")) {
            printHelp();
            System.exit(1);
        }

        dependenciesDir = args[1];

        App app = new App();
        app.setUp();

        // read the list of artifacts: "/home/cesarsv/Documents/xperiments/accumulo_versions_list.csv"
        BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));

        // directory to put the results files: "/home/cesarsv/Documents/xperiments/results/"
        String resultsDir = args[2];

        // temporal directory to put the artifact and its dependencies: "/home/cesarsv/Documents/xperiments/artifact/"
        String artifactDir = args[3];
        String dependenciesDir = localRepo.getAbsolutePath();

        BufferedWriter bwResults = new BufferedWriter(new FileWriter(resultsDir + "results.csv", true));
        BufferedWriter bwDescription = new BufferedWriter(new FileWriter(resultsDir + "description.csv", true));

        // option to restart the experiments
        if (args.length == 5 && args[4].equals("r")) {
            LOGGER.info("RESTARTING EXPERIMENTS FROM THE LAST PROCESSED ARTIFACT");

            // read the last processed artifact
            BufferedReader brLastProcessed = new BufferedReader(new FileReader(new File(resultsDir + "lastProcessed.txt")));
            String lastProcessedArtifact = brLastProcessed.readLine().split(",")[0];
            brLastProcessed.close();

            String artifact = "";
            while (!(artifact.equals(lastProcessedArtifact))) {
                artifact = br.readLine();
            }

            //read the next unprocessed artifact
            artifact = br.readLine();

            // read the list of artifacts' coordinates to be analyzed
            while (artifact != null) {
                String[] split = artifact.split(":");
                String groupId = split[0];
                String artifactId = split[1];
                String version = split[2];
                try {
                    app.execute(groupId, artifactId, version, resultsDir, artifactDir, dependenciesDir);
                } catch (Exception e) {
                    LOGGER.severe("Error while processing: " + artifact);
                    artifact = br.readLine();
                    continue;
                }
                artifact = br.readLine();
            }
            br.close();

        } else if (args.length == 4) {
            // write csv report headers
            bwDescription.write("Artifact,NbTypes,NbFields,NbMethods,NbAnnotations,IsMultimodule,Organization,Scm,License,HeightOriginalDT,HeightDebloatedDT" + "\n");
            bwResults.write("Artifact,AllDeps,Pack,Scope,Optional,Type,Used,Declared,Removable,NbTypes,NbFields,NbMethods,NbAnnotations,NbDeps,TreeLevel,InConflict,IsMultimodule,Licence,Organization" + "\n");
            bwResults.close();
            bwDescription.close();

            String artifact = br.readLine();
            // read the list of artifacts' coordinates to be analyzed
            while (artifact != null) {
//            artifact = artifact.substring(1, artifact.length() - 1);
                String[] split = artifact.split(":");
                String groupId = split[0];
                String artifactId = split[1];
                String version = split[2];

                BufferedWriter bwLastProcessed = new BufferedWriter(new FileWriter(resultsDir + "lastProcessed.txt"));
                bwLastProcessed.write(artifact);
                bwLastProcessed.close();

                try {
                    app.execute(groupId, artifactId, version, resultsDir, artifactDir, dependenciesDir);
                } catch (Exception e) {
                    LOGGER.severe("Error while processing: " + artifact);
                    artifact = br.readLine();
                    continue;
                }
                artifact = br.readLine();
            }
            br.close();

        }

    }

    public static BuildTool getBuildTool() {
        return buildTool;
    }

    public static File getLocalRepo() {
        return localRepo;
    }

    //-------------------------------/
    //------ PRIVATE METHOD/S -------/
    //-------------------------------/

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        buildTool = (BuildTool) lookup(BuildTool.ROLE);
        projectTool = (ProjectTool) lookup(ProjectTool.ROLE);
        analyzer = (ProjectDependencyAnalyzer) lookup(ProjectDependencyAnalyzer.ROLE);

        System.setProperty("maven.home", dependenciesDir);

        if (localRepo == null) {
            RepositoryTool repositoryTool = (RepositoryTool) lookup(RepositoryTool.ROLE);
            localRepo = repositoryTool.findLocalRepositoryDirectory();
            // set a custom local maven repository: "/home/cesarsv/Documents/xperiments/dependencies"
            localRepo = new File(dependenciesDir);
        }
    }

    private void execute(String groupId, String artifactId, String version, String resultsDir, String artifactDir, String dependenciesDir)
            throws TestToolsException, ProjectDependencyAnalyzerException, IOException, XmlPullParserException {

        MavenPluginInvoker mavenPluginInvoker = new MavenPluginInvoker();

        // remove the content of local directories
        FileUtils.cleanDirectory(new File(artifactDir));

        // set a size threshold of 10GB size (clean it if is larger that that)
        checkDependenciesDirSize(dependenciesDir, new BigInteger("53687091200")); // 50GB

        String coordinates = groupId + ":" + artifactId + ":" + version;

        LOGGER.info("---------------------------------------------------------------------------------------------");
        LOGGER.log(Level.INFO, () -> "Processing: " + coordinates);
        LOGGER.info("---------------------------------------------------------------------------------------------");

        // download the artifact pom
        LOGGER.info("downloading pom");
        PomDownloader.downloadPom(artifactDir, groupId, artifactId, version);

        // copy the artifact locally
        LOGGER.info("copying artifact");
        mavenPluginInvoker.copyArtifact(artifactDir + "pom.xml", coordinates, artifactDir);

        // decompress the artifact locally if the jar file exists
        File jarFile = new File(artifactDir + artifactId + "-" + version + ".jar");
        if (jarFile.exists()) {

            // get basic dependency info from the dependency tree
            LOGGER.info("getting dependency tree");
            String dependencyTreePath = artifactDir + "dependencyTree.txt";
            mavenPluginInvoker.copyDependencyTree(artifactDir + "pom.xml", coordinates, dependencyTreePath);

            if (new File(dependencyTreePath).exists()) {

                // resolve all the dependencies
                LOGGER.info("resolving dependencies");
                mavenPluginInvoker.resolveDependencies(artifactDir + "pom.xml", coordinates);

                // copy all the dependencies locally
                LOGGER.info("copying dependencies");
                mavenPluginInvoker.copyDependencies(artifactDir + "pom.xml", coordinates, dependenciesDir);

                LOGGER.info("decompressing jar");
                JarUtils.decompressJarFile(artifactDir + "target/classes/", artifactDir + artifactId + "-" + version + ".jar");

                // build the maven project with its dependencies from the local repository
                LOGGER.info("building maven project");

                MavenProject mavenProject = null;
                try {
                    mavenProject = projectTool.readProjectWithDependencies(new File(artifactDir + "pom.xml"), localRepo);
                } catch (Exception e) {
                    LOGGER.severe("Fail to build Maven project");
                }

                processDependencies(resultsDir, artifactDir, dependenciesDir, coordinates, dependencyTreePath, mavenProject);
            }
        }
    }

    private void processDependencies(String resultsDir, String artifactDir, String dependenciesDir, String coordinates, String dependencyTreePath, MavenProject mavenProject)
            throws ProjectDependencyAnalyzerException, IOException, XmlPullParserException {
        if (mavenProject != null) { // if is not null then the invoked project was build correctly

            Build build = new Build();
            build.setDirectory(artifactDir);
            mavenProject.setBuild(build);

            DependencyTreeAnalyzer dta = new DependencyTreeAnalyzer(dependencyTreePath);

            List<String> directDependencies = dta.getDirectDependencies();
            List<String> allDependencies = dta.getAllDependenciesCanonical(dta.getRootNode());

            LOGGER.info("analyzing dependencies usage");

            ProjectDependencyAnalysis actualAnalysis = analyzer.analyze(mavenProject);
            actualAnalysis.ignoreNonCompile();

            long nbVisitedTypes = ClassMembersVisitorCounter.getNbVisitedTypes();
            long nbVisitedFields = ClassMembersVisitorCounter.getNbVisitedFields();
            long nbVisitedMethods = ClassMembersVisitorCounter.getNbVisitedMethods();
            long nbVisitedAnnotations = ClassMembersVisitorCounter.getNbVisitedAnnotations();

            if (nbVisitedTypes > 0 && nbVisitedMethods > 0) {

                // used and declared dependencies"
                Set<Artifact> usedDeclaredDependencies = actualAnalysis.getUsedDeclaredArtifacts();

                // used but not undeclared dependencies
                Set<Artifact> usedUndeclaredDependencies = actualAnalysis.getUsedUndeclaredArtifacts();

                // manipulation of the pom file
                Model pomModel = PomManipulator.readModel(new File(artifactDir + "pom.xml"));

                List<String> artifactsInConflict = new ArrayList<>();
                ArrayList<MavenDependencyBuilder> dependencies = new ArrayList<>();

                // deep copy of the dependency tree object
                DependencyTreeAnalyzer dtaDebloated = (DependencyTreeAnalyzer) SerializationUtils.clone(dta);

                // label all the nodes
                dtaDebloated.labelNodes(usedDeclaredDependencies, usedUndeclaredDependencies);

                // remove unused and declared artifacts
                dtaDebloated.removeUnusedArtifacts();

                // save to a file
                StandardTextVisitor dtaDebloatedTree = new StandardTextVisitor();
                dtaDebloatedTree.visit(dtaDebloated.getRootNode());

                // process dependencies one by one
                for (String dep : allDependencies) {

                    String inConflict = "NO";

                    String originalDep = dep;

                    if (dep.startsWith("(")) {
                        dep = dep.substring(1, dep.length() - 1);
                        String[] tmpSplit = dep.split(" - ");
                        dep = tmpSplit[0];
                        inConflict = tmpSplit[1]
                                .replace(",", "[comma] ")
                                .replace(";", "[comma] ");
                    }

                    dep = dep.split(" ")[0];// manage the case "junit:junit:3.8.1:test (scope not updated to compile)"
                    String[] split = dep.split(":");
                    String g;
                    String a;
                    String t;
                    String v;
                    String s;
                    g = split[0];
                    if (split.length == 5) {
                        a = split[1];
                        t = split[2];
                        v = split[3];
                        s = split[4].split(" ")[0];
                    } else { // consider the case org.jacoco:org.jacoco.agent:jar:runtime:0.7.5.201505241946:test
                        a = split[1];
                        t = split[3];
                        v = split[4];
                        s = split[5].split(" ")[0];
                    }

                    boolean isOptional = false;
                    boolean isUsed = false;
                    boolean isDeclared = false;

                    for (Artifact usedDeclaredDependency : usedDeclaredDependencies) {
                        if (usedDeclaredDependency.toString().equals(dep)) {
                            isUsed = true;
                            isOptional = usedDeclaredDependency.isOptional();
                            break;
                        }
                    }

                    for (Artifact usedUndeclaredDependency : usedUndeclaredDependencies) {
                        if (usedUndeclaredDependency.toString().equals(dep)) {
                            isUsed = true;
                            isOptional = usedUndeclaredDependency.isOptional();
                            break;
                        }
                    }

                    List<Dependency> declaredDeps = pomModel.getDependencies();
                    for (Dependency declaredDep : declaredDeps) {
                        if (declaredDep.getGroupId().equals(g) &&
                                declaredDep.getArtifactId().equals(a)) {
                            isDeclared = true;
                            break;
                        }
                    }

                    // count bytecode class members
                    ClassMembersVisitorCounter.resetClassCounters();
                    File file;
                    if (split.length == 5) {
                        file = new File(dependenciesDir + "/" +
                                g.replace(".", "/") + "/" +
                                a + "/" +
                                v + "/" +
                                a + "-" +
                                v + ".jar");
                    } else { // consider the case org.jacoco:org.jacoco.agent:jar:runtime:0.7.5.201505241946:test
                        file = new File(dependenciesDir + "/" +
                                g.replace(".", "/") + "/" +
                                a + "/" +
                                v + "/" +
                                a + "-" +
                                v + "-" + t + ".jar");
                    }
                    if (file.exists()) {
                        URL url = file.toURI().toURL();
                        try {
                            ClassFileVisitorUtils.accept(url, new DependencyClassFileVisitor());
                        } catch (Exception e) {
                            ClassMembersVisitorCounter.markAsNotFoundClassCounters();
                            LOGGER.log(Level.WARNING, "Something was wrong with: " + file.getAbsolutePath());
                        }
                    } else {
                        ClassMembersVisitorCounter.markAsNotFoundClassCounters();
                    }

                    if (!inConflict.equals("NO")) {
                        artifactsInConflict.add(g + ":" + a + ":" + v);
                    }

                    // get the dependency POM
                    try {
                        String depPomPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4) + ".pom";
                        FileUtils.copyFile(new File(depPomPath), new File(artifactDir + "dependencyPom/pom.xml"));
                    } catch (IOException e) {
                        LOGGER.severe("Unable to get POM for dependency: " + g + ":" + a + ":" + v);
                    }

                    // get extra information from the dependency POM
                    MavenXpp3Reader reader = new MavenXpp3Reader();
                    Model model = reader.read(new FileReader(artifactDir + "dependencyPom/" + "pom.xml"));

                    // set parent in case of multi module projects
                    String parentCoordinates = "NO";
                    if (model.getParent() != null) {
//                        parentCoordinates = MavenDependencyUtils.toCoordinates(model.getParent().getGroupId(), model.getParent().getArtifactId(), model.getParent().getVersion());
                        parentCoordinates = "YES";

                    }

                    // set organization
                    Organization organizationObject = model.getOrganization();
                    String organization = "NA";
                    if (organizationObject != null && organizationObject.getName() != null) {
                        organization = organizationObject.getName().replaceAll(",", "[comma]").replaceAll("\n", " ");
                    }

                    // set licence
                    List<License> licencesList = model.getLicenses();
                    String licence = "NA";
                    if (!licencesList.isEmpty() && licencesList.get(0).getName() != null) {
                        licence = licencesList.get(0).getName().replaceAll(",", "[comma]").replaceAll("\n", " ");
                    }

                    MavenDependencyBuilder dependency = new MavenDependencyBuilder();
                    dependency
                            .setCoordinates(g + ":" + a + ":" + v)
                            .setType(t)
                            .setScope(s)
                            .isOptional(isOptional)
                            .setDependencyType((directDependencies.contains(originalDep)) ? "direct" : "transitive")
                            .isUsed(isUsed)
                            .isDeclared(isDeclared)
                            .isRemovable(!dtaDebloated.getAllDependenciesCoordinates(dtaDebloated.getRootNode()).contains(g + ":" + a + ":" + v))
                            .setTreeLevel(dta.getLevel(g, a, v))
                            .setNbTypes(ClassMembersVisitorCounter.getNbVisitedTypes())
                            .setNbFields(ClassMembersVisitorCounter.getNbVisitedFields())
                            .setNbMethods(ClassMembersVisitorCounter.getNbVisitedMethods())
                            .setNbAnnotations(ClassMembersVisitorCounter.getNbVisitedAnnotations())
                            .setNbDependencies(dta.getNumberOfDependenciesOfNode(g, a, v))
                            .inConflict(inConflict)
                            .setParent(parentCoordinates)
                            .setLicence(licence)
                            .setOrganization(organization);
                    // add dependencies to the data
                    dependencies.add(dependency);
                }

                // ********   Only happens if no exception occurred before *********** //

                LOGGER.info("writing artifact description and results for ===================> " + coordinates);

                // write description file
                CustomFileWriter.writeArtifactProperties(resultsDir + "description.csv", pomModel, coordinates, dta, dtaDebloated,
                        nbVisitedTypes, nbVisitedFields, nbVisitedMethods, nbVisitedAnnotations);

                // write results file
                CustomFileWriter.writeDependencyResults(resultsDir + "results.csv", coordinates, dependencies);

                // writing original DT
                FileUtils.copyFile(new File(artifactDir + "dependencyTree.txt"), new File(resultsDir + "trees/" + coordinates + "_original" + ".txt"));

                // writing debloated DT
                CustomFileWriter.writeDebloatedPom(dtaDebloatedTree, resultsDir + "trees/" + coordinates + "_debloated" + ".txt");
            }
        }
    }

    /**
     * Removes all files in the dependencies if the size of the directory is greater than a given value.
     *
     * @param dependenciesDir The directory with the dependencies
     * @param dirSize         The size threshold
     */
    private void checkDependenciesDirSize(String dependenciesDir, BigInteger dirSize) throws IOException {
        BigInteger dependencyFolderSize = FileUtils.sizeOfAsBigInteger(new File(dependenciesDir));
        if (dependencyFolderSize.compareTo(dirSize) > 0) {
            FileUtils.cleanDirectory(new File(dependenciesDir));
        }
    }
}
