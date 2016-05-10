package berlin.bbdc.inet.pcap2flink_standalone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.net.UnknownHostException;

public class UtilsTest {
    String strip = "192.168.0.2";
    long nrip = 3232235522L; // 192.168.0.2
    long nrsub = 3232235776L; // 192.168.1.0

    @Test
    public void convertStrIPtoNrNoException() {
        try {
            assertEquals(nrip, (long) Utils.strIPtoNrIP(strip));
        } catch (UnknownHostException e) {
            fail("An unknownHostException was thrown unexpectedly.");
        }
    }

    @Test
    public void convertStrIPtoNrThrowException() {
        try {
            assertEquals(nrip, (long) Utils.strIPtoNrIP("192.168.0.2.3"));
            fail("Should throw exception, but did not.");
        } catch (UnknownHostException e) {
        }
    }

    @Test
    public void convertNrIPtoStr() {
        System.out.println(Utils.nrIPtoStrIP(0b10000000_00000000_00000000_00000000L));
        System.out.println(Utils.nrIPtoStrIP(0b00000000_00000000_00000000_00000000L));
        assertEquals(strip, Utils.nrIPtoStrIP(nrip));
    }

    @Test
    public void IPContained() {
        assertTrue(Utils.isContained(nrsub, 16, nrip));
    }

    @Test
    public void IPNotContained() {
        assertFalse(Utils.isContained(nrsub, 24, nrip));
    }
}
