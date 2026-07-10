package net.seninp.jmotif.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeSet;
import net.seninp.jmotif.text.CosineDistanceMatrix;
import net.seninp.jmotif.text.TextProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * k-means clustering over SAX-VSM tf·idf vectors using cosine similarity.
 */
public final class KMeans {

  private static final double CONVERGENCE = 1e-9;
  private static final Logger LOGGER = LoggerFactory.getLogger(KMeans.class);
  private static final TextProcessor TP = new TextProcessor();

  private KMeans() {
  }

  public static ClusterAssignments cluster(Map<String, Map<String, Double>> tfidf, int k,
      KMeansInit init, long seed) {
    validateInput(tfidf, k);

    LinkedHashMap<String, Map<String, Double>> centroids = initialCentroids(tfidf, k, init, seed);
    Map<Integer, List<String>> groups = assignGroups(centroids, tfidf);

    while (updateCentroids(centroids, groups, tfidf)) {
      groups = assignGroups(centroids, tfidf);
    }

    LOGGER.debug("k-means finished with {} clusters", Integer.valueOf(k));
    return ClusterAssignments.of(groups);
  }

  private static void validateInput(Map<String, Map<String, Double>> tfidf, int k) {
    if (tfidf == null || tfidf.isEmpty()) {
      throw new IllegalArgumentException("tfidf must not be empty");
    }
    if (k < 1 || k > tfidf.size()) {
      throw new IllegalArgumentException(
          "k must be between 1 and the number of series (got k=" + k + ", n=" + tfidf.size() + ")");
    }
  }

  private static LinkedHashMap<String, Map<String, Double>> initialCentroids(
      Map<String, Map<String, Double>> tfidf, int k, KMeansInit init, long seed) {
    if (init == KMeansInit.RANDOM) {
      return randomCentroids(tfidf, k, seed);
    }
    return furthestFirstCentroids(tfidf, k, seed);
  }

  private static LinkedHashMap<String, Map<String, Double>> randomCentroids(
      Map<String, Map<String, Double>> tfidf, int k, long seed) {
    Random random = new Random(seed);
    ArrayList<String> keys = sortedIds(tfidf);
    LinkedHashMap<String, Map<String, Double>> centroids = new LinkedHashMap<String, Map<String, Double>>();
    for (int i = 0; i < k; i++) {
      int pick = random.nextInt(keys.size());
      String key = keys.remove(pick);
      centroids.put(String.valueOf(i), copyVector(tfidf.get(key)));
    }
    return centroids;
  }

  private static LinkedHashMap<String, Map<String, Double>> furthestFirstCentroids(
      Map<String, Map<String, Double>> tfidf, int k, long seed) {
    Random random = new Random(seed);
    CosineDistanceMatrix matrix = new CosineDistanceMatrix(toTfidfHashMap(tfidf));
    ArrayList<String> keys = sortedIds(tfidf);
    LinkedHashMap<String, Map<String, Double>> centroids = new LinkedHashMap<String, Map<String, Double>>();

    String first = keys.get(random.nextInt(keys.size()));
    ArrayList<String> chosen = new ArrayList<String>();
    chosen.add(first);

    while (chosen.size() < k) {
      String furthest = null;
      double smallestClosestSim = Double.MAX_VALUE;
      for (String candidate : keys) {
        if (chosen.contains(candidate)) {
          continue;
        }
        double closestSim = maxSimilarityToAny(candidate, chosen, matrix);
        if (closestSim < smallestClosestSim
            || (closestSim == smallestClosestSim && (furthest == null || candidate.compareTo(furthest) < 0))) {
          smallestClosestSim = closestSim;
          furthest = candidate;
        }
      }
      chosen.add(furthest);
    }

    int i = 0;
    for (String key : chosen) {
      centroids.put(String.valueOf(i++), copyVector(tfidf.get(key)));
    }
    return centroids;
  }

  private static double maxSimilarityToAny(String candidate, List<String> chosen,
      CosineDistanceMatrix matrix) {
    double maxSim = -1.0d;
    for (String other : chosen) {
      double sim = matrix.distanceBetween(candidate, other);
      if (sim > maxSim) {
        maxSim = sim;
      }
    }
    return maxSim;
  }

