package berlin.bbdc.inet.pcap2flink_standalone;

/**
 * Created by thoth on 05.05.16.
 */
public class PTrieNode implements Comparable<PTrieNode> {
    public PTrieNode parent = null;
    public long subnet;
    public int size;
    public PTrieNode left = null;
    public PTrieNode right = null;

    public enum CMP {
        CONTAINS, IS_CONTAINED, DIFFERENT, EQUAL
    }

    public PTrieNode(long subnet, int size) {
        this.subnet = subnet;
        this.size = size;
    }

    public CMP checkContainment(PTrieNode node) {
        if ((this.size < node.size) &&
            ((this.subnet & (0xFFFFFFFFL << (32 - this.size))) == (node.subnet & (0xFFFFFFFFL << (32 - this.size)))))
                return CMP.CONTAINS;
        else if ((this.size > node.size) &&
            ((this.subnet & (0xFFFFFFFFL << (32 - node.size))) == (node.subnet & (0xFFFFFFFFL << (32 - node.size)))))
                return CMP.IS_CONTAINED;
        else if ((this.subnet & (0xFFFFFFFFL << (32 - node.size))) == (node.subnet & (0xFFFFFFFFL << (32 - node.size))))
            return CMP.EQUAL;
        else
            return CMP.DIFFERENT;
    }

    @Override
    public int compareTo (PTrieNode node) {
        //int minSize = Math.min(this.size, node.size);
        //long mask1 = this.subnet & (0xFFFFFFFF << (32 - minSize));
        //long mask2 = node.subnet & (0xFFFFFFFF << (32 - minSize));
        //int cmp = Long.compare(mask1, mask2);
        return Long.compare(this.subnet, node.subnet);
    }

    @Override
    public String toString () {
        return Utils.nrIPtoStrIP(this.subnet) + "/" + this.size;
    }
}
class PTrieImNode extends PTrieNode {
    public PTrieImNode(long subnet, int size) {
        super(subnet, size);
    }
    public static PTrieImNode TrieImNodeFactory(PTrieNode node1, PTrieNode node2) throws Exception {
        int snsize = (int) (Math.log(node1.subnet ^ node2.subnet) / Math.log(2)) + 1;
        if (snsize >= 32)
            return new PTrieImNode(2147483648L, 0);
        return new PTrieImNode(node1.subnet & (0xFFFFFFFFL << (snsize)), snsize);
    }
    @Override
    public String toString () {
        return "TrieImNode " + super.toString();
    }
}
class PTrieLeafNode extends PTrieNode {
    public int asn;

    public PTrieLeafNode(long subnet, int size, int asn) {
        super(subnet, size);
        this.asn = asn;
    }

    public boolean contains(long ip) {
        // TODO: implement
        return false;
    }

    @Override
    public String toString () {
        return "TrieLeafNode " + super.toString() + " -> " + this.asn;
    }
}
