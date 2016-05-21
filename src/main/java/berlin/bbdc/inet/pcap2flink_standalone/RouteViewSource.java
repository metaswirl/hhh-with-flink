package berlin.bbdc.inet.pcap2flink_standalone;

import berlin.bbdc.inet.exceptions.CriticalException;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.runtime.io.network.api.reader.BufferReader;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RouteViewSource implements SourceFunction<Tuple3<Long, Integer, Integer>> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RouteViewSource.class);

    private volatile boolean isRunning = true;

    private final String path;

    public RouteViewSource(String path) throws CriticalException {
        this.path = path;
    }

    @Override
    public void run(SourceContext<Tuple3<Long, Integer, Integer>> ctx) throws Exception {
        LOG.info("Opening file" + this.path);
        PcapHandle handle;
        BufferedReader reader;
        Integer badlyFormatted = 0;
        String line;
        String[] fields;
        long ipaddr;
        int snsize;
        int asn;
        HashMap<List,String> mapper;
        try {
            reader = new BufferedReader(new FileReader(this.path));
        } catch (Exception e) {
            LOG.error("Could not open File");
            throw new CriticalException(e);
        }

        while (isRunning && (line = reader.readLine()) != null) {
            try {
                fields = line.split("\t");
                ipaddr = Utils.strIPtoNrIP(fields[0]);
                snsize = Integer.parseInt(fields[1]);
                if (fields[2].contains("_")) // Multi-homed AS
                    asn = -1;
                else if (fields[2].contains(",")) // Subnet owned by multiple AS
                    asn = -2;
                else
                    asn = Integer.parseInt(fields[1]);

                ctx.collect(new Tuple3<Long, Integer, Integer>(ipaddr, snsize, asn));
            } catch (Exception e) {
                LOG.error("an exception was thrown", e);
                throw e;
            }
        }
        LOG.info("Closing context");
        ctx.close();
    }

    @Override
    public void cancel() { this.isRunning = false; }
}
