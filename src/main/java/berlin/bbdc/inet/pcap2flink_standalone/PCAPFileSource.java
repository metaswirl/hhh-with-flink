package berlin.bbdc.inet.pcap2flink_standalone;

import berlin.bbdc.inet.exceptions.*;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.EOFException;

public class PCAPFileSource implements SourceFunction<Packet> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(PCAPFileSource.class);

    private volatile boolean isRunning = true;

    private final String path;

    public PCAPFileSource(String path) throws CriticalException {
        this.path = path;
    }

    @Override
    public void run(SourceContext<Packet> ctx) throws Exception {
        LOG.info("Opening file" + this.path);
        PcapHandle handle;
        Integer badlyFormatted = 0;
        try {
            handle = Pcaps.openOffline(path);
        } catch (Exception e) {
            LOG.error("Could not open File");
            throw new CriticalException(e);
        }
        while (isRunning) {
            try {
                ctx.collect(handle.getNextPacketEx());
            } catch (NullPointerException e) {
                LOG.warn("Weird Stuff");
                badlyFormatted++;
            } catch (IllegalArgumentException e) {
                badlyFormatted++;
                LOG.warn("Retrieved packet was ill-formatted");
            } catch (EOFException e) {
                LOG.info("EOF Parse ended");
                break;
            } catch (NotOpenException e) {
                LOG.error("PCAP file not open!");
                break;
            } catch (PcapNativeException e) {
                LOG.error("PCAP Exception?!");
                break;
            }
        }
        LOG.info("Closing handle");
        handle.close();
        LOG.info("Retrieved " + badlyFormatted + " ill-formatted packets overall!");
        LOG.info("Closing context");
        ctx.close();
    }

    @Override
    public void cancel() { this.isRunning = false; }
}
