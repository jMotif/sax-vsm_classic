package net.seninp.jmotif.cluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.seninp.jmotif.sax.SAXException;
import net.seninp.jmotif.text.Params;
import net.seninp.jmotif.text.TextProcessor;
import net.seninp.jmotif.text.WordBag;

/**
 * Unsupervised clustering of time series via SAX-VSM tf·idf vectors.
 *
 * <p>Mirrors {@link net.seninp.jmotif.SAXVSMEvaluator}: build tf·idf from labeled UCR data, then
 * cluster <em>individual series</em> rather than merged class bags.</p>
 */
public final class SAXVSMClustering {

  private SAXVSMClustering() {
  }

  /**
   * One tf·idf vector per time series (labels {@code class:index}).
   */
  public static Map<String, Map<String, Double>> seriesTfidf(Map<String, List<double[]>> data,
      Params params) throws SAXException {
    TextProcessor tp = new TextProcessor();
    List<WordBag> bags = tp.perSeriesWordBags(data, params);
    HashMap<String, HashMap<String, Double>> tfidf = tp.computeTFIDF(bags);
    densify(tfidf);
    return cast(tfidf);
  }

  public static ClusterAssignments kMeans(Map<String, Map<String, Double>> tfidf, int k) {
    return kMeans(tfidf, k, KMeansInit.FURTHEST_FIRST, 0L);
  }

  public static ClusterAssignments kMeans(Map<String, Map<String, Double>> tfidf, int k,
      KMeansInit init, long seed) {
    return KMeans.cluster(tfidf, k, init, seed);
  }

  public static Dendrogram hierarchical(Map<String, Map<String, Double>> tfidf, Linkage linkage) {
    return HierarchicalClustering.cluster(tfidf, linkage);
  }

  /** True labels keyed by {@code class:index} series ids from {@link #seriesTfidf}. */
  public static Map<String, String> seriesLabels(Map<String, List<double[]>> data) {
    HashMap<String, String> labels = new HashMap<String, String>();
    for (Map.Entry<String, List<double[]>> e : data.entrySet()) {
      int index = 0;
      for (int i = 0; i < e.getValue().size(); i++) {
        labels.put(e.getKey() + ":" + index++, e.getKey());
      }
    }
    return labels;
  }

  private static Map<String, Map<String, Double>> cast(
      HashMap<String, HashMap<String, Double>> tfidf) {
    HashMap<String, Map<String, Double>> res = new HashMap<String, Map<String, Double>>();
    for (Map.Entry<String, HashMap<String, Double>> e : tfidf.entrySet()) {
      res.put(e.getKey(), e.getValue());
    }
    return res;
  }

  /** Align sparse tf·idf rows to a shared vocabulary (missing words → 0). */
  private static void densify(HashMap<String, HashMap<String, Double>> tfidf) {
    TreeSet<String> vocabulary = new TreeSet<String>();
    for (HashMap<String, Double> vector : tfidf.values()) {
      vocabulary.addAll(vector.keySet());
    }
    for (HashMap<String, Double> vector : tfidf.values()) {
      for (String word : vocabulary) {
        if (!vector.containsKey(word)) {
          vector.put(word, 0.0d);
        }
      }
    }
  }
}
