package net.seninp.jmotif.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the word bag class.
 * 
 * @author psenin
 * 
 */
public class TestWordBag {

  private static final String CR = "\n";

  private static final String TEST_BAG_NAME = "TEST001";

  private static final String[] TEST_WORDS = { "word0", "word1", "word2", "word3", "word4" };

  private HashMap<String, Integer> map;

  @Before
  public void before() {
    map = new HashMap<>();
    for (int i = 0; i < TEST_WORDS.length; i++) {
      map.put(TEST_WORDS[i], i + 1);
    }

  }

  /**
   * Test the constructor.
   */
  @Test
  public void testConstructor() {
    WordBag bag = new WordBag(TEST_BAG_NAME);
    assertEquals(TEST_BAG_NAME, bag.getLabel());
    assertTrue(0 == bag.getWordFrequency("word"));
    assertTrue(bag.getWords().isEmpty());
    assertTrue(bag.getWordSet().isEmpty());

    for (int i = 0; i < TEST_WORDS.length; i++) {
      bag.addWord(TEST_WORDS[i], i + 1);
    }

    WordBag bag2 = new WordBag(TEST_BAG_NAME, map);
    for (Entry<String, AtomicInteger> word : bag2.getInternalWords().entrySet()) {
      assertEquals(word.getValue().get(), bag.getInternalWords().get(word.getKey()).get());
    }

    String newLabel = "bag#2";
    bag2.setLabel(newLabel);
    assertTrue(newLabel.equals(bag2.getLabel()));
  }

  @Test
  public void testAddWord() {
    WordBag bag = new WordBag(TEST_BAG_NAME, map);

    String newWord = "word77";
    bag.addWord(newWord);
    assertEquals(1, bag.getInternalWords().get(newWord).get());

    bag.addWord(newWord);
    assertEquals(2, bag.getInternalWords().get(newWord).get());

    bag.addWord(newWord, 8);
    assertEquals(10, bag.getInternalWords().get(newWord).get());

    String newWord2 = "word78";
    bag.addWord(newWord2, 7);
    assertEquals(7, bag.getInternalWords().get(newWord2).get());

    assertEquals(Integer.valueOf(1 + 2 + 3 + 4 + 5 + 10 + 7).intValue(), bag.getTotalWordCount());

  }

  @Test
  public void testMerge() {

    WordBag bag = new WordBag("bag2");
    for (int i = 0; i < TEST_WORDS.length; i++) {
      bag.addWord(TEST_WORDS[TEST_WORDS.length - 1 - i], i + 1);
    }

    bag.mergeWith(new WordBag(TEST_BAG_NAME, map));

    for (Entry<String, AtomicInteger> word : bag.getInternalWords().entrySet()) {
      assertEquals(6, bag.getInternalWords().get(word.getKey()).get());
    }

  }

  @Test
  public void testToString() {

    WordBag bag = new WordBag(TEST_BAG_NAME, map);

    String toString = bag.toString();
    assertTrue(toString.contains("word0\t1" + CR));

    String toColumn = bag.toColumn();
    assertTrue(toColumn.contains(CR + "word3" + CR));

  }

  @Test
  public void testClone() {

    WordBag bag = new WordBag(TEST_BAG_NAME, map);

    WordBag cloned = bag.clone();

    assertEquals(bag.getTotalWordCount(), cloned.getTotalWordCount());

    for (Entry<String, AtomicInteger> word : cloned.getInternalWords().entrySet()) {
      assertEquals(word.getValue().get(), bag.getInternalWords().get(word.getKey()).get());
    }

  }

  /**
   * Test the word bag functionality.
   */
  @Test
  public void testBag() {
    WordBag bag = new WordBag(TEST_BAG_NAME);
    for (int i = 0; i < TEST_WORDS.length; i++) {
      bag.addWord(TEST_WORDS[i], i + 1);
    }

    assertEquals(Integer.valueOf(3), bag.getWordFrequency(TEST_WORDS[2]));
    assertEquals(Integer.valueOf(5), bag.getWordFrequency(TEST_WORDS[4]));

    HashMap<String, Integer> words = bag.getWords();
    assertEquals(5, words.size());

    assertEquals(Integer.valueOf(4), words.get("word3"));
    assertTrue(bag.contains("word4"));
  }

}
