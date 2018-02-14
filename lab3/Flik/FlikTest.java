import static org.junit.Assert.*;
import org.junit.Test;

public class FlikTest {
    /**
     * Test Flik.isSameNumber()
     */
    @Test
    public void testIsSameNumber() {
        Integer a = 1;
        Integer b = -2;
        assertFalse(Flik.isSameNumber(a, b));
//        assertTrue("false", Flik.isSameNumber(a, b));
        assertTrue(Flik.isSameNumber(0, 0));
        a = 129;
        b = 129;
        assertTrue(Flik.isSameNumber(a,b));
/*
        a = Integer.MAX_VALUE;
        b = Integer.MAX_VALUE;
        assertTrue(Flik.isSameNumber(a, b));

        a = Integer.MIN_VALUE;
        b = Integer.MIN_VALUE;
        assertTrue(Flik.isSameNumber(a, b));
       */
    }

}
