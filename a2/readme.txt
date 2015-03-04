CS4320 HW2 PROGRAMMING PART
README.txt
Diyang Zhao (dz276),  Kai Wang (kw296)

(1) Node.java, IndexNode.java, LeafNode.java, Utils.java, Tests.java: received from instructor without any modification.
(2) BPlusTreeTest.java: Test file for testing search function and delete function.
(3) BPlusTree.java: implemented all functions, could search, insert, delete elements in BPlusTree. Passed all test cases. Search, insert, delete algorithms adapted from the textbook [1]. 
(4) MyEntry.java: implements Map.Entry, so we can create objects to store (Key, Value) pair. Adapted from [2].

BPlusTree.java:

Function used by search:
- tree_search: 
search the value associated with the given key, search from root and end until reach a leaf node. Return null if key doesn’t exist. Support recursive calls.

Functions used by insert:
- insertToParent:
this function is called only when overflow happens in the current height, will insert entry into ht(parent’s height).

- getHeight:
returns the current height of the whole tree

- findParentNode:
returns the node which points to the key value at height ht. Used in insertToParent function.

- splitLeafNode
when overflow happens in a leaf node, split the current leaf node and return the generated entry, which is going to be inserted into the parent node.

- splitIndexNode
when overflow happens in an index node, split the current index node and return the generated entry, which is going to be inserted into the parent node.

Functions used by delete:
- handleLeafNodeUnderflow
when underflow happens in a leaf node, merge with the target sibling if possible, else redistribute keys and values with the target sibling, returns the index of the parent key/children that parent should remove

- handleIndexNodeUnderflow
when underflow happens in an index node, merge with the target sibling index node if possible, else redistribute keys and values with the target sibling, returns the index of the parent key/children that parent should remove

Reference
[1]Database Management Systems Third Edition, R.Ramakrishnan and J.Gehrke, McGraw-Hill Education, 2003, pp344-356
[2]Java How To Create New Entry Key Value, stackoverflow, June 2010
http://stackoverflow.com/questions/3110547/java-how-to-create-new-entry-key-value