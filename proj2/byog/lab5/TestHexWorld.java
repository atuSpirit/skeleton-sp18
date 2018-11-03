package byog.lab5;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestHexWorld {
    HexWorld hexWorld;

    @Before
    public void setUp() {
        hexWorld = new HexWorld();
    }

    @Test
    public void testWidthOfThisRow() {
        assertEquals(4, hexWorld.widthOfThisRow(0, 4));
        assertEquals(6, hexWorld.widthOfThisRow(1, 4));
        assertEquals(8, hexWorld.widthOfThisRow(2, 4));
        assertEquals(10, hexWorld.widthOfThisRow(3, 4));
        assertEquals(10, hexWorld.widthOfThisRow(4, 4));
        assertEquals(8, hexWorld.widthOfThisRow(5, 4));
        assertEquals(6, hexWorld.widthOfThisRow(6, 4));
        assertEquals(4, hexWorld.widthOfThisRow(7, 4));

    }
    @Test
    public void testXStartOffset() {
        assertEquals(0, hexWorld.xStartOffset(0, 4));
        assertEquals(-1, hexWorld.xStartOffset(1, 4));
        assertEquals(-2, hexWorld.xStartOffset(2, 4));
        assertEquals(-3, hexWorld.xStartOffset(3, 4));
        assertEquals(-3, hexWorld.xStartOffset(4, 4));
        assertEquals(-2, hexWorld.xStartOffset(5, 4));
        assertEquals(-1, hexWorld.xStartOffset(6, 4));
        assertEquals(0, hexWorld.xStartOffset(7, 4));
    }

    @Test
    public void testFindFirstHexagon() {
        assertEquals(new Position(24, 0), hexWorld.findFirstHexagon(2));
        assertEquals(new Position(24, 0), hexWorld.findFirstHexagon(3));
        assertEquals(new Position(23, 0), hexWorld.findFirstHexagon(4));
        assertEquals(new Position(23, 0), hexWorld.findFirstHexagon(5));
    }

    @Test
    public void testFindLeftHexagon() {
        Position realPosition1 = new Position(16, 4);
        assertEquals(realPosition1, hexWorld.findLeftHexagon(new Position(23, 0), 4));
        Position realPosition2 = new Position(9, 8);
        assertEquals(realPosition2, hexWorld.findLeftHexagon(realPosition1, 4));
    }

    @Test
    public void testFindRightHexagon() {
        Position realPosition1 = new Position(30, 4);
        assertEquals(realPosition1, hexWorld.findRightHexagon(new Position(23, 0), 4));
        Position realPosition2 = new Position(37, 8);
        assertEquals(realPosition2, hexWorld.findRightHexagon(realPosition1, 4));
    }

}
