// Written by sulis008
public class LinkedList<T extends Comparable<T>> implements List<T> {
    private Node<T> head;
    private int size;
    private boolean isSorted;
    public LinkedList() {
        head = null;
        isSorted = true;
        size = 0;
    }
    public boolean add(T element) {
        // Can't send null element
        if (element == null) return false;
        // Sets head if list was empty
        if (isEmpty()) {
            head = new Node<>(element);
        } else {
            // Otherwise add to the end of the list
            Node<T> ptr = head;
            while (ptr.getNext() != null) {
                ptr = ptr.getNext();
            }
            ptr.setNext(new Node<>(element));
        }
        size++;
        isSorted = checkSort();
        return true;
    }

    public boolean add(int index, T element) {
        // First case if adding to index 0 with an empty list
        if (isEmpty() && index == 0 && element != null) {
            head = new Node<>(element);
        } else if (element == null || index < 0 || index >= size) {
            return false;
        } else if (index == 0) {
            // Adding to the front of the list
            head = new Node<>(element, head);
        } else {
            // Anywhere else in the list
            Node<T> ptr = head;
            for (int i = 0; i < index-1; i++) {
                ptr = ptr.getNext();
            }
            ptr.setNext(new Node<>(element, ptr.getNext()));
        }
        size++;
        isSorted = checkSort();
        return true;
    }
    
