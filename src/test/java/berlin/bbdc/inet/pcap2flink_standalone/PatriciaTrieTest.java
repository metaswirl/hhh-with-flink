package berlin.bbdc.inet.pcap2flink_standalone;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.Test;
import org.junit.Ignore;

import java.net.UnknownHostException;

/**
 * Created by thoth on 05.05.16.
 */

public class PatriciaTrieTest {

    @Test
    public void commonSubnet() throws Exception {
        long ip12 = 0b11100000_00000000_00000000_00000000L;
        long ip1 = 0b11110000_00000000_00000000_00000000L;
        long ip2 = 0b11100000_00000000_00000000_00000000L;
        long ip34 = 0b11100000_00000000_00000000_00000000L;
        long ip3 = 0b11100000_00000000_00000000_00000001L;
        long ip4 = 0b11100000_00000000_00000000_00000000L;
        long ip56 = 0b10000000_00000000_00000000_00000000L;
        long ip5 = 0b11111111_11111111_11111111_11111111L;
        long ip6 = 0b00000000_00000000_00000000_00000000L;
        assertEquals(0, new PTrieImNode(ip12, 3).compareTo(PTrieImNode.TrieImNodeFactory(new PTrieImNode(ip1, 24), new PTrieImNode(ip2, 24))));
        assertEquals(0, new PTrieImNode(ip34, 32).compareTo(PTrieImNode.TrieImNodeFactory(new PTrieImNode(ip3, 24), new PTrieImNode(ip4, 24))));
        assertEquals(0, new PTrieImNode(ip56, 32).compareTo(PTrieImNode.TrieImNodeFactory(new PTrieImNode(ip5, 24), new PTrieImNode(ip6, 24))));
    }

    @Test
    public void compareTrieNodesSameSize() throws UnknownHostException {
        PTrieLeafNode tln1 = new PTrieLeafNode(Utils.strIPtoNrIP("250.123.3.1"), 24, 2); // 250.123.3.1
        PTrieLeafNode tln2 = new PTrieLeafNode(Utils.strIPtoNrIP("10.0.0.1"), 24, 1); // 10.0.0.1
        PTrieImNode tin1 = new PTrieImNode(Utils.strIPtoNrIP("123.123.123.123"), 24);
        PTrieImNode tin2 = new PTrieImNode(Utils.strIPtoNrIP("10.0.0.1"), 24);
        assertEquals(1, tln1.compareTo(tln2));
        assertEquals(-1, tln2.compareTo(tln1));
        assertEquals(0, tln1.compareTo(tln1));
        assertEquals(1, tin1.compareTo(tin2));
        assertEquals(-1, tin2.compareTo(tin1));
        assertEquals(0, tin1.compareTo(tin1));
        assertEquals(1, tln1.compareTo(tin1));
        assertEquals(-1, tln2.compareTo(tin1));
        assertEquals(0, tln2.compareTo(tin2));
    }

    @Test
    public void compareTrieNodesDiffSizeWithoutContainment() throws UnknownHostException {
        PTrieLeafNode tln1 = new PTrieLeafNode(Utils.strIPtoNrIP("250.123.3.1"), 24, 2); // 250.123.3.1
        PTrieLeafNode tln2 = new PTrieLeafNode(Utils.strIPtoNrIP("10.0.0.1"), 8, 1); // 10.0.0.1
        PTrieImNode tin1 = new PTrieImNode(Utils.strIPtoNrIP("123.123.123.123"), 24);
        PTrieImNode tin2 = new PTrieImNode(Utils.strIPtoNrIP("10.0.0.1"), 16);
        assertTrue(tln1.compareTo(tln2) > 0);
        assertTrue(tln2.compareTo(tln1) < 0);
        assertTrue(tln1.compareTo(tln1) == 0);
        assertTrue(tin1.compareTo(tin2) > 0);
        assertTrue(tin2.compareTo(tin1) < 0);
        assertTrue(tin1.compareTo(tin1) == 0);
        assertTrue(tln1.compareTo(tin1) > 0);
        assertTrue(tln2.compareTo(tin1) < 0);
    }

//    @Test
//    public void compareTrieNodesDiffSizeWithContaiment() throws UnknownHostException {
//        PTrieLeafNode tln1 = new PTrieLeafNode(Utils.strIPtoNrIP("250.123.3.1"), 24, 2); // 250.123.3.1
//        PTrieLeafNode tln2 = new PTrieLeafNode(Utils.strIPtoNrIP("10.0.0.1"), 8, 1); // 10.0.0.1
//        PTrieImNode tin1 = new PTrieImNode(Utils.strIPtoNrIP("123.123.123.123"), 24);
//        PTrieImNode tin2 = new PTrieImNode(Utils.strIPtoNrIP("10.0.0.1"), 16);
//        assertTrue(tln2.compareTo(tin2) > 0);
//    }

