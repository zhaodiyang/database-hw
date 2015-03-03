import java.util.List;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 * (You should use BPlusTree.D in your code. Don't hard-code "2" everywhere.)
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 */
	public T search(K key) {
		 LeafNode<K,T> node;
		 
		 // if root is not a leaf node, 
		 // find the leaf node contains the key
		 if (!root.isLeafNode) {
			 IndexNode<K,T> indexnode = (IndexNode<K,T>) root;
			 node = (LeafNode<K,T>)tree_search(key, indexnode);
		 }
		 else
			 node = (LeafNode<K,T>) root;
		 
		 // find the matching key and return its value
		 for(int i=0; i<node.keys.size(); i++){
			 if(node.keys.get(i).compareTo(key) == 0)
				 return node.values.get(i);
		 }
		 return null;
	}
	
	/**
	 * TODO Given key/node return the pointer to the value
	 * 
	 * @param key
	 * @param node
	 * @return the leaf node contains the key
	 * 
	 */
	public Node<K,T> tree_search(K key, Node<K,T> node){
		
		// node is a leaf node
		if (node.isLeafNode)   
			return node;
		
		// node is an index node
		else { 
			IndexNode<K,T> indexnode = (IndexNode<K,T>) node;
			
			// key < k0
			if (indexnode.keys.get(0).compareTo(key) > 0) 
				return tree_search(key, indexnode.children.get(0));
			
			// kD <= key
			else if (indexnode.keys.get(
					indexnode.keys.size()-1).compareTo(key) <= 0){ 
				node = indexnode.children.get(indexnode.children.size()-1);
				return tree_search(key, node);
			}
			// ki <= key < ki+1
			else {
				for(int i=0; i<indexnode.keys.size()-1; i++) {
					if (indexnode.keys.get(i).compareTo(key) <= 0 && 
							indexnode.keys.get(i+1).compareTo(key) > 0){ 
						node = indexnode.children.get(i+1);
						return tree_search(key, node);
					}
				}
			}
		}
		return null;
	}

	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
		
		// root is null
		if (root == null)
			root = new LeafNode<K,T>(key, value);
		
		// root is a leaf node
		else if (root.isLeafNode){
			LeafNode<K,T> leafnode = (LeafNode<K,T>) root;
			
			// insert key and value to the root node
			leafnode.insertSorted(key, value);
			
			// root overflowed, reassign root node
			if (leafnode.isOverflowed()){
				MyEntry<K, Node<K,T>> entry = splitLeafNode(leafnode);
				IndexNode<K,T> newRoot = new IndexNode<K,T>(entry.getKey(), leafnode, entry.getValue());
				root = newRoot;
			}
			else
				root = leafnode;
		}
		// root is an index node
		else{
			IndexNode<K,T> indexnode = (IndexNode<K,T>) root; 
			LeafNode<K,T> leafnode = (LeafNode<K,T>)tree_search(key, indexnode);
			
			// insert key and value to the leaf node
			leafnode.insertSorted(key, value);
			
			// leaf node overflowed
			if (leafnode.isOverflowed()){
				MyEntry<K, Node<K,T>> entry = splitLeafNode(leafnode);
				insertToParent(entry, getHeight()-1);
			}
		}
	}
	
	/**
	 * @param entry
	 * @param tree height you want to insert the entry to, root is at height 0
	 * */
	
	public void insertToParent(MyEntry<K, Node<K,T>> entry, int ht) {
		IndexNode<K,T> indexnode = (IndexNode<K,T>) root;
		
		// find the index node that once pointed to the entry's key
		indexnode = findParentNode(entry.getKey(), ht);
		
		// find the index of the location where entry's key has to be inserted
		int index = 0;
		if (indexnode.keys.get(0).compareTo(entry.getKey()) > 0);
		
		// kD <= key
		else if (indexnode.keys.get(
				indexnode.keys.size()-1).compareTo(entry.getKey()) <= 0)
			index = indexnode.keys.size();
		
		// ki <= key < ki+1
		else {
			for(int j=0; j<indexnode.keys.size()-1; j++) {
				if (indexnode.keys.get(j).compareTo(entry.getKey()) <= 0 && 
						indexnode.keys.get(j+1).compareTo(entry.getKey()) > 0) {
					index = j + 1;
					break;
				}
			}
		}
		// insert entry to the index
		indexnode.insertSorted(entry, index);
		
		// index node overflowed
		if (indexnode.isOverflowed()) {
			MyEntry<K, Node<K,T>> newEntry = splitIndexNode(indexnode);
			ht--;
			if (ht < 0) {
				IndexNode<K,T> newRoot = new IndexNode<K,T>(newEntry.getKey(), indexnode, newEntry.getValue());
				root = newRoot;
			}
			else {
			    insertToParent(newEntry, ht);
			}
		}
	}
	
	/**
	 * @return height of BPlus Tree
	 * */
	public int getHeight() {
		int ht = 0;
		if (root == null) return ht;
		if (!root.isLeafNode) {
			Node<K,T> node = root;
			while(!node.isLeafNode) {
				IndexNode<K,T> indexNode = (IndexNode<K,T>) node;
				ht++;
				node = indexNode.children.get(0);
			}
		}
		return ht;
	}
	
	/**
	 * @param key
	 * @param BPlus Tree search height
	 * @return IndexNode which once pointed to the key's node
	 * */
	public IndexNode<K,T> findParentNode(K key, int ht) {
		IndexNode<K,T> indexnode = (IndexNode<K,T>) root;
		
		// find the index node that once pointed to the entry's key
		for(int i=0; i<ht; i++) {
			// key < k0
			if (indexnode.keys.get(0).compareTo(key) > 0) 
				indexnode = (IndexNode<K,T>)indexnode.children.get(0);
			
			// kD <= key
			else if (indexnode.keys.get(
					indexnode.keys.size()-1).compareTo(key) <= 0)
				indexnode = (IndexNode<K,T>)indexnode.children.get(indexnode.children.size()-1);
			
			// ki <= key < ki+1
			else {
				for(int j=0; j<indexnode.keys.size()-1; j++) {
					if (indexnode.keys.get(j).compareTo(key) <= 0 && 
							indexnode.keys.get(j+1).compareTo(key) > 0) {
						indexnode = (IndexNode<K,T>)indexnode.children.get(j+1);
						break;
					}
				}
			}
		}
		return indexnode;
	}

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<splittingKey, RightNode>
	 * 
	 * @param leaf
	 * @return the key/node pair as an Entry
	 */
	public MyEntry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {
		
		int len = leaf.keys.size();
		int half = len/2;
		
		// store the 2nd half of the key/value pair in a new node
		K keyHalf = leaf.keys.get(half);
		List<K> keyr = leaf.keys.subList(half, len);
		List<T> valuesr = leaf.values.subList(half, len);
		LeafNode<K, T> leafr = new LeafNode<K, T>(keyr, valuesr);
		
		// remove the 2nd half of the key/value pair from leaf
		for (int i=half; i<len; i++) {
		    leaf.keys.remove(half);
		    leaf.values.remove(half);
		}
		
		// link new node with the old node
		leafr.previousLeaf = leaf;
		if (leaf.nextLeaf != null) {
			leafr.nextLeaf = leaf.nextLeaf;
		    leafr.nextLeaf.previousLeaf = leafr;
		}
		leaf.nextLeaf = leafr;

		MyEntry<K, Node<K, T>> newEntry = new MyEntry<K, Node<K, T>>(keyHalf, leafr);
		return newEntry;
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<splittingKey, RightNode>
	 * 
	 * @param node
	 * @return new key/node pair as an Entry
	 */
	public MyEntry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> indexnode) {
		int len = indexnode.keys.size();
	    int half = len/2;
		
	    // store the 2nd half of the key/value pair in a new node
		K key = indexnode.keys.get(half);
		List<K> keyr = indexnode.keys.subList(half+1, len);
		List<Node<K,T>> childrenr = indexnode.children.subList(half+1, len+1);
		IndexNode<K,T> indexr = new IndexNode<K,T>(keyr, childrenr);
		
		// remove the 2nd half of the key/value pair from index node
		for (int i=half; i<len; i++) {
		    indexnode.keys.remove(half);
		    indexnode.children.remove(half+1);
		}
		
		MyEntry<K, Node<K,T>> newEntry = new MyEntry<K,Node<K,T>>(key, indexr);
		return newEntry;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {
		
		LeafNode<K,T> node;
		// if root is not a leaf node, 
		// find the leaf node contains the key
		if (!root.isLeafNode) {
			IndexNode<K,T> indexnode = (IndexNode<K,T>)root;
			node = (LeafNode<K,T>)tree_search(key, indexnode);
		}
		else {
			node = (LeafNode<K,T>) root;
		}
		// find the matching key and remove its key/value pair
		for(int i=0; i<node.keys.size(); i++){
			if(node.keys.get(i).compareTo(key) == 0) {
				node.keys.remove(i);
				node.values.remove(i);
				break;
			}
		}
		// leaf node underflowed
		if (node.isUnderflowed()) {
			 
			// root is a leaf node
			if (root.isLeafNode) {
				if (root.keys.size() == 0)
					root = null;
				return;
			}
			// root is an index node
			IndexNode<K,T> parentNode = findParentNode(node.keys.get(0), getHeight()-1); 
			LeafNode<K,T> sibling;	// store the target sibling node
			int parentIndex;		// store the removed index value
			int currht = getHeight();	// store the current height of the tree
			
			// determine the target sibling:
			// if left leaf is not null, check if have the same parent
			// if doesn't, then the right leaf must have the same parent
			// if left leaf is null, then sibling must be the right leaf
			if (node.previousLeaf != null) {
				sibling = node.previousLeaf;
				if (findParentNode(sibling.keys.get(0), 
						getHeight()-1).keys.get(0).compareTo(parentNode.keys.get(0)) == 0)
					parentIndex = handleLeafNodeUnderflow(sibling, node, parentNode);
				else {
					sibling = node.nextLeaf;
					parentIndex = handleLeafNodeUnderflow(node, sibling, parentNode);
				}
			}
			else {
				sibling = node.nextLeaf;
				parentIndex = handleLeafNodeUnderflow(node, sibling, parentNode);
			}
			// merge happened, parentNode has to remove the key/pointer
			while (parentIndex != -1) {
				currht--;
				parentNode.keys.remove(parentIndex);
				parentNode.children.remove(parentIndex+1);
				// parentNode is root
				if (currht == 0) {
					if (parentNode.keys.size() == 0)
						root = parentNode.children.get(0);
					parentIndex = -1;
				}
				// parentNode is not root and is underflowed
				else if (parentNode.isUnderflowed()) {
					IndexNode<K,T> indexSibling;	// store the target index sibling node
					IndexNode<K,T> currNode = parentNode;
					parentNode = findParentNode(parentNode.keys.get(0), currht-1 );
					int index = 0;	// store child index value
					
					while (parentNode.children.get(index).keys.get(0).compareTo(
							currNode.keys.get(0)) != 0) {
						index++;
					}
					// sibling is the right node
					if (index == 0) {
						indexSibling = (IndexNode<K,T>)parentNode.children.get(index+1);
						parentIndex  = handleIndexNodeUnderflow(currNode, indexSibling, parentNode);	
					}
					// sibling is the left node
					else {
						indexSibling = (IndexNode<K,T>)parentNode.children.get(index-1);
						parentIndex  = handleIndexNodeUnderflow(indexSibling, currNode, parentNode);
					}
				}
			}	
		}
	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		// find node index in parent
		int leftIndex = 0;
		for (int i=0; i<parent.children.size(); i++) {
			if (parent.children.get(i).keys.get(0) == left.keys.get(0)) {
				leftIndex = i;
				break;
			}
		}
		// merge
		if (left.keys.size() + right.keys.size() <= 2*D) {
			left.keys.addAll(right.keys);
			left.values.addAll(right.values);
			// link right.nextLeaf is exists
			if (right.nextLeaf != null) {
				left.nextLeaf = right.nextLeaf;
				left.nextLeaf.previousLeaf = left;
			}

			return leftIndex;
		}
		// redistribute
		else {
			// change parent node key
			K newKey = right.keys.get(0);
			parent.keys.remove(leftIndex);
			parent.keys.add(leftIndex, newKey);
			
			// add key/value to left
			left.keys.add(newKey);
			left.values.add(right.values.get(0));
			
			// remove key/value from right
			right.keys.remove(0);
			right.values.remove(0);
			
			return -1;
		}
	}
	
	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> left,
			IndexNode<K,T> right, IndexNode<K,T> parent) {
		// find node index in parent
		int leftIndex = 0;
		for (int i=0; i<parent.children.size(); i++) {
			if (parent.children.get(i).keys.get(0) == left.keys.get(0)) {
				leftIndex = i;
				break;
			}
		}
		// merge
		if (left.keys.size() + right.keys.size() <= 2*D) {
			left.keys.add(parent.keys.get(leftIndex));
			left.keys.addAll(right.keys);
			left.children.addAll(right.children);
			return leftIndex;
		}
		// redistribute
		else {
			// change parent node key
			K newKey = right.keys.get(0);
			K parentKey = parent.keys.get(leftIndex);
			parent.keys.remove(leftIndex);
			parent.keys.add(leftIndex, newKey);
			
			// add key/value to left
			left.keys.add(parentKey);
			left.children.add(right.children.get(0));
			
			// remove key/value from right
			right.keys.remove(0);
			right.children.remove(0);
			
			return -1;
		}
	}
}
