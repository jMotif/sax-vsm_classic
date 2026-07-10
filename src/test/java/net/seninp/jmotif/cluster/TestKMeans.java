package net.seninp.jmotif.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class TestKMeans {

  private static final String[] IDS = { "a", "b", "c", "d" };

  @Test
  public void separatesTwoGroupsWithFurthestFirstSeeding() {
    Map<String, Map<String, Double>> tfidf = toyTfidf();

    ClusterAssignments result = KMeans.cluster(tfidf, 2, KMeansInit.FURTHEST_FIRST, 42L);
    assertEquals(2, result.k());

    int clusterA = result.clusterOf("a");
    assertEquals(clusterA, result.clusterOf("b"));
    int clusterC = result.clusterOf("c");
    assertEquals(clusterC, result.clusterOf("d"));
    assertTrue(clusterA != clusterC);
  }

  @Test
  public void sameSeedProducesSamePartition() {
    Map<String, Map<String, Double>> tfidf = toyTfidf();

    ClusterAssignments first = KMeans.cluster(tfidf, 2, KMeansInit.FURTHEST_FIRST, 7L);
    ClusterAssignments second = KMeans.cluster(tfidf, 2, KMeansInit.FURTHEST_FIRST, 7L);

    assertEquals(first.asMap(), second.asMap());
  }

  @Test
  public void rejectsInvalidK() {
    Map<String, Map<String, Double>> tfidf = toyTfidf();
    try {
      KMeans.cluster(tfidf, 0, KMeansInit.RANDOM, 0L);
      assertTrue(false);
    }
    catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("k must be"));
    }
  }

  private static Map<String, Map<String, Double>> toyTfidf() {
    Map<String, Map<String, Double>> tfidf = new HashMap<String, Map<String, Double>>();
    tfidf.put("a", vector(0.9, 0.1, 0.0, 0.0));
    tfidf.put("b", vector(0.85, 0.15, 0.0, 0.0));
    tfidf.put("c", vector(0.0, 0.0, 0.2, 0.9));
    tfidf.put("d", vector(0.0, 0.0, 0.25, 0.85));
    return tfidf;
  }

  private static Map<String, Double> vector(double... values) {
    HashMap<String, Double> res = new HashMap<String, Double>();
    for (int i = 0; i < IDS.length; i++) {
      res.put(IDS[i], values[i]);
    }
    return res;
  }
}
