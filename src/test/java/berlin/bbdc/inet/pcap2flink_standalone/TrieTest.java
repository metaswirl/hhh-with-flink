package berlin.bbdc.inet.pcap2flink_standalone;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by thoth on 10.05.16.
 */
public class TrieTest {
    @Test
    public void emptyRoot() {
        Trie trie = new Trie(10);
        assertNotNull(trie.root);
        assertNull(trie.root.left);
        assertNull(trie.root.right);
    }

    @Test
    public void insertAS() {
        long ip1 = 0b10000000_00000000_00000000_00000000L;
        long ip2 = 0b00010000_00000000_00000000_00000000L;
        int size1 = 1;
        int size2 = 4;

        int asn = 1000;
        Trie trie = new Trie(10);
        trie.insertAS(ip1, size1, asn);
        assertNotNull(trie.root);
        assertNull(trie.root.left);
        assertNotNull(trie.root.right);
        assertEquals(1, trie.root.right.value);
        assertEquals(1, trie.root.right.depth);
        assertEquals(asn, trie.root.right.asn);

        trie.insertAS(ip2, size2, asn);
        assertNotNull(trie.root.left);
        assertEquals(1, trie.root.left.left.left.right.value);
        assertEquals(4, trie.root.left.left.left.right.depth);
        assertEquals(asn, trie.root.left.left.left.right.asn);
    }

    @Test
    public void insertIP() {
        Trie trie = new Trie(4);
        long ip = 0b11000000_00000000_00000000_00000000L;
        int size = 3;
        // create node right
        trie.insertIP(ip, size);
        assertNotNull(trie.root);
        assertNotNull(trie.root.right);
        assertEquals(size, trie.root.right.volume);
        // create node right right
        trie.insertIP(ip, size);
        assertNotNull(trie.root.right.right);
        assertEquals(size, trie.root.right.right.volume);
        ip = 0b00100000_00000000_00000000_00000000L;
        size = 1;
        trie.insertIP(ip, size);
        assertNotNull(trie.root.left);
        assertNull(trie.root.left.left);
        assertEquals(size, trie.root.left.volume);
        trie.insertIP(ip, size);
        assertNotNull(trie.root.left);
        assertNull(trie.root.left.left);
        assertEquals(2*size, trie.root.left.volume);
    }
}