    @Test
    public void insertRight() {
    }

    @Test
    public void findRight() {
    }

    @Test
    public void emptyRoot() {
        PatriciaTrie patriciaTrie = new PatriciaTrie();
        assertTrue(patriciaTrie.root instanceof PTrieImNode);
    }

    @Test
    public void oneNodeRight() throws UnknownHostException, Exception {
        long nrip = Utils.strIPtoNrIP("192.168.0.2"); //
        int offset = 24;
        int asn = 1000;
        PTrieLeafNode testNode = new PTrieLeafNode(nrip, offset, asn);
        PatriciaTrie patriciaTrie = new PatriciaTrie();
        patriciaTrie.insert(nrip, offset, asn);
        assertThat(patriciaTrie.root, instanceOf(PTrieImNode.class));
        assertNull(patriciaTrie.root.left);
        assertNotNull(patriciaTrie.root.right);
        assertThat(patriciaTrie.root.right, instanceOf(PTrieLeafNode.class));
        assertEquals(0, patriciaTrie.root.right.compareTo(testNode));
    }

    @Test
    public void containment() throws Exception{
        PTrieImNode testNodeContains = new PTrieImNode(Utils.strIPtoNrIP("10.2.1.2"), 8);
        PTrieImNode testNodeContained = new PTrieImNode(Utils.strIPtoNrIP("10.1.0.2"), 24);
        PTrieImNode testNodeDifferent = new PTrieImNode(Utils.strIPtoNrIP("10.2.0.2"), 16);
        PTrieImNode testNode = new PTrieImNode(Utils.strIPtoNrIP("10.1.0.3"), 16);
        assertEquals(PTrieNode.CMP.CONTAINS, testNode.checkContainment(testNodeContained));
        assertEquals(PTrieNode.CMP.IS_CONTAINED, testNode.checkContainment(testNodeContains));
        assertEquals(PTrieNode.CMP.DIFFERENT, testNode.checkContainment(testNodeDifferent));
        assertEquals(PTrieNode.CMP.EQUAL, testNode.checkContainment(testNode));
    }

    @Test // Basically testing symmetry
    public void oneNodeLeft() throws UnknownHostException, Exception {
        long nrip = Utils.strIPtoNrIP("10.10.0.2"); //
        int offset = 24;
        int asn = 1000;
        PTrieLeafNode testNode = new PTrieLeafNode(nrip, offset, asn);
        PatriciaTrie patriciaTrie = new PatriciaTrie();
        patriciaTrie.insert(nrip, offset, asn);
        assertThat(patriciaTrie.root, instanceOf(PTrieImNode.class));
        assertNull(patriciaTrie.root.right);
        assertNotNull(patriciaTrie.root.left);
        assertThat(patriciaTrie.root.left, instanceOf(PTrieLeafNode.class));
        assertEquals(0, patriciaTrie.root.left.compareTo(testNode));
    }

    @Test
    public void twoNodesRootLevel() throws UnknownHostException, Exception {
        long nripL = Utils.strIPtoNrIP("10.10.0.2"); //
        long nripR = Utils.strIPtoNrIP("192.168.0.2"); //
        int offset = 24;
        int asn = 1000;
        PTrieLeafNode testNodeLeft = new PTrieLeafNode(nripL, offset, asn);
        PTrieLeafNode testNodeRight = new PTrieLeafNode(nripR, offset, asn);
        PatriciaTrie patriciaTrie = new PatriciaTrie();
        patriciaTrie.insert(nripL, offset, asn);
        patriciaTrie.insert(nripR, offset, asn);
        assertThat(patriciaTrie.root, instanceOf(PTrieImNode.class));
        assertNotNull(patriciaTrie.root.left);
        assertThat(patriciaTrie.root.left, instanceOf(PTrieLeafNode.class));
        assertNotNull(patriciaTrie.root.right);
        assertThat(patriciaTrie.root.right, instanceOf(PTrieLeafNode.class));
        assertEquals(0, patriciaTrie.root.left.compareTo(testNodeLeft));
        assertEquals(0, patriciaTrie.root.right.compareTo(testNodeRight));
    }

