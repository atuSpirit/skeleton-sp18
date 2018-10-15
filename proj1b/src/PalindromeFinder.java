/** This class outputs all palindromes in the words file in the current directory. */
public class PalindromeFinder {

    public static void main(String[] args) {
        int minLength = 4;
        Palindrome palindrome = new Palindrome();

        int maxCount = 0;
        int maxN = 1;
        //OffByOne offByOne = new OffByOne();
        for (int N = 1; N < 50; N++) {
            OffByN offByN = new OffByN(N);
            In in = new In("../library-sp18/data/words.txt");

            int count = 0;
            while (!in.isEmpty()) {
                String word = in.readString();
                if (word.length() >= minLength && palindrome.isPalindrome(word, offByN)) {
                    //System.out.println(word);
                    count++;
                }
            }
            System.out.println(count);
            if (count > maxCount) {
                maxCount = count;
                maxN = N;
            }
        }
        System.out.println("The off " + maxN + " has the most panlindromes, total " + maxCount);
    }
}
