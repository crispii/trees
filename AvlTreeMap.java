package hw4;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Map implemented as an AVL Tree.
 *
 * @param <K> Type for keys.
 * @param <V> Type for values.
 */
public class AvlTreeMap<K extends Comparable<K>, V> implements OrderedMap<K, V> {

  /*** Do not change variable name of 'root'. ***/
  private Node<K, V> root;
  private int size;

  @Override
  public void insert(K k, V v) throws IllegalArgumentException {
    if (k == null || has(k)) {
      throw new IllegalArgumentException();
    } else if (size == 0) {
      root = new Node<>(k, v);
    } else {
      root = insert(root, k, v);
    }
    size++;
  }

  private Node<K,V> insert(Node<K, V> n, K k, V v) {
    if (n == null) {
      return new Node<>(k, v);
    }
    if (k.compareTo(n.key) > 0) { // node is greater than parent
      n.right = insert(n.right, k, v);
    } else if (k.compareTo(n.key) < 0) { // node is less than parent
      n.left = insert(n.left, k, v);
    } else {
      throw new IllegalArgumentException("Duplicate key found.");
    }
    if (balanceFactor(n) > 1 || balanceFactor(n) < -1) {
      n = performRotation(n);
    }
    return n;
  }

  private Node<K,V> performRotation(Node<K, V> n) {
    if (balanceFactor(n) > 1) { // left subtree is heavy
      if (balanceFactor(n.left) >= 0) { // left heavy or normal
        // single right rotation
        return rotateRight(n);
      } else { // right heavy
        // left-right rotation
        return rotateLeftRight(n);
      }
    } else if (balanceFactor(n) < -1) { // right subtree is heavy
      if (balanceFactor(n.right) <= 0) { // right heavy or normal
        // single left rotation
        return rotateLeft(n);
      } else { // left heavy
        // right-left rotation
        return rotateRightLeft(n);
      }
    }
    return n;
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

  private Node<K,V> rotateRightLeft(Node<K, V> n) {
    n.right = rotateRight(n.right);
    return rotateLeft(n);
  }

  private Node<K,V> rotateLeftRight(Node<K, V> n) {
    n.left = rotateLeft(n.left);
    return rotateRight(n);
  }

  private int balanceFactor(Node<K, V> n) {
    if (n == null) {
      return 0;
    }
    return height(n.left) - height(n.right);
  }

  private int height(Node<K, V> n) {
    if (n == null) {
      return -1;
    }
    int leftHeight = height(n.left);
    int rightHeight = height(n.right);

    if (leftHeight > rightHeight) {
      return leftHeight + 1;
    } else {
      return rightHeight + 1;
    }
  }

  @Override
  public V remove(K k) throws IllegalArgumentException {
    Node<K,V> n = find(k);
    if (n == null) {
      throw new IllegalArgumentException();
    }
    V value = n.value;
    root = remove(root, n);
    size--;
    return value;
  }

  private Node<K,V> remove(Node<K,V> currRoot, Node<K, V> toRemove) {
    int cmp = currRoot.key.compareTo(toRemove.key);
    if (cmp == 0) {
      return remove(currRoot);
    } else if (cmp > 0) {
      currRoot.left = remove(currRoot.left, toRemove);
    } else {
      currRoot.right = remove(currRoot.right, toRemove);
    }
    if (balanceFactor(currRoot) > 1 || balanceFactor(currRoot) < -1) {
      currRoot = performRotation(currRoot);
    }
    return currRoot;
  }

  // Remove given node and return the remaining tree (structural change).
  private Node<K, V> remove(Node<K, V> node) {
    // Easy if the node has 0 or 1 child.
    if (node.right == null) {
      return node.left;
    } else if (node.left == null) {
      return node.right;
    }

    // If it has two children, find the predecessor (max in left subtree),
    Node<K, V> toReplaceWith = max(node);
    // then copy its data to the given node (value change),
    node.key = toReplaceWith.key;
    node.value = toReplaceWith.value;
    // then remove the predecessor node (structural change).
    node.left = remove(node.left, toReplaceWith);

    if (balanceFactor(node) > 1 || balanceFactor(node) < -1) {
      node = performRotation(node);
    }

    return node;
  }

  // Return a node with maximum key in subtree rooted at given node.
  private Node<K, V> max(Node<K, V> node) {
    Node<K, V> curr = node.left;
    while (curr.right != null) {
      curr = curr.right;
    }
    return curr;
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

  @Override
  public int size() {
    return size;
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
   * long as we use recursive insert/remove helpers.
   *
   * @param <K> Type for keys.
   * @param <V> Type for values.
   **/
  private static class Node<K, V> implements BinaryTreeNode {
    Node<K, V> left;
    Node<K, V> right;
    K key;
    V value;

    // Constructor to make node creation easier to read.
    Node(K k, V v) {
      // left and right default to null
      key = k;
      value = v;
    }

    @Override
    public String toString() {
      return key + ":" + value;
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

  // Iterative in-order traversal over the keys
  private class InorderIterator implements Iterator<K> {
    private final Stack<AvlTreeMap.Node<K, V>> stack;

    InorderIterator() {
      stack = new Stack<>();
      pushLeft(root);
    }

    private void pushLeft(AvlTreeMap.Node<K, V> curr) {
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
      AvlTreeMap.Node<K, V> top = stack.pop();
      pushLeft(top.right);
      return top.key;
    }
  }
}
