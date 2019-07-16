package se.kth.jbroom.callgraph;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class JCallGraphModifiedTest {

    private JCallGraphModified jCallGraphModified;

    @Before
    public void setUp() throws Exception {
        jCallGraphModified = new JCallGraphModified();
    }

    @Test
    public void processFile() {

        jCallGraphModified.getAllMethodsCallsFromFile(
                "/home/cesarsv/Documents/papers/2019_papers/royal-debloat/jicocowrapper/experiments/dummy-project/target/classes")
                .forEach(System.out::println);

        int c;
    }

    @Test
    public void getAllUsedClasses() {

        jCallGraphModified.runUsageAnalysis(
                "/home/cesarsv/Documents/papers/2019_papers/royal-debloat/jicocowrapper/experiments/dummy-project/target/classes")
                .keySet()
                .forEach(System.out::println);
    }

    @Test
    public void getUsageAnalysis() {

        Map<String, Set<String>> usageAnalysis = jCallGraphModified.runUsageAnalysis(
                "/home/cesarsv/Documents/papers/2019_papers/royal-debloat/jicocowrapper/experiments/dummy-project/target/classes");

        System.out.println(usageAnalysis);
    }

}
