/** Perform some basic Array List test */
public class ArrayDequeTest {

    public static boolean checkEmpty(boolean expected, boolean actual) {
        if (actual != expected) {
            System.out.println("IsEmpty() is expected to be " + expected + " but the actural is " + actual);
            return false;
        }
        return true;
    }

    public static boolean checkSize(int expectedSize, int actualSize) {
        if (actualSize != expectedSize) {
            System.out.println("size() should be " + expectedSize + " but returns " + actualSize);
            return false;
        }
        return true;
    }

    /** print the status of the test */
    public static boolean checkSizeTest() {
        boolean passed = true;
        ArrayDeque<String> stringArrayDeque = new ArrayDeque<>();
        stringArrayDeque.addFirst("first");
        passed = passed & checkSize(1, stringArrayDeque.size());
        stringArrayDeque.addLast("middle");
        passed = passed & checkSize(2, stringArrayDeque.size());
        stringArrayDeque.addLast("last");
        passed = passed & checkSize(3, stringArrayDeque.size());

        System.out.println("Print out the Deque: ");
        stringArrayDeque.printDeque();

        stringArrayDeque.removeFirst();
        passed = passed & checkSize(2, stringArrayDeque.size());
        System.out.println("Print out the Deque: ");
        stringArrayDeque.printDeque();

        stringArrayDeque.removeLast();
        passed = passed & checkSize(1, stringArrayDeque.size());
        System.out.println("Print out the Deque: ");
        stringArrayDeque.printDeque();

        stringArrayDeque.removeLast();
        passed = passed & checkEmpty(true, stringArrayDeque.isEmpty());
        System.out.println("Print out the Deque: ");
        stringArrayDeque.printDeque();
        return passed;
    }

    /** Test addFirst() */
    public static boolean addFirstTest() {
        /* add 100 items */
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            intDeque.addFirst(i);
        }
        System.out.println("Print out the Deque: ");
        intDeque.printDeque();

        return true;
    }

    /** Test addLast() */
    public static boolean addLastTest() {
        /* add 100 items */
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            intDeque.addLast(i);
        }
        System.out.println("Print out the Deque: ");
        intDeque.printDeque();

        return true;
    }

    /** Test removeFirst() in ArrayDeque */
    public static boolean removeFirstTest() {
        /* add 100 items */
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            intDeque.addFirst(i);
        }
        System.out.println("Original array: ");
        intDeque.printDeque();

        //Delete 98 elements from last
        for (int i = 0; i < 98; i++) {
            intDeque.removeFirst();
        }
        System.out.println("Removed array: ");
        intDeque.printDeque();

        return true;
    }

    /** Test removeLast() in ArrayDeque */
    public static boolean removeLastTest() {
        /* add 100 items */
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            intDeque.addLast(i);
        }
        System.out.println("Original array: ");
        intDeque.printDeque();

        //Delete 98 elements from last
        for (int i = 0; i < 98; i++) {
            intDeque.removeLast();
        }
        System.out.println("Removed array: ");
        intDeque.printDeque();

        return true;
    }

    /** Test ArrayDeque.get() */
    public static boolean testGet() {
        /* add 100 items */
        ArrayDeque<Integer> intDeque = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            intDeque.addLast(i);
        }
        System.out.println("Print out the Deque: ");
        intDeque.printDeque();

        System.out.println("The first item: " + intDeque.get(0));
        System.out.println("The last item: " + intDeque.get(99));
        System.out.println("The 32 item: " + intDeque.get(32));

        return true;
    }

    /** Print the pass message */
    public static void printPassMessage(boolean passed) {
        if (passed) {
            System.out.println("All test are passed!");
        } else {
            System.out.println("Test failed!");
        }
    }

    public static void main(String[] args) {
        printPassMessage(checkSizeTest() & addFirstTest() & addLastTest()
                & removeFirstTest() & removeLastTest() & testGet());

    }
}
