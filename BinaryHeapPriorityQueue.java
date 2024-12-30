package hw4;

import exceptions.EmptyException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Priority queue implemented as a binary heap with a ranked array representation.
 *
 * @param <T> Element type.
 */
public class BinaryHeapPriorityQueue<T extends Comparable<T>> implements PriorityQueue<T> {
  private final List<T> heap;
  private Comparator<T> cmp;
  private int size;

  /**
   * Make a BinaryHeapPriorityQueue.
   */
  public BinaryHeapPriorityQueue() {
    this(new DefaultComparator<>());
  }

  /**
   * Make a BinaryHeapPriorityQueue with a custom comparator.
   *
   * @param cmp Comparator to use.
   */
  public BinaryHeapPriorityQueue(Comparator<T> cmp) {
    this.cmp = cmp;
    heap = new ArrayList<>();
    heap.add(null); // Add a dummy element at index 0 to simplify arithmetic
    size = 0;
  }

  @Override
  public void insert(T t) {
    /*
    1.) insert at the worst element in the list (at the end)
    2.) swim the element up if needed. (compare to its parent)
     */
    heap.add(t);
    size++;

    int currIndex = size;
    int parentIndex = currIndex / 2;
    while (parentIndex > 0 && cmp.compare(heap.get(parentIndex), heap.get(currIndex)) < 0) {
      T temp = heap.get(parentIndex);
      heap.set(parentIndex, heap.get(currIndex));
      heap.set(currIndex, temp);

      currIndex = parentIndex;
      parentIndex = currIndex / 2;
    }
  }

  @Override
  public void remove() throws EmptyException {
    if (empty()) {
      throw new EmptyException();
    }
    T temp = heap.get(1);
    heap.set(1, heap.get(size));
    heap.set(size, temp);
    heap.remove(size);
    size--;

    swimDown(1);
  }

  private void swimDown(int currIndex) {
    while (true) {
      int leftChildIndex = 2 * currIndex;
      int rightChildIndex = 2 * currIndex + 1;
      int bestIndex = currIndex;
      // compare current index to left child
      if (leftChildIndex <= size && cmp.compare(heap.get(leftChildIndex), heap.get(bestIndex)) > 0) {
        bestIndex = leftChildIndex;
      }
      // compare current index to right child
      if (rightChildIndex <= size && cmp.compare(heap.get(rightChildIndex), heap.get(bestIndex)) > 0) {
        bestIndex = rightChildIndex;
      }
      // stop while loop once the current index is the best out of its children
      if (bestIndex == currIndex) {
        break;
      }

      // if the current is not the best, swap with best
      T temp2 = heap.get(bestIndex);
      heap.set(bestIndex, heap.get(currIndex));
      heap.set(currIndex, temp2);

      currIndex = bestIndex;
    }
  }

  @Override
  public T best() throws EmptyException {
    if (empty()) {
      throw new EmptyException();
    } else {
      return heap.get(1);
    }
  }

  @Override
  public boolean empty() {
    return heap.size() < 2;
  }

  @Override
  public Iterator<T> iterator() {
    return new InorderIterator();
  }

  // Default comparator is the natural order of elements that are Comparable.
  private static class DefaultComparator<T extends Comparable<T>> implements Comparator<T> {
    public int compare(T t1, T t2) {
      return t1.compareTo(t2);
    }
  }

  // Iterative in-order traversal over the keys
  private class InorderIterator implements Iterator<T> {
    private int index;

    InorderIterator() {
      index = 1;
    }

    @Override
    public boolean hasNext() {
      return index <= size;
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      T temp = heap.get(index);
      index++;
      return temp;
    }
  }
}
