package se.kth.jbroom.util;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class CmdExecTest {

    CmdExec cmdExec;

    @Before
    public void setUp() throws Exception {
        cmdExec = new CmdExec();
    }

    @Test
    public void linuxExec() {
        Set<String> result = cmdExec.execProcess("/home/cesarsv/Documents/papers/2019_papers/royal-debloat/jicocowrapper/experiments/dummy-project/target/classes/",
                "bag.BagClass", new String[]{"1", "2"});
        result.stream().forEach(s -> System.out.println(s));

    }
}