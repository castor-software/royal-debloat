package se.kth.jbroom.util;

import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CmdExec {

    /**
     * Creates a system process to execute an given Java class with its parameters via command line.
     * E.g, java -verbose:class -jar target/clitools-1.0.0-SNAPSHOT-jar-with-dependencies.jar whoami | grep "\[Loaded " | grep -v " from /usr/lib" | cut -d ' ' -f2 | sort > loaded-classes
     *
     * @param classPath
     * @param clazzFullyQualifiedName
     * @param parameters
     * @return the set of classes in the classpath that were loaded
     */
    public Set<String> execProcess(String classPath, String clazzFullyQualifiedName, String[] parameters) {

        Set<String> result = new HashSet<>();

        try {
            String line;
            String[] cmd = {"java",
                    "-verbose:class",
                    "-classpath",
                    classPath,
                    clazzFullyQualifiedName};

            cmd = ArrayUtils.addAll(cmd, parameters);

            System.out.print("Executing command: ");
            Arrays.asList(cmd).stream().forEach(s -> System.out.print(s + " "));
            System.out.println("\n");

            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.startsWith("[Loaded ") && line.endsWith(classPath + "/]")) {
                    result.add(line.split(" ")[1]);
                }
            }
            input.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        result.stream().forEach(s -> System.out.println("Loaded: " + s));

        return result;
    }
}