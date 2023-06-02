// Written by sulis008
public class ArrayList<T extends Comparable<T>> implements List<T> {
    private T[] array;
    private boolean isSorted;
    private int size;
    public ArrayList() {
        array = (T[]) new Comparable[2];
        isSorted = true;
        size = 0;
    }
    public boolean add(T element) {
        if (element == null) return false;
        if (size == array.length) {
            this.arrayExpand();
        }
        array[size] = element;
        size++;
        isSorted = checkSort();
        return true;
    }
    public boolean add(int index, T element) {
        if (element != null && size == 0 && index == 0) {
            array[0] = element;
            size++;
        }
        if (element == null || index < 0 || index >= size) return false;
        // If array not full, shift numbers one to the right.
        if (array.length != size) {
            for (int i = size; i > 0; i--) {
                // Last shift needed:
                if (i-1 == index) {
                    array[i] = array[i-1];
                    array[i-1] = element;
                    size += 1;  // I noticed I can only have the size increase here, otherwise size would increase twice
                    break;
                } else {
                    // More shifts needed
                    array[i] = array[i-1];
                    array[i-1] = null;
                }
            }
        } else {
            // It is full, so expand and use the same algorithm.
            this.arrayExpand();
            this.add(index, element);
        }
        // Used to have size++ right here, but it increased size twice because of the "recursive" call.
        isSorted = checkSort();
        return true;
    }
    public void clear() {
        array = (T[]) new Comparable[2];
        isSorted = true;
        size = 0;
    }
    public T get(int index) {
        if (index < 0 || index >= size) return null;
        return array[index];
    }
    public int indexOf(T element) {
        if (isSorted) {
            // Use Binary Search to get O(log(n))
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
        return size == 0;
    }
    public int size() {
        return size;
    }
    public void sort() {
        // O(n^2)
        if (!isSorted && size > 1)
            for (int i = 0; i < size-1; i++)
                for (int j = i+1; j < size; j++) {
                    int compare = array[i].compareTo(array[j]);
                    if (compare > 0) {
                        T temp = array[i];
                        array[i] = array[j];
                        array[j] = temp;
                    }
                }
        isSorted = true;
    }
    public T remove(int index) {
        if (index < 0 || index >= size) return null;
        // Save data
        T data = array[index];
        array[index] = null;
        // Move empty spot to the end of the array
        for (int i = index; i < size-1; i++) {
            array[i] = array[i+1];
            array[i+1] = null;
        }
        size--;
        isSorted = checkSort();
        return data;
    }
    public void equalTo(T element) {
        if (element != null) {
            if (isSorted) {
                int first = indexOf(element);
                int last = first;
                while (last+1 != size && array[last+1].equals(element))
                    last++;
                for (int i = size-1; i >= 0; i--) {
                    boolean toBeRemoved = i > last || i < first;
                    if (toBeRemoved) remove(i);
                }
            } else {
                // Remove linearly
                for (int i = size-1; i >= 0; i--)
                    if (!array[i].equals(element)) remove(i);
                }
            // The remove() method checks and updates isSorted in real time
            }
        }
    public void reverse() {
        int start = 0;
        int end = size-1;
        // Swap first and last values, second and second to last vales, etc. until middle is reached.
        while (start < end) {
            T temp = array[start];
            array[start] = array[end];
            array[end] = temp;
            start++;
            end--;
        }
        isSorted = checkSort();
    }
    public void merge(List<T> otherList) {
        ArrayList<T> other = (ArrayList<T>) otherList;
        sort();
        other.sort();
        int originalSize = size;
        int otherSize = other.size;
        if (!other.isEmpty()) {
            T[] merged = (T[]) new Comparable[size + other.size];
            int index = 0;
            while (!isEmpty() && !other.isEmpty()) {
                int compare = array[0].compareTo(other.array[0]);
                if (compare < 0) {
                    merged[index] = array[0];
                    // When it is determined which value to add next, the front of that array is chopped off.
                    remove(0);
                } else {
                    merged[index] = other.array[0];
                    // When it is determined which value to add next, the front of that array is chopped off.
                    other.remove(0);
                }
                index++;
            }
            // Add whatever is remaining to be added
            if (isEmpty()) {
                for (int i = 0; i < other.size; i++) {
                    merged[index] = other.array[i];
                    index++;
                }
            } else {
                for (int i = 0; i < size; i++) {
                    merged[index] = array[i];
                    index++;
                }
            }
            this.array = merged;
            size = originalSize + otherSize;
            isSorted = true;
        }
    }
    public void pairSwap() {
        // If even number of elements:
        if (size % 2 == 0) {
            // Swap them all!
            for (int i = 0; i < size-1; i+=2) {
                T temp = array[i];
                array[i] = array[i+1];
                array[i+1] = temp;
            }
        } else {
            // Odd number of elements. Last element is saved, chopped off, and sent to pairSwap.
            T end = array[size-1];
            remove(size-1);
            pairSwap();
            // Saved last element added back.
            add(end);
        }
        isSorted = checkSort();
    }
    public boolean isSorted() {
        return isSorted;
    }
    public String toString() {
        if (size == 0) return "[]";
        String output = "[";
        for (int i = 0; i < size; i++) {
            if (i != size-1)
                output += array[i] + ", ";
            else
                output += array[i] + "]";
        }
        return output;
    }
    private boolean checkSort() {
        if (size <= 1) return true;
        for (int i = 0; i < size-1; i++) {
            // Reached the end of the array when element is null
            if (array[i] == null)
                break;
            // Assert ascending order
            if (array[i].compareTo(array[i+1]) > 0)
                return false;
        }
        return true;
    }   // Helper method
    private void arrayExpand() {
        // Expands the mutable array to two times the previous capacity
        T[] expanded = (T[]) new Comparable[array.length * 2];
        for (int i = 0; i < size; i++) {
            expanded[i] = array[i];
        }
        this.array = expanded;
    }   // Helper method
}