    @Test
    public void twoNodesBelowRootLevel() throws UnknownHostException, Exception {
        long nripRL = Utils.strIPtoNrIP("192.168.1.0");
        long nripRR = Utils.strIPtoNrIP("192.168.2.0");
        int offset = 24;
        int asn = 1000;
        PTrieLeafNode testNodeRL = new PTrieLeafNode(nripRL, offset, asn);
        PTrieLeafNode testNodeRR = new PTrieLeafNode(nripRR, offset, asn);
        PatriciaTrie patriciaTrie = new PatriciaTrie();
        patriciaTrie.insert(nripRL, offset, asn);
        patriciaTrie.insert(nripRR, offset, asn);
        assertNull(patriciaTrie.root.left);
        assertNotNull(patriciaTrie.root.right);
        assertNotNull(patriciaTrie.root.right.left);
        assertNotNull(patriciaTrie.root.right.right);
        assertThat(patriciaTrie.root.right, instanceOf(PTrieImNode.class));
        assertThat(patriciaTrie.root.right.left, instanceOf(PTrieLeafNode.class));
        assertThat(patriciaTrie.root.right.right, instanceOf(PTrieLeafNode.class));
        assertEquals(0, patriciaTrie.root.right.left.compareTo(testNodeRL));
        assertEquals(0, patriciaTrie.root.right.right.compareTo(testNodeRR));
    }

    @Ignore
    @Test
    public void threeNodes() throws UnknownHostException, Exception {
        long nripRL = Utils.strIPtoNrIP("192.168.1.0");
        long nripRR = Utils.strIPtoNrIP("192.168.2.0");
        long nripRnew = Utils.strIPtoNrIP("192.169.1.0");
        int offset = 24;
        int asn = 1000;
        PTrieLeafNode testNodeRL = new PTrieLeafNode(nripRL, offset, asn);
        PTrieLeafNode testNodeRR = new PTrieLeafNode(nripRR, offset, asn);
        PTrieLeafNode testNodeRnew = new PTrieLeafNode(nripRnew, offset, asn);
        PatriciaTrie patriciaTrie = new PatriciaTrie();
        patriciaTrie.insert(nripRL, offset, asn);
        patriciaTrie.insert(nripRR, offset, asn);
        patriciaTrie.insert(nripRnew, offset, asn);
        System.out.println(patriciaTrie);
        assertNull(patriciaTrie.root.left);
        assertNotNull(patriciaTrie.root.right);
        assertNotNull(patriciaTrie.root.right.left);
        assertNotNull(patriciaTrie.root.right.right);
        assertNotNull(patriciaTrie.root.right.left.left);
        assertNotNull(patriciaTrie.root.right.left.right);
        assertThat(patriciaTrie.root.right, instanceOf(PTrieImNode.class));
        assertThat(patriciaTrie.root.right.left, instanceOf(PTrieImNode.class));
        assertThat(patriciaTrie.root.right.left.left, instanceOf(PTrieLeafNode.class));
        assertThat(patriciaTrie.root.right.left.right, instanceOf(PTrieLeafNode.class));
        assertThat(patriciaTrie.root.right.right, instanceOf(PTrieLeafNode.class));
        assertEquals(0, patriciaTrie.root.right.left.left.compareTo(testNodeRL));
        assertEquals(0, patriciaTrie.root.right.right.right.compareTo(testNodeRR));
        assertEquals(0, patriciaTrie.root.right.right.compareTo(testNodeRnew));
    }

    @Test
    public void twoNodesSame() throws UnknownHostException, Exception {
        long nrip = Utils.strIPtoNrIP("192.168.1.0");
        int offset = 24;
        int asn = 1000;
        PatriciaTrie patriciaTrie = new PatriciaTrie();
        patriciaTrie.insert(nrip, offset, asn);

        try {
            patriciaTrie.insert(nrip, offset, asn);
            fail("Should throw exception, as a subnet is added twice!");
        } catch (Exception e) {}
    }
}

