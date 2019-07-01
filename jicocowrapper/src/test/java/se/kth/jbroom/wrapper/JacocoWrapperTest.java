package se.kth.jbroom.wrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class JacocoWrapperTest {

    /*private JacocoWrapper jacocoWrapper;

    @Before
    public void setUp() {
        File workingDir = new File("/home/cesarsv/Documents/papers/2019_papers/royal-debloat/jicocowrapper/experiments/dummy-project");
        File report = new File(workingDir, "report.xml");
        jacocoWrapper = new JacocoWrapper(workingDir, report);
    }

    @Test
    public void name() throws IOException, ParserConfigurationException, SAXException {

        Map<String, Set<String>> u = jacocoWrapper.analyzeUsages();

        System.out.println("#unused classes: " + u.entrySet().stream().filter(e -> e.getValue() == null).count());
        System.out.println("#unused methods: " + u.entrySet().stream().filter(e -> e.getValue() != null).map(e -> e.getValue()).mapToInt(s -> s.size()).sum());

//        Assert.assertEquals(u.entrySet().stream().filter(e -> e.getValue() == null).count(), 606);
//        Assert.assertEquals(u.entrySet().stream().filter(e -> e.getValue() != null).map(e -> e.getValue()).mapToInt(s -> s.size()).sum(), 9);
    }*/
}