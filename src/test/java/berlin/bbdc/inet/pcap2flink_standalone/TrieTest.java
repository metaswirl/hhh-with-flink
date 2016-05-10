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
        System.out.println(trie);
        assertNotNull(trie.root);
        assertNull(trie.root.left);
        assertNotNull(trie.root.right);
        assertEquals(1, trie.root.right.value);
        assertEquals(1, trie.root.right.depth);
        assertEquals(asn, trie.root.right.asn);

        trie.insertAS(ip2, size2, asn);
        System.out.println(trie);
        assertNotNull(trie.root.left);
        assertEquals(1, trie.root.left.left.left.right.value);
        assertEquals(4, trie.root.left.left.left.right.depth);
        assertEquals(asn, trie.root.left.left.left.right.asn);
    }

    @Test
    public void insertIP() {
        Trie trie = new Trie(10);
        long ip = 0b10000000_00000000_00000000_00000000L;
        int size = 3;
        trie.insertIP(ip, size);
        assertNotNull(trie.root);
        assertEquals(size, trie.root.volume);
    }
}
