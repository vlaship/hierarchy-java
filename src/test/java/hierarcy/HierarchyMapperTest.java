package hierarcy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HierarchyMapperTest {

    @Test
    @DisplayName("Test case 1: Simple full-depth tree with instruments")
    void testSimpleFullDepthTreeWithInstruments() {
        // BAC (1) -> AC (2) -> SC (3) -> SL (4 - with instruments)
        var guitar = new Instrument("Guitar");
        var piano = new Instrument("Piano");

        var sl4 = new Node(401, Tier.SL, List.of(guitar, piano));
        var sc3 = new Node(301, Tier.SC, List.of(), sl4);
        var ac2 = new Node(201, Tier.AC, List.of(), sc3);
        var bac1 = new Node(101, Tier.BAC, List.of(), ac2);

        var mapper = new HierarchyMapper();
        var result = mapper.mapInstrumentsToHierarchy(bac1);

        assertEquals(2, result.size());
        assertTrue(result.containsKey(guitar));
        assertTrue(result.containsKey(piano));

        var guitarHierarchy = result.get(guitar);
        assertEquals(101, guitarHierarchy.bacNodeId());
        assertEquals(201, guitarHierarchy.acNodeId());
        assertEquals(301, guitarHierarchy.scNodeId());
        assertEquals(401, guitarHierarchy.slNodeId());

        var pianoHierarchy = result.get(piano);
        assertEquals(101, pianoHierarchy.bacNodeId());
        assertEquals(201, pianoHierarchy.acNodeId());
        assertEquals(301, pianoHierarchy.scNodeId());
        assertEquals(401, pianoHierarchy.slNodeId());
    }

    @Test
    @DisplayName("Test case 2: Tree with instruments at different levels")
    void testTreeWithInstrumentsAtDifferentLevels() {
        // Structure:
        // BAC_1 [violin] -> AC_1 [drums] -> SC_1 -> SL_1 [guitar]
        //                                -> SC_2 [piano] -> SL_2 [flute]

        var violin = new Instrument("Violin");
        var drums = new Instrument("Drums");
        var guitar = new Instrument("Guitar");
        var piano = new Instrument("Piano");
        var flute = new Instrument("Flute");

        var sl1 = new Node(411, Tier.SL, List.of(guitar));
        var sl2 = new Node(422, Tier.SL, List.of(flute));
        var sc1 = new Node(311, Tier.SC, List.of(), sl1);
        var sc2 = new Node(322, Tier.SC, List.of(piano), sl2);
        var ac1 = new Node(211, Tier.AC, List.of(drums), sc1, sc2);
        var bac1 = new Node(111, Tier.BAC, List.of(violin), ac1);

        var mapper = new HierarchyMapper();
        var result = mapper.mapInstrumentsToHierarchy(bac1);

        assertEquals(5, result.size());

        // Check violin (at BAC level)
        var violinHierarchy = result.get(violin);
        assertEquals(111, violinHierarchy.bacNodeId());
        assertNull(violinHierarchy.acNodeId());
        assertNull(violinHierarchy.scNodeId());
        assertNull(violinHierarchy.slNodeId());

        // Check drums (at AC level)
        var drumsHierarchy = result.get(drums);
        assertEquals(111, drumsHierarchy.bacNodeId());
        assertEquals(211, drumsHierarchy.acNodeId());
        assertNull(drumsHierarchy.scNodeId());
        assertNull(drumsHierarchy.slNodeId());

        // Check guitar (at SL level through SC1)
        var guitarHierarchy = result.get(guitar);
        assertEquals(111, guitarHierarchy.bacNodeId());
        assertEquals(211, guitarHierarchy.acNodeId());
        assertEquals(311, guitarHierarchy.scNodeId());
        assertEquals(411, guitarHierarchy.slNodeId());

        // Check piano (at SC level)
        var pianoHierarchy = result.get(piano);
        assertEquals(111, pianoHierarchy.bacNodeId());
        assertEquals(211, pianoHierarchy.acNodeId());
        assertEquals(322, pianoHierarchy.scNodeId());
        assertNull(pianoHierarchy.slNodeId());

        // Check flute (at SL level through SC2)
        var fluteHierarchy = result.get(flute);
        assertEquals(111, fluteHierarchy.bacNodeId());
        assertEquals(211, fluteHierarchy.acNodeId());
        assertEquals(322, fluteHierarchy.scNodeId());
        assertEquals(422, fluteHierarchy.slNodeId());
    }

    @Test
    @DisplayName("Test case 3: Multiple instruments in single node")
    void testMultipleInstrumentsInSingleNode() {
        // BAC (1 - with multiple instruments)
        var guitar = new Instrument("Guitar");
        var piano = new Instrument("Piano");
        var drums = new Instrument("Drums");

        var bac1 = new Node(103, Tier.BAC, List.of(guitar, piano, drums));

        var mapper = new HierarchyMapper();
        var result = mapper.mapInstrumentsToHierarchy(bac1);

        assertEquals(3, result.size());

        for (var instrument : List.of(guitar, piano, drums)) {
            assertTrue(result.containsKey(instrument));
            var hierarchy = result.get(instrument);
            assertEquals(103, hierarchy.bacNodeId());
            assertNull(hierarchy.acNodeId());
            assertNull(hierarchy.scNodeId());
            assertNull(hierarchy.slNodeId());
        }
    }

    @Test
    @DisplayName("Test case 4: Tree with skipped SC tier")
    void testTreeWithSkippedTier() {
        // BAC -> AC -> SL (SC tier skipped)
        var trumpet = new Instrument("Trumpet");

        var sl4 = new Node(444, Tier.SL, List.of(trumpet));
        var ac3 = new Node(233, Tier.AC, List.of(), sl4);
        var bac2 = new Node(122, Tier.BAC, List.of(), ac3);

        var mapper = new HierarchyMapper();
        var result = mapper.mapInstrumentsToHierarchy(bac2);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(trumpet));

        var trumpetHierarchy = result.get(trumpet);
        assertEquals(122, trumpetHierarchy.bacNodeId());
        assertEquals(233, trumpetHierarchy.acNodeId());
        assertNull(trumpetHierarchy.scNodeId());
        assertEquals(444, trumpetHierarchy.slNodeId());
    }

    @Test
    @DisplayName("Test case 5: Empty tree (null root)")
    void testEmptyTreeNullRoot() {
        var mapper = new HierarchyMapper();
        var result = mapper.mapInstrumentsToHierarchy(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test case 6: Tree with no instruments")
    void testTreeWithNoInstruments() {
        // BAC -> AC -> SC -> SL (no instruments anywhere)
        var sl4 = new Node(401, Tier.SL, List.of());
        var sc3 = new Node(301, Tier.SC, List.of(), sl4);
        var ac2 = new Node(201, Tier.AC, List.of(), sc3);
        var bac1 = new Node(101, Tier.BAC, List.of(), ac2);

        var mapper = new HierarchyMapper();
        var result = mapper.mapInstrumentsToHierarchy(bac1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test case 7: Complex tree with mixed instrument placement")
    void testComplexTreeWithMixedInstrumentPlacement() {
        // BAC_1 [violin] -> AC_1 -> SC_1 [piano] -> SL_1 [guitar]
        //                         -> SC_2 -> SL_2 [drums]
        //                 -> AC_2 [flute] -> SC_3 [trumpet]

        var violin = new Instrument("Violin");
        var piano = new Instrument("Piano");
        var guitar = new Instrument("Guitar");
        var drums = new Instrument("Drums");
        var flute = new Instrument("Flute");
        var trumpet = new Instrument("Trumpet");

        var sl1 = new Node(411, Tier.SL, List.of(guitar));
        var sl2 = new Node(422, Tier.SL, List.of(drums));
        var sc1 = new Node(311, Tier.SC, List.of(piano), sl1);
        var sc2 = new Node(322, Tier.SC, List.of(), sl2);
        var sc3 = new Node(333, Tier.SC, List.of(trumpet));
        var ac1 = new Node(211, Tier.AC, List.of(), sc1, sc2);
        var ac2 = new Node(222, Tier.AC, List.of(flute), sc3);
        var bac1 = new Node(111, Tier.BAC, List.of(violin), ac1, ac2);

        var mapper = new HierarchyMapper();
        var result = mapper.mapInstrumentsToHierarchy(bac1);

        assertEquals(6, result.size());

        var violinHierarchy = result.get(violin);
        assertEquals(111, violinHierarchy.bacNodeId());
        assertNull(violinHierarchy.acNodeId());

        var pianoHierarchy = result.get(piano);
        assertEquals(111, pianoHierarchy.bacNodeId());
        assertEquals(211, pianoHierarchy.acNodeId());
        assertEquals(311, pianoHierarchy.scNodeId());
        assertNull(pianoHierarchy.slNodeId());

        var guitarHierarchy = result.get(guitar);
        assertEquals(111, guitarHierarchy.bacNodeId());
        assertEquals(211, guitarHierarchy.acNodeId());
        assertEquals(311, guitarHierarchy.scNodeId());
        assertEquals(411, guitarHierarchy.slNodeId());

        var drumsHierarchy = result.get(drums);
        assertEquals(111, drumsHierarchy.bacNodeId());
        assertEquals(211, drumsHierarchy.acNodeId());
        assertEquals(322, drumsHierarchy.scNodeId());
        assertEquals(422, drumsHierarchy.slNodeId());

        var fluteHierarchy = result.get(flute);
        assertEquals(111, fluteHierarchy.bacNodeId());
        assertEquals(222, fluteHierarchy.acNodeId());
        assertNull(fluteHierarchy.scNodeId());
        assertNull(fluteHierarchy.slNodeId());

        var trumpetHierarchy = result.get(trumpet);
        assertEquals(111, trumpetHierarchy.bacNodeId());
        assertEquals(222, trumpetHierarchy.acNodeId());
        assertEquals(333, trumpetHierarchy.scNodeId());
        assertNull(trumpetHierarchy.slNodeId());
    }
}