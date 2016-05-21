package berlin.bbdc.inet.pcap2flink_standalone;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Created by thoth on 10.05.16.
 */
public class Trie implements Serializable {
    TrieNode root = null;
    private long tSplit;
    private int maxDepth;

    public Trie (int tSplit) {
        this.tSplit = tSplit;
        this.maxDepth = 21;
        this.reset();
    }
    public void reset () {
        this.root = new TrieNode(0, 0);
        this.root.fringe = false;
    }
    public void insertAS (long subnet, int subnetsize, int asn) {
        TrieNode curr = this.root;
        //this.root.fringe = true;
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
            goRight = ((ip & (long) Math.pow(2, i)) > 0);
            if (goRight) {
                if (curr.right == null) {
                    curr.right = new TrieNode(1, curr.depth + 1);
                    curr.right.parent = curr;
                    curr.right.fringe = true;
                }
                curr = curr.right;
            } else {
                if (curr.left == null) {
                    curr.left = new TrieNode(1, curr.depth + 1);
                    curr.left.parent = curr;
                    curr.left.fringe = true;
                }
                curr = curr.left;
            }
        }
    }
    public void privatefinalizeTrie() {
        updateTotalVolume(this.root);
        updateMissingVolume(this.root);
    }
    public long updateTotalVolume (TrieNode node) {
        if (node == null) return 0;
        TrieNode curr = node;
        if (curr.depth != this.maxDepth) {
            curr.subtotal = 0;
            curr.subtotal += updateTotalVolume(node.left);
            curr.subtotal += updateTotalVolume(node.right);
        }
        return curr.volume + curr.subtotal;
    }
    public void updateMissingVolume(TrieNode node) {
        TrieNode child = null;
        long frac = 0;
        for (Iterator<TrieNode> iter = node.getChildren(); iter.hasNext(); child = iter.next()) {
            if (child == null) continue;
            // copy-all strategy
            //child.miss_copy = node.volume + node.miss_copy;
            // split strategy
            frac = (child.subtotal + child.volume) / node.subtotal;
            child.miss_split = (node.volume + node.miss_split) * frac;
            updateMissingVolume(child);
        }
    }
    public void extractHeavyHitters(double factor, Collector<Tuple4<String, Integer, Integer, Long>> out) {
        List<Tuple4<String, Integer, Integer, Long>> listo = new ArrayList();
        extractHeavyHitters((long) Math.floor(factor * this.root.getSum()), this.root, 0L, -1000, listo);
        Collections.sort(listo, new Comparator<Tuple4<String, Integer, Integer, Long>>() {
            @Override
            public int compare(Tuple4<String, Integer, Integer, Long> t0, Tuple4<String, Integer, Integer, Long> t1) {
                return t0.f2.compareTo(t1.f2);
            }
        });
        listo.forEach(elem -> out.collect(elem));
    }
    public void extractHeavyHitters(long minimum, TrieNode node, long ip, int asn, List<Tuple4<String, Integer, Integer, Long>> out) {
        if (node == null) return;
        TrieNode curr = node;
        ip = ip | (long) Math.pow(2, (32-curr.depth)); // off-by-one?
        if (curr.asn != 1000) asn = curr.asn;
        extractHeavyHitters(minimum, node.left, ip, asn, out);
        extractHeavyHitters(minimum, node.right, ip, asn, out);
        if (!curr.fringe && curr.getSum() > minimum)
            out.add(new Tuple4<String, Integer, Integer, Long>(Utils.nrIPtoStrIP(ip), curr.depth, curr.asn, curr.subtotal));
    }
    private <T> void walk(BiFunction<Integer, TrieNode, T> fun, List<T> output, TrieNode node, int level) { // pre-order, collects strings
        output.add(fun.apply(level, node));
        if (node == null) return;
        walk(fun, output, node.left, level+1);
        walk(fun, output, node.right, level+1);
    }
    public String toString() {
        List<String> output = new ArrayList<String>();
        BiFunction<Integer, TrieNode, String> fun =(lvl, n) -> {
            String lvlstr = "";
            for (int i=0; i < lvl; i++) {
                lvlstr += "-";
            }
            if (n == null)
                return lvlstr + "> null";
            else
                return lvlstr + "> " + n;
        };
        this.walk(fun, output, this.root, 0);
        return String.join("\n", output);
    }
}
