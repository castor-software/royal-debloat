package com.sangupta.clitools;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CliMainTest {

    @Before
    public void setUp() throws Exception {
        System.out.println("Starting test tests...");
    }

    @Test
    public void testWhoami() {
        System.out.println("test whoami...");
        CliMain.main(new String[]{"whoami"});
    }

//    @Test
//    public void testImdb() {
//        System.out.println("test imdb...");
//        CliMain.main(new String[]{"imdb", "Gladiator"});
//    }
}