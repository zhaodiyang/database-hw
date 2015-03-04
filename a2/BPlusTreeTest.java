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
		Character alphabet[] = new Character[] { 'a','b','c','d','e','f','g' };
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
	}

}
