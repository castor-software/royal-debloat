package SJmp3;

import static org.junit.Assert.*;

public class SJmp3guiTest {

    @org.junit.Test
    public void mainTest() {
        SJmp3gui sJmp3gui = new SJmp3gui();
//        sJmp3gui.main(null);
        SJmp3gui.main(new String[]{});
    }
}