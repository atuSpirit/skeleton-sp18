/** Create a double linked list */
public class LinkedListDeque<T> implements Deque<T> {
    private class ItemNode {
        private ItemNode pre;
        private T item;
        private ItemNode next;

        ItemNode(ItemNode pre, T item, ItemNode next) {
            this.pre = pre;
            this.item = item;
            this.next = next;
        }

        ItemNode(T item) {
            this.pre = this;
            this.item = item;
            this.next = this;
        }
    }

    ItemNode sentinel;
    int size;

    /**
     * Construction method to build a double linked list
     * given sentinel item.
      */
    public LinkedListDeque() {
        sentinel = new ItemNode(null, null, null);
        sentinel.pre = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    /* Adds an item of type T to the front of the deque. */
    public void addFirst(T item) {
        ItemNode first = new ItemNode(sentinel, item, sentinel.next);
        sentinel.next.pre = first;
        sentinel.next = first;
        size++;

    }

    @Override
    /* Adds an item of type T to the back of the deque.*/
    public void addLast(T item) {
        ItemNode last = new ItemNode(sentinel.pre, item, sentinel);
        sentinel.pre.next = last;
        sentinel.pre = last;
        size++;
    }

    @Override
    /* Returns true if deque is empty, false otherwise. */
    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    @Override
    /*  Returns the number of items in the deque. */
    public int size() {
        return size;

    }

    @Override
    /* Prints the items in the deque from first to last,
       separated by a space. */
    public void printDeque() {
        ItemNode node = sentinel.next;
        while (node != sentinel) {
            System.out.print(node.item.toString() + " ");
            node = node.next;
        }
    }

    @Override
    /* Removes and returns the item at the front of the deque.
       If no such item exists, returns null.
     */
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        ItemNode first = sentinel.next;
        first.next.pre = sentinel;
        sentinel.next = first.next;
        first.pre = null;
        first.next = null;
        size--;
        return first.item;
    }

    @Override
    /* Removes and returns the item at the back of the deque.
       If no such item exists, returns null.
     */
    public T removeLast() {
        if (0 == size) {
            return null;
        }
        ItemNode last = sentinel.pre;
        last.pre.next = sentinel;
        sentinel.pre = last.pre;
        last.pre = null;
        last.next = null;
        size--;
        return last.item;
    }

    @Override
    /*  Gets the item at the given index, where 0 is the front,
        1 is the next item, and so forth. If no such item exists,
        returns null.
     */
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int i = 0;
        ItemNode p = sentinel.next;
        while (i < index) {
            p = sentinel.next;
            i++;
        }
        return p.item;
    }
}
