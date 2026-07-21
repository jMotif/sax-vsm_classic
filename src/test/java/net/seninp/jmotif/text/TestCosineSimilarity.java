package net.seninp.jmotif.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.HashMap;
import java.util.Map.Entry;
import org.junit.Test;

/**
 * Test the cosine similarity implementation.
 * 
 * @author psenin
 * 
 */
public class TestCosineSimilarity {

  private static final double TEST_VALUE = 0.8215838362577491D;

  private static final double TEST_PASS_PRECISION = 0.000000000000001;

  private static final TextProcessor tp = new TextProcessor();

  /**
   * Using a dumb example.
   */
  @Test
  public void testCosineSimilarity() {

    WordBag wb1 = new WordBag("first");
    WordBag wb2 = new WordBag("second");

    wb1.addWord("me", 2);
    wb1.addWord("Julie", 1);
    wb1.addWord("likes", 0);
    wb1.addWord("loves", 2);
    wb1.addWord("Jane", 0);
    wb1.addWord("Linda", 1);
    wb1.addWord("than", 1);
    wb1.addWord("more", 1);

    wb2.addWord("me", 2);
    wb2.addWord("Julie", 1);
    wb2.addWord("likes", 1);
    wb2.addWord("loves", 1);
    wb2.addWord("Jane", 1);
    wb2.addWord("Linda", 0);
    wb2.addWord("than", 1);
    wb2.addWord("more", 1);

    double cosine = tp.cosineDistance(wb1.getWordsAsDoubles(), wb2.getWordsAsDoubles());

    assertEquals("Testing cosine similarity", TEST_VALUE, cosine, TEST_PASS_PRECISION);
  }

  /**
   * Making sure normalization works as intended - i.e doesn't change anything.
   */
  @Test
  public void testCosineSimilarityNorm() {

    WordBag wb1 = new WordBag("first");
    WordBag wb2 = new WordBag("second");

    wb1.addWord("me", 2);
    wb1.addWord("Julie", 1);
    wb1.addWord("likes", 0);
    wb1.addWord("loves", 2);
    wb1.addWord("Jane", 0);
    wb1.addWord("Linda", 1);
    wb1.addWord("than", 1);
    wb1.addWord("more", 1);

    wb2.addWord("me", 2);
    wb2.addWord("Julie", 1);
    wb2.addWord("likes", 1);
    wb2.addWord("loves", 1);
    wb2.addWord("Jane", 1);
    wb2.addWord("Linda", 0);
    wb2.addWord("than", 1);
    wb2.addWord("more", 1);

    double cosine = tp.cosineDistance(wb1.getWordsAsDoubles(), wb2.getWordsAsDoubles());
    assertEquals("Testing cosine similarity", TEST_VALUE, cosine, TEST_PASS_PRECISION);

    // grow the vector
    HashMap<String, Double> wbLong = wb1.getWordsAsDoubles();
    double multiplier = 8.24864813846848348486;
    for (Entry<String, Double> e : wbLong.entrySet()) {
      wbLong.put(e.getKey(), e.getValue() * multiplier);
    }
    double distLong = tp.cosineDistance(wbLong, wb2.getWordsAsDoubles());

    assertEquals("Testing cosine similarity", TEST_VALUE, distLong, TEST_PASS_PRECISION);

    // normalize vectors
    HashMap<String, HashMap<String, Double>> vectors = new HashMap<String, HashMap<String, Double>>();
    vectors.put("first", wbLong);
    vectors.put("second", wb2.getWordsAsDoubles());
    vectors = tp.normalizeToUnitVectors(vectors);

    double distNorm = tp.cosineDistance(vectors.get("first"), vectors.get("second"));

    assertEquals("Testing cosine similarity", TEST_VALUE, distNorm, TEST_PASS_PRECISION);
  }

  /**
   * A zero-magnitude vector has no direction: cosineDistance must return 0.0, not 0/0 = NaN,
   * so it cannot poison distance matrices or similarity-based class picks.
   */
  @Test
  public void testCosineDistanceZeroVectorReturnsZero() {
    HashMap<String, Double> zero = new HashMap<String, Double>();
    zero.put("a", 0.0);
    zero.put("b", 0.0);

    HashMap<String, Double> nonzero = new HashMap<String, Double>();
    nonzero.put("a", 1.0);
    nonzero.put("b", 2.0);

    double zeroVsNonzero = tp.cosineDistance(zero, nonzero);
    double nonzeroVsZero = tp.cosineDistance(nonzero, zero);
    double zeroVsZero = tp.cosineDistance(zero, zero);

    assertFalse("zero vector must not yield NaN", Double.isNaN(zeroVsNonzero));
    assertFalse("zero vector (rhs) must not yield NaN", Double.isNaN(nonzeroVsZero));
    assertFalse("zero vs zero must not yield NaN", Double.isNaN(zeroVsZero));
    assertEquals("zero-magnitude cosine distance", 0.0, zeroVsNonzero, TEST_PASS_PRECISION);
    assertEquals("zero-magnitude cosine distance (rhs)", 0.0, nonzeroVsZero, TEST_PASS_PRECISION);
    assertEquals("zero-vs-zero cosine distance", 0.0, zeroVsZero, TEST_PASS_PRECISION);
  }

  /**
   * An empty test bag (zero magnitude) must yield a 0.0 similarity, not NaN, in
   * cosineSimilarity(WordBag, weightVector).
   */
  @Test
  public void testCosineSimilarityEmptyBagReturnsZero() {
    WordBag empty = new WordBag("empty");

    HashMap<String, Double> weights = new HashMap<String, Double>();
    weights.put("me", 2.0);
    weights.put("Julie", 1.0);

    double sim = tp.cosineSimilarity(empty, weights);

    assertFalse("empty bag must not yield NaN", Double.isNaN(sim));
    assertEquals("empty-bag cosine similarity", 0.0, sim, TEST_PASS_PRECISION);
  }

  /**
   * The instrumented overload must also guard the zero-magnitude denominator and return 0.0.
   */
  @Test
  public void testCosineSimilarityInstrumentedEmptyBagReturnsZero() {
    WordBag empty = new WordBag("empty");

    HashMap<String, Double> weights = new HashMap<String, Double>();
    weights.put("me", 2.0);
    weights.put("Julie", 1.0);

    HashMap<String, Double> insight = new HashMap<String, Double>();
    double sim = tp.cosineSimilarityInstrumented(empty, weights, insight);

    assertFalse("instrumented empty bag must not yield NaN", Double.isNaN(sim));
    assertEquals("instrumented empty-bag similarity", 0.0, sim, TEST_PASS_PRECISION);
  }
}
