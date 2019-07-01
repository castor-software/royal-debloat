package se.kth.jbroom.wrapper;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class JacocoReportReaderTest {

 /*   private JacocoReportReader jacocoReportReader;

    @Before
    public void setUp() throws ParserConfigurationException {
        jacocoReportReader = new JacocoReportReader();
    }

    @Test
    public void testGetUnusedClassesAndMethods() throws IOException, SAXException {

        Map<String, Set<String>> u = jacocoReportReader.getUnusedClassesAndMethods(new File("experiments/clitools/report.xml"));

        System.out.println("#unused classes: " + u.entrySet().stream().filter(e -> e.getValue() == null).count());
        System.out.println("#unused methods: " + u.entrySet().stream().filter(e -> e.getValue() != null).map(e -> e.getValue()).mapToInt(s -> s.size()).sum());
        System.out.println("DOne");

//        Assert.assertEquals(u.entrySet().stream().filter(e -> e.getValue() == null).count(), 606);
//        Assert.assertEquals(u.entrySet().stream().filter(e -> e.getValue() != null).map(e -> e.getValue()).mapToInt(s -> s.size()).sum(), 9);
    }*/
}