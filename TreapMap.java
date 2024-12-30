package hw4;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Stack;

/**
 * Map implemented as a Treap.
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public class TreapMap<K extends Comparable<K>, V> implements OrderedMap<K, V> {

  /*** Do not change variable name of 'rand'. ***/
  private static Random rand;
  /*** Do not change variable name of 'root'. ***/
  private Node<K, V> root;
  private int size;

  /**
   * Make a TreapMap.
   */
  public TreapMap() {
    rand = new Random();
  }


  /**
   * Make a TreapMap with a seeded random number generator.
   * @param seed for the Random generation.
   */
  public TreapMap(int seed) {
    rand = new Random(seed);
  }

  @Override
  public void insert(K k, V v) throws IllegalArgumentException {
    /*
    If the child node that violates the heap order property is to the parent's right, then you need to rotate left.
    If the child is to the left of the parent, then you need to rotate right.
     */
    if (k == null) {
      throw new IllegalArgumentException("Key cannot be null");
    }
    if (root == null) { // tree is empty
      root = new Node<>(k, v);
    } else {
      root = insert(root, k, v);
    }
    size++;
  }

  private Node<K,V> insert(Node<K,V> currRoot, K k, V v) {
    if (currRoot == null) {
      return new Node<>(k, v);
    }
    if (k.compareTo(currRoot.key) > 0) { // node is greater than parent
      currRoot.right = insert(currRoot.right, k, v);
      // perform rotation with the parent
      if (currRoot.priority > currRoot.right.priority) {
        currRoot = rotateLeft(currRoot);
      }
    } else if (k.compareTo(currRoot.key) < 0) { // node is less than parent
      currRoot.left = insert(currRoot.left, k, v);
      // perform rotation with the parent
      if (currRoot.priority > currRoot.left.priority) {
        currRoot = rotateRight(currRoot);
      }
    } else {
      throw new IllegalArgumentException("Duplicate key found.");
    }

    return currRoot;
  }

  private Node<K,V> rotateRight(Node<K, V> n) {
    Node<K,V> newRoot = n.left;
    n.left = newRoot.right;
    newRoot.right = n;
    return newRoot;
  }

  private Node<K,V> rotateLeft(Node<K, V> n) {
    Node<K,V> newRoot = n.right;
    n.right = newRoot.left;
    newRoot.left = n;
    return newRoot;
  }

  @Override
  public V remove(K k) throws IllegalArgumentException {
    Node<K,V> n = find(k);
    if (n == null) {
      throw new IllegalArgumentException("Key not found.");
    }
    V value = n.value;
    root = remove(root, n);
    size--;
    return value;
  }

  private Node<K,V> remove(Node<K,V> currRoot, Node<K,V> toRemove) {
    if (currRoot == null) {
      return null;
    }
    if (currRoot.key.compareTo(toRemove.key) == 0) {
      currRoot.priority = Integer.MAX_VALUE;
      if (currRoot.left == null && currRoot.right == null) {
        return null;
      } else {
        currRoot = removeWithChildren(currRoot, toRemove);
      }
    } else if (currRoot.key.compareTo(toRemove.key) > 0) {
      currRoot.left = remove(currRoot.left, toRemove);
    } else {
      currRoot.right = remove(currRoot.right, toRemove);
    }
    return currRoot;
  }

  private Node<K,V> removeWithChildren(Node<K,V> currRoot, Node<K,V> toRemove) {
    if (currRoot.left == null) {
      return currRoot.right;
    } else if (currRoot.right == null) {
      return currRoot.left;
    } else {
      if (currRoot.left.priority < currRoot.right.priority) {
        currRoot = rotateRight(currRoot);
        currRoot.right = remove(currRoot.right, toRemove);
      } else if (currRoot.left.priority > currRoot.right.priority) {
        currRoot = rotateLeft(currRoot);
        currRoot.left = remove(currRoot.left, toRemove);
      }
      return currRoot;
    }
  }

  @Override
  public void put(K k, V v) throws IllegalArgumentException {
    Node<K,V> n = find(k);
    if (n == null) {
      throw new IllegalArgumentException();
    }
    n.value = v;
  }

  @Override
  public V get(K k) throws IllegalArgumentException {
    Node<K,V> node = find(k);
    if (node == null) {
      throw new IllegalArgumentException();
    }
    return node.value;
  }

  @Override
  public boolean has(K k) {
    if (k == null) {
      return false;
    }
    return find(k) != null;
  }

  // Return node for given key.
  private Node<K, V> find(K k) {
    if (k == null) {
      throw new IllegalArgumentException("cannot handle null key");
    }
    Node<K, V> n = root;
    while (n != null) {
      int cmp = k.compareTo(n.key);
      if (cmp < 0) {
        n = n.left;
      } else if (cmp > 0) {
        n = n.right;
      } else {
        return n;
      }
    }
    return null;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Iterator<K> iterator() {
    return new InorderIterator();
  }

  /*** Do not change this function's name or modify its code. ***/
  @Override
  public String toString() {
    return BinaryTreePrinter.printBinaryTree(root);
  }


  /**
   * Inner node class, each holds a key (which is what we sort the
   * BST by) as well as a value. We don't need a parent pointer as
   * long as we use recursive insert/remove helpers. Since this is
   * a node class for a Treap we also include a priority field.
   *
   * @param <K> Type for keys.
   * @param <V> Type for values.
   **/
  private static class Node<K, V> implements BinaryTreeNode {
    Node<K, V> left;
    Node<K, V> right;
    K key;
    V value;
    int priority;

    // Constructor to make node creation easier to read.
    Node(K k, V v) {
      // left and right default to null
      key = k;
      value = v;
      priority = generateRandomInteger();
    }

    // Use this function to generate random values
    // to use as node priorities as you insert new
    // nodes into your TreapMap.
    private int generateRandomInteger() {
      // Note: do not change this function!
      return rand.nextInt();
    }

    @Override
    public String toString() {
      return key + ":" + value + ":" + priority;
    }

    @Override
    public BinaryTreeNode getLeftChild() {
      return left;
    }

    @Override
    public BinaryTreeNode getRightChild() {
      return right;
    }

    // Feel free to add whatever you want to the Node class (e.g. new fields).
    // Just avoid changing any existing names, deleting any existing variables, or modifying the overriding methods.
  }

  // Iterative in-order traversal over the keys for a Treap map
  private class InorderIterator implements Iterator<K> {
    private final Stack<Node<K, V>> stack;

    InorderIterator() {
      stack = new Stack<>();
      pushLeft(root);
    }

    private void pushLeft(Node<K, V> curr) {
      while (curr != null) {
        stack.push(curr);
        curr = curr.left;
      }
    }

    @Override
    public boolean hasNext() {
      return !stack.isEmpty();
    }

    @Override
    public K next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Node<K, V> top = stack.pop();
      pushLeft(top.right);
      return top.key;
    }
  }
}
