public class OffByN implements CharacterComparator {
    int N;

    public OffByN(int N) {
        this.N = N;
    }
    @Override
    /* If two character is off by one, return true */
    public boolean equalChars(char x, char y) {
        if (Math.abs(x - y) == this.N) {
            return true;
        }
        return false;
    }
}
