package berlin.bbdc.inet.pcap2flink_standalone;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.pcap4j.packet.Packet;


public class FlinkJob {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        // get input data
        String path = "/home/thoth/tmp/201602021400.pcap";
        DataStream<Packet> pkts = env.addSource(new PCAPFileSource(path));
        DataStream<Tuple3> routes = env.addSource(new RouteViewSource(path));

        pkts.flatMap(new IPgetter()).print();
        //routes.flatMap(new PatriciaTrie);
        // split up the lines in pairs (2-tuples) containing: (word,1)

        // execute program

        env.execute("Packet program");
    }

}