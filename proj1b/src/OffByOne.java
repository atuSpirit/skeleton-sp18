public class OffByOne implements CharacterComparator {
    @Override
    /* If two character is off by one, return true */
    public boolean equalChars(char x, char y) {
        if (Math.abs(x - y) == 1) {
            return true;
        }
        return false;
    }
}
