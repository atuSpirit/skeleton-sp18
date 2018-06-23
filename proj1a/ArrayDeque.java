/* Create a double linked list using array */
public class ArrayDeque<T> {
    T[] item;
    int nextFirst;
    int size;
    int nextLast;


    public ArrayDeque() {
        item = (T[]) new Object[8];
        nextFirst = 0;
        nextLast = 1;
        size = 0;
    }

    /* Helper function to compute index - 1 */
    private int minusOne(int index) {
        return ((index - 1) + item.length) % item.length;
    }

    /* Helper function to compute index + 1 */
    private int addOne(int index) {
        return (index + 1) % item.length;
    }

    /* Function to resize the array.  If the elements number
       is larger than capacity, increase the capacity of the
       array to two times.  If the element number is smaller
       than half of the capacity, shrink the array by two times.
     */
    private void resize(int capacity) {
        T[] newArray = (T[]) new Object[capacity];
        int index = nextFirst;
        int i = 0;
        while (i < size) {
            index = addOne(index);
            newArray[i] = item[index];
            item[index] = null;
            i++;
        }
        item = newArray;
        nextFirst = minusOne(0);
        nextLast = size;
    }

    /* Adds an item of type T to the front of the deque. */
    public void addFirst(T element) {
        item[nextFirst] = element;
        size++;
        nextFirst = minusOne(nextFirst);
        if (addOne(nextFirst) == nextLast) {
            resize(size * 2);
        }
    }

    /* Adds an item of type T to the back of the deque.*/
    public void addLast(T element) {
        item[nextLast] = element;
        size++;
        nextLast = addOne(nextLast);
        if (addOne(nextFirst) == nextLast) {
            resize(size * 2);
        }
    }
    /* Returns true if deque is empty, false otherwise. */
    public boolean isEmpty() {
        if (0 == size) {
            return true;
        }
        return false;

    }
    /*  Returns the number of items in the deque. */
    public int size() {
        return size;

    }
    /* Prints the items in the deque from first to last,
       separated by a space. */
    public void printDeque() {
        int i = 0;
        int index = addOne(nextFirst);
        while (i < size()) {
            System.out.print(item[index].toString());
            System.out.print(" ");
            index = addOne(index);
            i++;
        }
        System.out.println();
    }
    /* Removes and returns the item at the front of the deque.
       If no such item exists, returns null.
     */
    public T removeFirst() {
        if (size() == 0) {
            return null;
        }
        T element = item[addOne(nextFirst)];
        item[addOne(nextFirst)] = null;
        nextFirst = addOne(nextFirst);
        size--;
        if ((item.length >= 16) && (size / (float) item.length < 0.25)) {
//            System.out.println("size: " + size + "item.length " + item.length);
            resize(item.length / 2);
        }
        return element;
    }
    /* Removes and returns the item at the back of the deque.
       If no such item exists, returns null.
     */
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T element = item[minusOne(nextLast)];
        item[minusOne(nextLast)] = null;
        nextLast = minusOne(nextLast);
        size--;
        if ((item.length >= 16) && (size / (float) item.length < 0.25)) {
            resize(item.length / 2);
        }

        return element;
    }
    /*  Gets the item at the given index, where 0 is the front,
        1 is the next item, and so forth. If no such item exists,
        returns null.
     */
    public T get(int index) {
        if (0 == size) {
            return null;
        }
        if (index < 0 || index > (size - 1)) {
            System.err.println("Index out of boundary");
            return null;
        }
        index = (nextFirst + 1 + index) % item.length;
        return item[index];
    }
}
