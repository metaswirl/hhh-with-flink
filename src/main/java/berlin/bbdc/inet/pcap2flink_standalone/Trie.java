package berlin.bbdc.inet.pcap2flink_standalone;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoth on 10.05.16.
 */
public class Trie {
    TrieNode root = null;
    int tSplit;
    int maxDepth;

    public Trie (int tSplit) {
        this.tSplit = tSplit;
        this.maxDepth = 21;
        this.root = new TrieNode(0, 0);
    }
    public void insertAS (long subnet, int subnetsize, int asn) {
        TrieNode curr = this.root;
        for (int i=31; i > (31-subnetsize); i--) {
            if ((subnet & (long) Math.pow(2, i)) > 0) {
                if (curr.right == null) {
                    curr.right = new TrieNode(1, curr.depth+1);
                    curr.right.parent = curr;
                }
                curr = curr.right;
            } else {
                if (curr.left == null) {
                    curr.left = new TrieNode(0, curr.depth+1);
                    curr.left.parent = curr;
                }
                curr = curr.left;
            }
        }
        curr.asn = asn;
        curr.fringe = true;
    }
    public void insertIP (long ip, int size) {
        TrieNode curr = this.root;
        boolean goRight;
        for (int i=31; i > 0; i--) { // TODO: use max depth
            goRight = ((ip & (long) Math.pow(2, i)) > 0);
            if (curr.fringe) {
                if ((curr.volume + size) < this.tSplit) {
                    curr.volume += size;
                    return;
                } else {
                    curr.fringe = false;
                    if (curr.depth == this.maxDepth) {
                        curr.subtotal = size;
                        return;
                    }
                }

            } else if (curr.depth == this.maxDepth){
                curr.subtotal += size;
                return;
            }
            if (goRight) {
                if (curr.right == null) {
                    curr.right = new TrieNode(1, curr.depth + 1);
                    curr.right.parent = curr;
                }
                curr = curr.right;
            } else {
                if (curr.left == null) {
                    curr.left = new TrieNode(1, curr.depth + 1);
                    curr.left.parent = curr;
                }
                curr = curr.left;
            }
        }
    }
    public int updateTotalVolume (TrieNode node) {
        if (node == null) return 0;
        TrieNode curr = node;
        if (curr.depth != this.maxDepth) {
            curr.subtotal = 0;
            curr.subtotal += updateTotalVolume(node.left);
            curr.subtotal += updateTotalVolume(node.right);
        }
        return curr.subtotal;
    }
    public void extractHeavyHitters(Collector<Tuple3<Long, Integer, Integer>> out) {
        extractHeavyHitters(this.root, 0L, -1000, out);
    }
    public void extractHeavyHitters(TrieNode node, long ip, int asn, Collector<Tuple3<Long, Integer, Integer>> out) {
        if (node == null) return;
        TrieNode curr = node;
        ip = ip | (long) Math.pow(2, (31-curr.depth)); // off-by-one?
        if (curr.asn != 1000) asn = curr.asn;
        extractHeavyHitters(node.left, ip, asn, out);
        extractHeavyHitters(node.right, ip, asn, out);
        if (!curr.fringe)
            out.collect(new Tuple3<Long, Integer, Integer>(ip, curr.asn, curr.subtotal));
    }
    private List<String> walk(TrieNode node, List<String> list, int level) { // pre-order, collects strings
        String output = "";
        for (int i=0; i < level; i++) {
            output += "-";
        }
        if (node == null) {
            list.add(output + ">null");
            return list;
        }
        list.add(output + ">" + node.toString());
        list = walk(node.left, list, level+1);
        list = walk(node.right, list, level+1);
        return list;
    }
    @Override
    public String toString() {
        List<String> result = this.walk(this.root, new ArrayList<String>(), 0);
        return String.join("\n", result);
    }
}
