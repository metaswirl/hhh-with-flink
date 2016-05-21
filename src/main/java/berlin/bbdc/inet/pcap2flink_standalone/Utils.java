package berlin.bbdc.inet.pcap2flink_standalone;

import com.google.common.math.LongMath;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by thoth on 05.05.16.
 */
public class Utils {
    public static long strIPtoNrIP(String strip) throws UnknownHostException {
        long result = 0;
        for (byte b: ((Inet4Address) InetAddress.getByName(strip)).getAddress())
            result = result << 8 | (b & 0xFF);
        return result;
    }
    public static long byteIPtoNrIP(byte[] byteip) throws UnknownHostException {
        long result = 0;
        for (byte b: byteip)
            result = result << 8 | (b & 0xFF);
        return result;
    }
    public static String nrIPtoStrIP(long nrip) {
        String strip = "";
        for (int i=3; i>=0; i--) {
            strip += String.valueOf((nrip & (0xFF << (i*8))) >> (i*8));
            if (i > 0)
               strip += ".";
        }
        return strip;
    }
    public static boolean isContained (long subnet, int offset, long ip) {
        long mask = 0xFFFFFFFF << (32-offset);
        if ((subnet & mask) == (ip & mask))
            return true;
        return false;
    }
}
