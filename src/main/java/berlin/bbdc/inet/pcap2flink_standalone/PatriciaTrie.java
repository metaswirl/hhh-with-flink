package berlin.bbdc.inet.pcap2flink_standalone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoth on 05.05.16.
 * 110* left
 *  111110* left
 *  111111* right -> AS 1
 * 111* right -> AS 1
 *
 */

public class PatriciaTrie {
    public PTrieNode root = null;

    public PatriciaTrie() {
        this.root = new PTrieImNode(2147483648L, 0);
    }

    public void insert(long subnet, int offset, int asn) throws Exception {
        PTrieLeafNode leaf = new PTrieLeafNode(subnet, offset, asn);
        PTrieNode curr = root;
        PTrieNode parent = null;
        while (curr instanceof PTrieImNode && leaf.checkContainment(curr) == PTrieNode.CMP.IS_CONTAINED) {
            parent = curr;
            if (leaf.compareTo(curr) >= 0) { // go deeper
                if (curr.right == null) {
                    curr.right = leaf;
                    leaf.parent = curr;
                    return;
                }
                curr = curr.right;
            } else if (leaf.compareTo(curr) < 0) {
                if (curr.left == null) {
                    curr.left = leaf;
                    leaf.parent = curr;
                    return;
                }
                curr = curr.left;
            }
        }
        if (curr instanceof PTrieImNode) {
            if (leaf.checkContainment(curr) == PTrieNode.CMP.CONTAINS) {
                throw new Exception("Case should not occur. It would entail that an AS contains another AS.");
            } else if (leaf.checkContainment(curr) == PTrieNode.CMP.DIFFERENT) {
                PTrieImNode newImNode = PTrieImNode.TrieImNodeFactory(leaf, curr);
                newImNode.parent = parent;
                if (curr == parent.left)
                    parent.left = newImNode;
                else
                    parent.right = newImNode;

                if (leaf.compareTo(curr) >= 0) {
                    newImNode.left = curr;
                    newImNode.right = leaf;
                } else {
                    newImNode.left = leaf;
                    newImNode.right = curr;
                }
                return;
            } else if (leaf.checkContainment(curr) == PTrieNode.CMP.EQUAL) {
                throw new Exception("Case should not occur. It would entail that an AS contains another AS.");
            }
        }
        if (curr instanceof PTrieLeafNode) {
            if (leaf.checkContainment(curr) == PTrieNode.CMP.CONTAINS) {
            } else if (leaf.checkContainment(curr) == PTrieNode.CMP.DIFFERENT) {
                PTrieImNode newNode = PTrieImNode.TrieImNodeFactory(leaf, curr);
                curr.parent = newNode;
                leaf.parent = newNode;
                if (curr == parent.left)
                    parent.left = newNode;
                else
                    parent.right = newNode;
                newNode.parent = parent;
                if (leaf.compareTo(curr) >= 0) {
                    newNode.left = curr;
                    newNode.right = leaf;
                } else {
                    newNode.left = leaf;
                    newNode.right = curr;
                }
                return;
            } else if (leaf.checkContainment(curr) == PTrieNode.CMP.EQUAL) {
                throw new Exception("Nodes with the same subnet have appeared!");
            }
        }
        throw new Exception("Exceptional case triggered!");
    }

    private List<String> walk(PTrieNode node, List<String> list, int level) { // pre-order, collects strings
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

    // TODO: Retrieve ASN and
    public int find(long ip) {
        PTrieNode curr = root;
        while (true) {
            if (curr instanceof PTrieImNode) {
                // TODO
            } else if (curr instanceof PTrieLeafNode && ((PTrieLeafNode) curr).contains(ip)) {
                return ((PTrieLeafNode) curr).asn;
            } else {
                return -1;
            }
        }
    }
}
