import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();
    static OffByOne offByOne = new OffByOne();
    static OffByN offBy2 = new OffByN(2);

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testIsPalindrome() {
        String word1 = "";
        boolean actual1 = palindrome.isPalindrome(word1);
        assertTrue(actual1);

        String word2 = "aba";
        boolean actual2 = palindrome.isPalindrome(word2);
        assertTrue(actual2);

        String word3 = "ahha";
        boolean actual3 = palindrome.isPalindrome(word3);
        assertTrue(actual3);

        String word4 = "haha";
        boolean actual4 = palindrome.isPalindrome(word4);
        assertFalse(actual4);

    }

    @Test
    public void testIsPalindromeWithComparator() {
        //Test palindrome offby 1
        assertTrue(palindrome.isPalindrome("flake", offByOne));
        assertTrue(palindrome.isPalindrome("flke", offByOne));
        assertFalse(palindrome.isPalindrome("aha", offByOne));

        //Test palindrome offby 2
        assertTrue(palindrome.isPalindrome("wormy", offBy2));
        assertFalse(palindrome.isPalindrome("flake", offBy2));
    }
}
