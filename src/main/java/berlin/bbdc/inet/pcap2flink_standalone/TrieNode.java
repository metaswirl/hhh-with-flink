package berlin.bbdc.inet.pcap2flink_standalone;

/**
 * Created by thoth on 10.05.16.
 */
public class TrieNode {
    TrieNode left = null;
    TrieNode right = null;
    TrieNode parent = null;
    int value;
    int asn = -1000;
    int depth;
    boolean fringe;
    int volume;
    int subtotal;
    int miss_copy;
    int miss_split;

    public TrieNode (int value, int depth) {
        this.value = value;
        this.depth = depth;
    }

    @Override
    public String toString () {
        if (asn > -1000)
            return this.value + "/" + this.depth + " AS: " + this.asn;
        return this.value + "/" + this.depth;
    }
}
