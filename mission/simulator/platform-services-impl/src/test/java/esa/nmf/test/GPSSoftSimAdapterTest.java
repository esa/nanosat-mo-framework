package esa.nmf.test;

import opssat.simulator.main.ESASimulator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class GPSSoftSimAdapterTest {

    private static opssat.simulator.main.ESASimulator eSASimulator = new ESASimulator();

    @Test
    public void testTIMEAFormat() throws IOException {
        esa.mo.platform.impl.provider.softsim.PowerControlSoftSimAdapter pcAdapter =
                new esa.mo.platform.impl.provider.softsim.PowerControlSoftSimAdapter(eSASimulator);
        esa.mo.platform.impl.provider.softsim.GPSSoftSimAdapter gPSSoftSimAdapter =
                new esa.mo.platform.impl.provider.softsim.GPSSoftSimAdapter(eSASimulator, pcAdapter);
        //Expecting something in lines of:
        //#TIMEA,COM1,0,35.0,FINESTEERING,1337,410010.000,00000000,9924,1984;VALID,0,0,0,2005,8,25,17,53,17000,VALID*e2fc088c
        String expectedRegex = "#TIMEA,COM1,0,35\\.0,FINESTEERING,\\d{4},\\d{6}\\.\\d{1,3},[0|1]{8},[\\d|\\D]{4}," +
                "\\d{4};VALID,0,0,-?\\d{1,2}\\.\\d{11},\\d{4},\\d{1,2},\\d{1,2},\\d{1,2},\\d{1,2},\\d{5}," +
                "VALID\\*[\\D|\\d]{1,8}";
        String timea = gPSSoftSimAdapter.getTIMEASentence();
        Assert.assertTrue("Actual was: " +timea, timea.matches(expectedRegex));
    }

    @Test
    public void testBESTXYZAHeaderFormat() throws IOException {
        esa.mo.platform.impl.provider.softsim.PowerControlSoftSimAdapter pcAdapter =
                new esa.mo.platform.impl.provider.softsim.PowerControlSoftSimAdapter(eSASimulator);
        esa.mo.platform.impl.provider.softsim.GPSSoftSimAdapter gPSSoftSimAdapter =
                new esa.mo.platform.impl.provider.softsim.GPSSoftSimAdapter(eSASimulator, pcAdapter);
        //Expecting the BESTXYZ header to be in the lines of:
        //#BESTXYZA,COM1,0,35.0,FINESTEERING,2177,306117.000,00100000,97b7,2310...
        String expectedRegex = "^#BESTXYZA,COM1,0,35\\.0,FINESTEERING,\\d{4},\\d{6}\\.\\d{1,3},[0|1]{8}," +
                "[\\d|\\D]{4},\\d{4}.+";
        String timea = gPSSoftSimAdapter.getBestXYZSentence();
        Assert.assertTrue("Actual was: " +timea, timea.matches(expectedRegex));
    }

}
