package esa.nmf.test;

import opssat.simulator.main.ESASimulator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class GPSSoftSimAdapterTest {

    @Test
    public void testTIMEAFormat() throws IOException {
        opssat.simulator.main.ESASimulator eSASimulator = new ESASimulator();
        esa.mo.platform.impl.provider.softsim.PowerControlSoftSimAdapter pcAdapter =
                new esa.mo.platform.impl.provider.softsim.PowerControlSoftSimAdapter(eSASimulator);
        esa.mo.platform.impl.provider.softsim.GPSSoftSimAdapter gPSSoftSimAdapter =
                new esa.mo.platform.impl.provider.softsim.GPSSoftSimAdapter(eSASimulator, pcAdapter);
        //Expecting something in lines of:
        //#TIMEA,COM1,0,35.0,FINESTEERING,1337,410010.000,00000000,9924,1984;VALID,0,0,0,2005,8,25,17,53,17000,VALID*e2fc088c
        String expectedRegex = "#TIMEA,COM1,0,35\\.0,FINESTEERING,\\d{4},\\d{6}\\.\\d{2,3},[0|1]{8},[\\d|\\D]{4},\\d{4}" +
                ";VALID,0,0,0,\\d{4},\\d{1,2},\\d{1,2},\\d{1,2},\\d{1,2},\\d{5},VALID[\\D|\\d]{9}";
        String timea = gPSSoftSimAdapter.getTIMEASentence();
        Assert.assertTrue("Actual was: " +timea, timea.matches(expectedRegex));
    }

}
