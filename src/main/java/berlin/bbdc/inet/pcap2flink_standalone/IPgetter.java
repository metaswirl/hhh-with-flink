package berlin.bbdc.inet.pcap2flink_standalone;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.factory.PacketFactories;
import org.pcap4j.packet.namednumber.DataLinkType;

import java.net.Inet4Address;

public final class IPgetter implements FlatMapFunction<Packet, Tuple3<Inet4Address, Inet4Address, Integer>> {
    private static final long serialVersionUID = 1L;

    @Override
    public void flatMap(Packet pkt, Collector<Tuple3<Inet4Address, Inet4Address, Integer>> out)
            throws Exception {
        // normalize and split the line
        //Packet pkt = interpretData(value);
        if (pkt != null && pkt.getPayload() instanceof IpV4Packet) {
          IpV4Packet.IpV4Header hdr = (IpV4Packet.IpV4Header) pkt.getPayload().getHeader();
          out.collect(new Tuple3<Inet4Address, Inet4Address, Integer>(hdr.getDstAddr(), hdr.getSrcAddr(), hdr.getTotalLengthAsInt()));
        }
    }
    private Packet interpretData(byte[] rawPkt) {
        try {
            return PacketFactories.getFactory(Packet.class, DataLinkType.class)
                    .newInstance(rawPkt, 0, rawPkt.length, DataLinkType.EN10MB);
        } catch (java.lang.IllegalArgumentException e) {
            return null;
            //LOG.warning("Input Data is corrupted!");
        }
    }
}