    public void clear() {
        head = null;
        isSorted = true;
        size = 0;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        } else {
            // Traverseee
            Node<T> ptr = head;
            for (int i = 0; i < index; i++) {
                ptr = ptr.getNext();
            }
            return ptr.getData();
        }
    }

    public int indexOf(T element) {
        if (isSorted) {
            // Use Binary Search: O(log(n))
            int low = 0;
            int high = size-1;
            while (low <= high) {
                int mid = low + (high - low) / 2;
                // p: mid == 0, q: mid-1 < element, r: mid == element
                // (p ^ r) -> return mid || (q ^ r) -> return mid
                // (p ^ r) || (q ^ r) == (p || q) -> r
                if ((mid == 0 || get(mid-1).compareTo(element) < 0) && get(mid).equals(element)) {
                    return mid;
                } else if (get(mid).compareTo(element) < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            // If it gets here, element was not found
            return -1;
        } else {
            // Search linearly: O(n)
            for (int i = 0; i < size; i++) {
                if (get(i).equals(element)) return i;
            }
        }
        // If it gets here, element was not found
        return -1;
    }
    public boolean isEmpty() {
        // I believe that size == 0 iff head == null
        // and head == null iff size == 0.
        // size == 0 <-> head == null
        return (size == 0 || head == null);
    }
    public int size() {
        return size;
    }
    public void sort() {
        // O(n^2)
        if (!isSorted && !isEmpty() && size > 1) {
            Node<T> ptr;
            Node<T> mover;
            for (ptr = head; ptr.getNext() != null; ptr = ptr.getNext()) {
                for (mover = ptr.getNext(); mover != null; mover = mover.getNext()) {
                    int compare = ptr.getData().compareTo(mover.getData());
                    if (compare > 0) {
                        T tmp = ptr.getData();
                        ptr.setData(mover.getData());
                        mover.setData(tmp);
                    }
                }
            }
        }
        isSorted = true;
    }
    public T remove(int index) {
        // Out of bounds
        if (index < 0 || index >= size) return null;
        // Removing the only element
        if (size == 1) {
            T data = head.getData();
            this.clear();
            return data;
        }
        // Remove head
        if (index == 0) {
            T data = head.getData();
            head = head.getNext();
            size--;
            isSorted = checkSort();
            return data;
        } else if (index == size - 1) {
            // Last element in list
            Node<T> ptr = head;
            // Traverse to second to last node
            for (int i = 0; i < size-2; i++) {
                ptr = ptr.getNext();
            }
            T data = ptr.getNext().getData();
            ptr.setNext(null);
            size--;
            isSorted = checkSort();
            return data;
        } else {
            // Middle of list
            Node<T> ptr = head;
            for (int i = 0; i < index-1; i++) {
                ptr = ptr.getNext();
            }
            T data = ptr.getNext().getData();
            ptr.setNext(ptr.getNext().getNext());
            size--;
            isSorted = checkSort();
            return data;
        }
    }
    public void equalTo(T element) {
        if (element != null) {
            if (isSorted) {
                // Implement efficiently
                // Will find the range of index's that equal element
                // Example: 1-> 2-> 3-> 3-> 3-> 7-> null for 3 would give [2, 4] for [first, last]
                int first = this.indexOf(element);  // O(log(n))
                int last = first;
                // Conditions to break:
                // Last is at the last element in the list
                // The last's next number doesn't equal element
                while (true) {
                    if ((last+1 < size && !get(last+1).equals(element)) || last == size-1)
                        break;
                    else last++;
                }
                // Obtain proper subset of the LinkedList
                for (int i = size-1; i >= 0; i--) {
                    boolean toBeRemoved = (i > last)||(i < first);
                    if (toBeRemoved) this.remove(i);
                }
            } else {
                // Not sorted, go linearly through the list
                for (int i = size-1; i >= 0; i--) {
                    if (!get(i).equals(element)) {
                        this.remove(i);
                    }
                }
            }
            isSorted = true;
        }
    }
    public void reverse() {
        // Previous node to attach numbers to
        Node<T> previous = null;
        // Loop while there are more numbers to attach
        while (head != null) {
            Node<T> next = head.getNext();
            // Attach the number
            head.setNext(previous);
            // Move the attachment point
            previous = head;
            // Set beginning of list to the next value
            head = next;
        }
        // At the end, previous will be the new head
        head = previous;
        isSorted = checkSort();
    }
    public void merge(List<T> otherList) {
        if (otherList != null) {
            LinkedList<T> other = (LinkedList<T>) otherList;
            sort();
            other.sort();
            Node<T> origHead = head;
            Node<T> otherHead = other.head;
            Node<T> ptr;
            int origSize = size;
            int otherSize = other.size;
            // The following is to start off the merged list correctly, as it is different from the loop algorithm
            int compareFirst = origHead.getData().compareTo(otherHead.getData());
            if (compareFirst < 0) {
                head = origHead;
                origHead = origHead.getNext();
            } else {
                head = otherHead;
                otherHead = otherHead.getNext();
            }
            // Pointer node to set next nodes
            ptr = head;
            while (origHead != null && otherHead != null) {
                int compare = origHead.getData().compareTo(otherHead.getData());
                // Compare and set next accordingly
                if (compare < 0) {
                    ptr.setNext(origHead);
                    origHead = origHead.getNext();
                } else {
                    ptr.setNext(otherHead);
                    otherHead = otherHead.getNext();
                }
                // Important to move the pointer node with the algorithm
                ptr = ptr.getNext();
            }
            // In case list sizes are different, or one reaches null first
            if (origHead == null) {
                ptr.setNext(otherHead);
            } else {
                ptr.setNext(origHead);
            }
            // Updating the size of the list during the loop was not necessary
            // This is much simpler
            size = origSize + otherSize;
            isSorted = true;
        }
    }
    public void pairSwap() {
        // If even size
        if (size % 2 == 0) {
            for (Node<T> ptr = head; ptr != null; ptr = ptr.getNext().getNext()) {
                // Swap
                T tmp = ptr.getData();
                ptr.setData(ptr.getNext().getData());
                ptr.getNext().setData(tmp);
            }
        } else {
            // Odd size. Same EXACT THING, but stops at last index
            for (Node<T> ptr = head; ptr.getNext() != null; ptr = ptr.getNext().getNext()) {
                // Swap
                T tmp = ptr.getData();
                ptr.setData(ptr.getNext().getData());
                ptr.getNext().setData(tmp);
            }
        }
        // Check if pairSwap magically sorted the list!
        isSorted = checkSort();
    }
    public String toString() {
        if (isEmpty()) return "[]";
        String output = "[";
        for (int i = 0; i < size; i++) {
            if (i != size - 1) {
                output += get(i) + ", ";
            } else {
                output += get(i)+"]";
            }
        }
        return output;
    }
    public boolean isSorted() { return isSorted; }
    public boolean checkSort() {
        if (size <= 1) return true;
        for (Node<T> ptr = head; ptr.getNext() != null; ptr = ptr.getNext()) {
            int compare = ptr.getData().compareTo(ptr.getNext().getData());
            if (compare > 0) return false;
        }
        return true;
    }   // Helper method
}
