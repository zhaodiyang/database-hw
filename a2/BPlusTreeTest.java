import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;


public class BPlusTreeTest {

	@Test
	public void testSearch0() {

		BPlusTree<String, String> tree = new BPlusTree<String, String>();
		tree.insert("a1", "a1");

		String test = tree.search("a1");
		String correct = "a1";

		assertEquals(correct, test);

		tree.insert("b2", "b2");

		test = tree.search("b2");
		correct = "b2";

		assertEquals(correct, test);
	}

	@Test
	public void testSearch1() {
		Character alphabet[] = new Character[] { 'a','b','c','d','e','f','g','h' };
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);

		String test = tree.search('a');
		String correct = "a";
		assertEquals(correct, test);

		test = tree.search('g');
		correct = "g";
		assertEquals(correct, test);

		test = tree.search('c');
		correct = "c";
		assertEquals(correct, test);

		tree.delete('a');

		test = Utils.outputTree(tree);
		correct = "@e/@%%[(b,b);(c,c);(d,d);]#[(e,e);(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		test = Utils.outputTree(tree);
		correct = "@e/@%%[(b,b);(c,c);(d,d);]#[(e,e);(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('b');
		test = Utils.outputTree(tree);
		correct = "@e/@%%[(c,c);(d,d);]#[(e,e);(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('c');

		test = Utils.outputTree(tree);
		correct = "@f/@%%[(d,d);(e,e);]#[(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('d');

		test = Utils.outputTree(tree);
		correct = "[(e,e);(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('e');

		test = Utils.outputTree(tree);
		correct = "[(f,f);(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('f');

		test = Utils.outputTree(tree);
		correct = "[(g,g);(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('g');
		test = Utils.outputTree(tree);
		correct = "[(h,h);]$%%";
		assertEquals(correct, test);

		tree.delete('h');
	}

	@Test
	public void testSearch2() {
		Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14,
				15, 16 };
		String primeNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < primeNumbers.length; i++) {
			primeNumberStrings[i] = (primeNumbers[i]).toString();
		}
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);

		String test = tree.search(2);
		String correct = "2";
		assertEquals(test, correct);

		test = tree.search(16);
		correct = "16";
		assertEquals(test, correct);

		test = tree.search(5);
		correct = "5";
		assertEquals(test, correct);

		test = tree.search(8);
		correct = "8";
		assertEquals(test, correct);

		tree.delete(2);
		test = Utils.outputTree(tree);
		correct = "@8/10/12/14/@%%[(4,4);(5,5);(7,7);]#[(8,8);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);

		tree.insert(3, "3");
		test = Utils.outputTree(tree);
		correct = "@8/10/12/14/@%%[(3,3);(4,4);(5,5);(7,7);]#[(8,8);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);

		tree.delete(8);
		test = Utils.outputTree(tree);
		correct = "@7/10/12/14/@%%[(3,3);(4,4);(5,5);]#[(7,7);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
		assertEquals(test, correct);

		Integer specialNumbers[] = new Integer[] { 22, 24, 25, 27, 28, 29, 30, 31, 32, 33};
		String specialNumberStrings[] = new String[primeNumbers.length];
		for (int i = 0; i < specialNumbers.length; i++) {
			specialNumberStrings[i] = (specialNumbers[i]).toString();
		}
		Utils.bulkInsert(tree, specialNumbers, specialNumberStrings);

		test = Utils.outputTree(tree);
		correct = "@12/24/@%%@7/10/@@14/16/@@27/29/31/@%%[(3,3);(4,4);(5,5);]#[(7,7);(9,9);]#[(10,10);(11,11);]$[(12,12);(13,13);]#[(14,14);(15,15);]"
				+ "#[(16,16);(22,22);]$[(24,24);(25,25);]#[(27,27);(28,28);]#[(29,29);(30,30);]#[(31,31);(32,32);(33,33);]$%%";
		assertEquals(test, correct);
		
		tree.delete(3);
		tree.delete(4);
		tree.delete(5);
		tree.delete(33);
		tree.delete(32);
		tree.delete(31);
		tree.delete(30);
		
		test = Utils.outputTree(tree);
		correct = "@16/@%%@10/12/14/@@24/27/@%%[(7,7);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);]$[(16,16);(22,22);]#[(24,24);(25,25);]"
				+ "#[(27,27);(28,28);(29,29);]$%%";
		assertEquals(test, correct);
	}

}
