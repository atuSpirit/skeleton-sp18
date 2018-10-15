public class Palindrome {
    /* Store characters in a word into Deque
     */
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> wordDeque = new ArrayDeque<>();

        int wordLen = word.length();
        for (int i = 0; i < wordLen; i++) {
            Character c = word.charAt(i);
            wordDeque.addLast(c);
        }
        return wordDeque;
    }

    /* Judge whether one word is a palindrome or not.
       Return true or false
     */
    public boolean isPalindrome(String word) {
        Deque<Character> wordDeque = wordToDeque(word);
        while (wordDeque.size() > 1) {
            Character first = wordDeque.removeFirst();
            Character last = wordDeque.removeLast();

            if (first != last) {
                return false;
            }
        }
        return true;
    }

    /* Judge whether one word is a palindrome or not.
       Whether two characters are same or not is decided by character comparator
       Return true or false
     */
    public boolean isPalindrome(String word, CharacterComparator c) {
        Deque<Character> wordDeque = wordToDeque(word);
        while (wordDeque.size() > 1) {
            Character first = wordDeque.removeFirst();
            Character last = wordDeque.removeLast();

            if (!c.equalChars(first, last)) {
                return false;
            }
        }
        return true;
    }

}
