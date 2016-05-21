package berlin.bbdc.inet.pcap2flink_standalone;

import berlin.bbdc.inet.exceptions.*;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.EOFException;
import java.sql.Timestamp;

public class PCAPFileSource implements SourceFunction<Tuple1<Packet>> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(PCAPFileSource.class);

    private volatile boolean isRunning = true;

    private final String path;
    private Integer badlyFormatted = 0;
    private Integer wellFormatted = 0;

    public PCAPFileSource(String path) throws CriticalException {
        this.path = path;
    }

    @Override
    public void run(SourceContext<Tuple1<Packet>> ctx) throws Exception {
        LOG.info("Opening file" + this.path);
        PcapHandle handle;
        Packet pkt;
        long newTimestamp;
        long oldTimestamp = 0;
        try {
            handle = Pcaps.openOffline(path);
        } catch (Exception e) {
            LOG.error("Could not open File");
            throw new CriticalException(e);
        }
        while (isRunning) {
            try {
                pkt = handle.getNextPacketEx();
                newTimestamp = handle.getTimestamp().getTime();
                ctx.collectWithTimestamp(new Tuple1(pkt), newTimestamp);
                wellFormatted++;
                if (newTimestamp > oldTimestamp) {
                    ctx.emitWatermark(new Watermark(newTimestamp));
                } else if (newTimestamp < oldTimestamp) {
                    LOG.warn("Error :( Timestamps did not work out");
                }
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
        LOG.info("Retrieved " + badlyFormatted + " ill-formatted and " + wellFormatted + " packets overall!");
        LOG.info("Closing context");
        ctx.close();
    }

    @Override
    public void cancel() {
        LOG.info("Retrieved " + badlyFormatted + " ill-formatted and " + wellFormatted + " packets overall!");
        this.isRunning = false;
    }
}
