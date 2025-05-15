package hierarcy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HierarchyMapperTest {

    @Test
    @DisplayName("Test case 1: Simple full-depth tree")
    void testSimpleFullDepthTree() {
        // BAC (1) -> AC (2) -> SC (3) -> SL (4 - Leaf)
        var sl4 = new Node(401, Tier.SL);
        var sc3 = new Node(301, Tier.SC, sl4);
        var ac2 = new Node(201, Tier.AC, sc3);
        var bac1 = new Node(101, Tier.BAC, ac2);

        var mapper = new HierarchyMapper();
        var result = mapper.mapLeavesToAncestors(bac1);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(401));

        var mapping = result.get(401);
        assertEquals(101, mapping.bacNodeId());
        assertEquals(201, mapping.acNodeId());
        assertEquals(301, mapping.scNodeId());
        assertEquals(401, mapping.slNodeId());
    }

    @Test
    @DisplayName("Test case 2: Tree with multiple branches and leaves")
    void testTreeWithMultipleBranches() {
        // Structure:
        // BAC_1 -> AC_1 -> SC_1 -> SL_1 (Leaf)
        //                -> SC_2 -> SL_2 (Leaf)
        //        -> AC_2 -> SC_3 -> SL_3 (Leaf)
        // BAC_2 -> AC_3 -> SL_4 (Leaf - SC tier skipped)
        //        -> SC_4 -> SL_5 (Leaf)
        //        -> SC_5 (Leaf - SL tier missing)

        var sl1 = new Node(411, Tier.SL);
        var sl2 = new Node(422, Tier.SL);
        var sc1 = new Node(311, Tier.SC, sl1);
        var sc2 = new Node(322, Tier.SC, sl2);
        var ac1 = new Node(211, Tier.AC, sc1, sc2);

        var sl3 = new Node(433, Tier.SL);
        var sc3 = new Node(333, Tier.SC, sl3);
        var ac2 = new Node(222, Tier.AC, sc3);
        var bac1 = new Node(111, Tier.BAC, ac1, ac2);

        var sl4 = new Node(444, Tier.SL); // Leaf under AC, skipping SC tier
        var sl5 = new Node(455, Tier.SL);
        var sc4 = new Node(344, Tier.SC, sl5);
        var sc5 = new Node(355, Tier.SC); // SC node is a leaf (leafNode = true)
        var ac3 = new Node(233, Tier.AC, sl4, sc4, sc5);
        var bac2 = new Node(122, Tier.BAC, ac3);

        var mapper = new HierarchyMapper();
        var result1 = mapper.mapLeavesToAncestors(bac1);

        assertEquals(3, result1.size());
        assertTrue(result1.containsKey(411));
        assertTrue(result1.containsKey(422));
        assertTrue(result1.containsKey(433));

        // Check SL_1 (411)
        var mapping411 = result1.get(411);
        assertEquals(111, mapping411.bacNodeId());
        assertEquals(211, mapping411.acNodeId());
        assertEquals(311, mapping411.scNodeId());
        assertEquals(411, mapping411.slNodeId());

        // Check SL_2 (422)
        var mapping422 = result1.get(422);
        assertEquals(111, mapping422.bacNodeId());
        assertEquals(211, mapping422.acNodeId());
        assertEquals(322, mapping422.scNodeId());
        assertEquals(422, mapping422.slNodeId());

        // Check SL_3 (433)
        var mapping433 = result1.get(433);
        assertEquals(111, mapping433.bacNodeId());
        assertEquals(222, mapping433.acNodeId());
        assertEquals(333, mapping433.scNodeId());
        assertEquals(433, mapping433.slNodeId());

        // Now test the second BAC tree (bac2)
        var result2 = mapper.mapLeavesToAncestors(bac2);

        assertEquals(3, result2.size());
        assertTrue(result2.containsKey(444));
        assertTrue(result2.containsKey(455));
        assertTrue(result2.containsKey(355)); // Check for the SC leaf

        // Check SL_4 (444 - SC tier skipped)
        var mapping444 = result2.get(444);
        assertEquals(122, mapping444.bacNodeId());
        assertEquals(233, mapping444.acNodeId());
        assertNull(mapping444.scNodeId());
        assertEquals(444, mapping444.slNodeId());

        // Check SL_5 (455)
        var mapping455 = result2.get(455);
        assertEquals(122, mapping455.bacNodeId());
        assertEquals(233, mapping455.acNodeId());
        assertEquals(344, mapping455.scNodeId());
        assertEquals(455, mapping455.slNodeId());

        // Check SC_5 (355 - SC node is a leaf)
        var mapping355 = result2.get(355);
        assertEquals(122, mapping355.bacNodeId());
        assertEquals(233, mapping355.acNodeId());
        assertEquals(355, mapping355.scNodeId()); // SC ancestor should be 355 (the leaf itself)
        assertNull(mapping355.slNodeId());
    }

    @Test
    @DisplayName("Test case 3: Leaf node is AC")
    void testLeafIsAC() {
        // BAC (1) -> AC (2 - Leaf)
        var ac2 = new Node(202, Tier.AC);
        var bac1 = new Node(102, Tier.BAC, ac2);

        var mapper = new HierarchyMapper();
        var result = mapper.mapLeavesToAncestors(bac1);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(202));

        var mapping = result.get(202);
        assertEquals(102, mapping.bacNodeId());
        assertEquals(202, mapping.acNodeId()); // AC ancestor should be 202 (the leaf itself)
        assertNull(mapping.scNodeId());
        assertNull(mapping.slNodeId());
    }

    @Test
    @DisplayName("Test case 4: Leaf node is BAC (the root)")
    void testLeafIsBAC() {
        // BAC (1 - Leaf)
        var bac1 = new Node(103, Tier.BAC);

        var mapper = new HierarchyMapper();
        var result = mapper.mapLeavesToAncestors(bac1);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(103));

        var mapping = result.get(103);
        assertEquals(103, mapping.bacNodeId()); // BAC ancestor should be 103 (the leaf itself)
        assertNull(mapping.acNodeId());
        assertNull(mapping.scNodeId());
        assertNull(mapping.slNodeId());
    }

    @Test
    @DisplayName("Test case 5: Empty tree (null root)")
    void testEmptyTreeNullRoot() {
        var mapper = new HierarchyMapper();
        var result = mapper.mapLeavesToAncestors(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        // Ensure it's an immutable map (optional but good practice when using Map.of())
        assertThrows(UnsupportedOperationException.class, () -> result.put(1, new Hierarchy(null, null, null, null)));
    }

    @Test
    @DisplayName("Test case 6: Tree with a single BAC node that is a leaf (explicitly empty children)")
    void testSingleBACLeafWithEmptyChildren() {
        // BAC (1 - Leaf)
        var bac1 = new Node(104, Tier.BAC);

        var mapper = new HierarchyMapper();
        var result = mapper.mapLeavesToAncestors(bac1);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(104));

        var mapping = result.get(104);
        assertEquals(104, mapping.bacNodeId());
        assertNull(mapping.acNodeId());
        assertNull(mapping.scNodeId());
        assertNull(mapping.slNodeId());
    }

    @Test
    @DisplayName("Test case 7: Tree with a branch where a node has null children (is a leaf)")
    void testNodeWithNullChildren() {
        // BAC -> AC -> SC -> SL (Leaf)
        //        -> SC_null (Leaf with null children)
        var sl4 = new Node(401, Tier.SL);
        var sc3 = new Node(301, Tier.SC, sl4);
        var scNull = new Node(305, Tier.SC);
        var ac2 = new Node(201, Tier.AC, sc3, scNull);
        var bac1 = new Node(101, Tier.BAC, ac2);

        var mapper = new HierarchyMapper();
        var result = mapper.mapLeavesToAncestors(bac1);

        assertEquals(2, result.size());
        assertTrue(result.containsKey(401));
        assertTrue(result.containsKey(305));

        // Check leaf 401
        var mapping401 = result.get(401);
        assertEquals(101, mapping401.bacNodeId());
        assertEquals(201, mapping401.acNodeId());
        assertEquals(301, mapping401.scNodeId());
        assertEquals(401, mapping401.slNodeId());

        // Check leaf 305 (SC leaf with no children)
        var mapping305 = result.get(305);
        assertEquals(101, mapping305.bacNodeId());
        assertEquals(201, mapping305.acNodeId());
        assertEquals(305, mapping305.scNodeId()); // SC ancestor should be 305 (the leaf itself)
        assertNull(mapping305.slNodeId());
    }
}