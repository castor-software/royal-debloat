package se.kth;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class JacocoWrapper {
	static File mavenHome = new File("/usr/share/maven");
	static int testExecutionTimeOut = 5*60;//5 minutes in seconds

	File workingDir;
	File report;

	public boolean runMaven(List<String> goals, Properties properties) {
		File pomFile = new File(workingDir, "pom.xml");
		InvocationRequest request = new DefaultInvocationRequest();
		request.setBatchMode(true);
		request.setPomFile(pomFile);
		if(properties != null)
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


	public static void  main(String[] args) throws ParserConfigurationException, IOException, SAXException {
		//Init
		JacocoWrapper wrapper = new JacocoWrapper();
		wrapper.workingDir = new File("/home/nharrand/Documents/royal-debloat/jicocowrapper/experiments/dummy-project");
		wrapper.report = new File(wrapper.workingDir,"report.xml");
		if(wrapper.report.exists()) FileUtils.deleteQuietly(wrapper.report);

		wrapper.runMaven(Arrays.asList("clean", "compile"),null);
		wrapper.runMaven(Collections.singletonList("org.jacoco:jacoco-maven-plugin:0.8.4:instrument"),null);
		wrapper.runMaven(Collections.singletonList("test"),null);

		FileUtils.moveFile(new File(wrapper.workingDir,"jacoco.exec"),
				new File(wrapper.workingDir,"target/jacoco.exec"));

		wrapper.runMaven(Arrays.asList(
				"org.jacoco:jacoco-maven-plugin:0.8.4:restore-instrumented-classes",
				"org.jacoco:jacoco-maven-plugin:0.8.4:report"),null);

		FileUtils.moveFile(new File(wrapper.workingDir, "target/site/jacoco/jacoco.xml"),wrapper.report);

		JacocoReportReader reportReader = new JacocoReportReader();

		Map<String, Set<String>> u = reportReader.getUnusedClassesAndMethods(wrapper.report);

		System.out.println("#unused classes: " + u.entrySet().stream().filter(e -> e.getValue() == null).count());
		System.out.println("#unused methods: " + u.entrySet().stream().filter(e -> e.getValue() != null).map(e -> e.getValue()).mapToInt(s -> s.size()).sum());
	}
}
