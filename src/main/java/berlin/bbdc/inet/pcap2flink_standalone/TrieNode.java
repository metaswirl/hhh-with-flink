package berlin.bbdc.inet.pcap2flink_standalone;

import java.io.Serializable;
import java.util.*;

/**
 * Created by thoth on 10.05.16.
 */
public class TrieNode implements Serializable {
    TrieNode left = null;
    TrieNode right = null;
    TrieNode parent = null;
    int value;
    int asn = -1000;
    int depth;
    boolean fringe;
    long volume = 0;
    long subtotal = 0;
    long miss_split = 0;

    public TrieNode (int value, int depth) {
        this.value = value;
        this.depth = depth;
    }

    public Iterator<TrieNode> getChildren() {
        return Arrays.asList(this.left, this.right).iterator();
    }

    public long getSum() {
        return this.volume + this.subtotal + this.miss_split;
    }

    @Override
    public String toString () {
        if (asn > -1000)
            return this.value + "/" + this.depth + " AS: " + this.asn;
        return this.value + "/" + this.depth + ":" + this.volume;
    }
}
