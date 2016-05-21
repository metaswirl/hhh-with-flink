package berlin.bbdc.inet.pcap2flink_standalone;

import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.pcap4j.packet.Packet;


public class FlinkJob {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        Trie trie = new Trie(10 * 1500);
        // get input data
        String path_pkts = "/home/thoth/research/data/201605041400.pcap";
        DataStream<Tuple1<Packet>> pkts = env.addSource(new PCAPFileSource(path_pkts));
        String path_routes = "/home/thoth/research/data/routeviews-rv2-20160504-1200.pfx2as";
        DataStream<Tuple3<Long, Integer, Integer>> routes = env.addSource(new RouteViewSource(path_routes));

        routes.flatMap(new AddRoutes2Trie(trie)).print();

        pkts.flatMap(new IPgetter())
            .windowAll(TumblingEventTimeWindows.of(Time.seconds(15)))
            .apply(new AddIP2TrieAndExtractHH(trie))
            .print();


        //routes.print();
        env.execute("Packet program");
    }

}