package net.seninp.jmotif.cluster;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class TestHierarchicalClustering {

  @Test
  public void singleLinkageToyNewick() {
    Dendrogram tree = HierarchicalClustering.cluster(toyTfidf(), Linkage.SINGLE);
    assertEquals(
        "(d:0.0010241295,c:0.0010241295):0.3288590604,(b:0.0010241295,a:0.0010241295):0.3288590604",
        tree.toNewick());
  }

  @Test
  public void completeLinkageToyNewick() {
    Dendrogram tree = HierarchicalClustering.cluster(toyTfidf(), Linkage.COMPLETE);
    assertEquals(
        "(d:0.0010241295,c:0.0010241295):0.3902439024,(b:0.0010241295,a:0.0010241295):0.3902439024",
        tree.toNewick());
  }

  private static Map<String, Map<String, Double>> toyTfidf() {
    Map<String, Map<String, Double>> tfidf = new HashMap<String, Map<String, Double>>();
    tfidf.put("a", word("x", 0.9, "y", 0.1));
    tfidf.put("b", word("x", 0.85, "y", 0.15));
    tfidf.put("c", word("x", 0.1, "y", 0.9));
    tfidf.put("d", word("x", 0.15, "y", 0.85));
    return tfidf;
  }

  private static Map<String, Double> word(String w0, double v0, String w1, double v1) {
    HashMap<String, Double> res = new HashMap<String, Double>();
    res.put(w0, v0);
    res.put(w1, v1);
    return res;
  }
}
