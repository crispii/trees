package hw4;

import exceptions.EmptyException;

/**
 * Priority Queue of ordered values.
 *
 * @param <T> Element type.
 */
public interface PriorityQueue<T extends Comparable<T>> extends Iterable<T> {
  /**
   * Insert a value.
   *
   * @param t Value to insert.
   */
  void insert(T t);

  /**
   * Remove best value.
   *
   * @throws EmptyException If queue is empty.
   */
  void remove() throws EmptyException;

  /**
   * Return best value.
   *
   * @return best value in the queue.
   * @throws EmptyException If queue is empty.
   */
  T best() throws EmptyException;

  /**
   * Check if no elements present.
   *
   * @return True if queue is empty, false otherwise.
   */
  boolean empty();
}
