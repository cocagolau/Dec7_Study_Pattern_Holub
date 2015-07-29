package me.dec7.study.holub.learning;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Tree {
	
	private Node root = null;
	
	private static class Node {
		
		public Node left, right;
		
		public Object item;
		
		public Node(Object item) {
			this.item = item;
		}
	}
	
	// passive iterator
	public interface Examiner {
		public void examine(Object currentNode);
	}
	
	public void traverse(Examiner client) {
		traverseInOrder(root, client);
	}
	
	private void traverseInOrder(Node current, Examiner client) {
		if (current == null) {
			return;
		}
		
		traverseInOrder(current.left, client);
		client.examine(current.item);
		traverseInOrder(current.right, client);
	}
	
	public void add(Object item) {
		
		if (root == null) {
			root = new Node(item);
		} else {
			insertItem(root, item);
		}
	}
	
	// active iterator
	public Iterator iterator() {
		
		return new Iterator() {
			
			private Node current = root;
			
			private LinkedList stack = new LinkedList();

			@Override
			public boolean hasNext() {
				
				return !(current == null && stack.size() == 0);
			}

			@Override
			public Object next() {
				
				while (current != null) {
					stack.addFirst(current);
					current = current.left;
				}
				
				if (stack.size() != 0) {
					current = (Node) (stack.removeFirst());
					Object toReturn = current.item;
					current = current.right;
					
					return toReturn;
				}
				
				throw new NoSuchElementException();
			}
			
			public void remove() {
				
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	private void insertItem(Node current, Object item) {
		
		if (current.item.toString().compareTo(item.toString()) > 0) {
			if (current.left == null) {
				current.left = new Node(item);
			} else {
				insertItem(current.left, item);
			}
		} else {
			if (current.right == null) {
				current.right = new Node(item);
			} else {
				insertItem(current.right, item);
			}
		}
	}
	
	public static void main(String[] args) {
		Tree t = new Tree();
		t.add("D");
		t.add("B");
		t.add("F");
		t.add("A");
		t.add("C");
		t.add("E");
		t.add("G");
		
		Iterator i = t.iterator();
		while (i.hasNext()) {
			System.out.println(i.next().toString());
			System.out.println(" ");
			
			t.traverse(new Examiner() {

				@Override
				public void examine(Object currentNode) {
					System.out.println(currentNode.toString());
				}
				
			});
		}
		System.out.println(" ");
	}

}
