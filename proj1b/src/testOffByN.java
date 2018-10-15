import org.junit.Test;
import static org.junit.Assert.*;

public class testOffByN {

    @Test
    public void testEqualChars() {
        int N = 2;
        OffByN offBy2 = new OffByN(N);
        assertTrue(offBy2.equalChars('a', 'c'));
        assertTrue(offBy2.equalChars('c', 'a'));
        assertFalse(offBy2.equalChars('a', 'z'));
    }
}