  private static Map<Integer, List<String>> assignGroups(
      LinkedHashMap<String, Map<String, Double>> centroids,
      Map<String, Map<String, Double>> tfidf) {
    Map<Integer, List<String>> groups = new LinkedHashMap<Integer, List<String>>();
    for (String centroidId : centroids.keySet()) {
      groups.put(Integer.valueOf(centroidId), new ArrayList<String>());
    }

    for (String seriesId : sortedIds(tfidf)) {
      Map<String, Double> series = tfidf.get(seriesId);
      String bestCentroid = null;
      double bestSimilarity = -1.0d;
      for (Entry<String, Map<String, Double>> centroid : centroids.entrySet()) {
        double similarity = TP.cosineDistance(toHashMap(centroid.getValue()), toHashMap(series));
        if (similarity > bestSimilarity
            || (similarity == bestSimilarity && (bestCentroid == null
                || centroid.getKey().compareTo(bestCentroid) < 0))) {
          bestSimilarity = similarity;
          bestCentroid = centroid.getKey();
        }
      }
      groups.get(Integer.valueOf(bestCentroid)).add(seriesId);
    }
    return groups;
  }

  private static boolean updateCentroids(LinkedHashMap<String, Map<String, Double>> centroids,
      Map<Integer, List<String>> groups, Map<String, Map<String, Double>> tfidf) {
    boolean changed = false;
    for (Entry<Integer, List<String>> group : groups.entrySet()) {
      Map<String, Double> updated = meanVector(tfidf, group.getValue());
      String key = String.valueOf(group.getKey());
      double similarity = TP.cosineDistance(toHashMap(updated), toHashMap(centroids.get(key)));
      if ((1.0d - similarity) > CONVERGENCE) {
        centroids.put(key, updated);
        changed = true;
      }
    }
    return changed;
  }

  static Map<String, Double> meanVector(Map<String, Map<String, Double>> tfidf,
      List<String> memberIds) {
    if (memberIds.isEmpty()) {
      throw new IllegalArgumentException("Cannot compute centroid of an empty cluster");
    }
    Map<String, Double> template = tfidf.get(memberIds.get(0));
    TreeSet<String> vocabulary = new TreeSet<String>(template.keySet());
    for (int i = 1; i < memberIds.size(); i++) {
      vocabulary.addAll(tfidf.get(memberIds.get(i)).keySet());
    }
    HashMap<String, Double> sum = new HashMap<String, Double>();
    for (String word : vocabulary) {
      sum.put(word, 0.0d);
    }
    for (String id : memberIds) {
      for (Entry<String, Double> e : tfidf.get(id).entrySet()) {
        sum.put(e.getKey(), sum.get(e.getKey()) + e.getValue());
      }
    }
    HashMap<String, Double> mean = new HashMap<String, Double>();
    double n = memberIds.size();
    for (Entry<String, Double> e : sum.entrySet()) {
      mean.put(e.getKey(), e.getValue() / n);
    }
    return TP.normalizeToUnitVector(mean);
  }

  private static Map<String, Double> copyVector(Map<String, Double> vector) {
    return new HashMap<String, Double>(vector);
  }

  private static HashMap<String, Double> toHashMap(Map<String, Double> vector) {
    return new HashMap<String, Double>(vector);
  }

  private static HashMap<String, HashMap<String, Double>> toTfidfHashMap(
      Map<String, Map<String, Double>> tfidf) {
    HashMap<String, HashMap<String, Double>> res = new HashMap<String, HashMap<String, Double>>();
    for (Entry<String, Map<String, Double>> e : tfidf.entrySet()) {
      res.put(e.getKey(), new HashMap<String, Double>(e.getValue()));
    }
    return res;
  }

  private static ArrayList<String> sortedIds(Map<String, Map<String, Double>> tfidf) {
    return new ArrayList<String>(new TreeSet<String>(tfidf.keySet()));
  }
}
