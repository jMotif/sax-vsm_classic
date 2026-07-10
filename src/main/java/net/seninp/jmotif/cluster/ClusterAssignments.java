package net.seninp.jmotif.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Immutable partition of series (bag) ids into {@code k} clusters.
 */
public final class ClusterAssignments {

  private final Map<Integer, List<String>> clusters;
  private final Map<String, Integer> assignment;

  private ClusterAssignments(Map<Integer, List<String>> clusters) {
    this.clusters = new LinkedHashMap<Integer, List<String>>();
    this.assignment = new HashMap<String, Integer>();
    for (Entry<Integer, List<String>> e : clusters.entrySet()) {
      List<String> members = Collections.unmodifiableList(new ArrayList<String>(e.getValue()));
      this.clusters.put(e.getKey(), members);
      for (String id : members) {
        this.assignment.put(id, e.getKey());
      }
    }
  }

  public static ClusterAssignments of(Map<Integer, List<String>> clusters) {
    return new ClusterAssignments(clusters);
  }

  public int k() {
    return clusters.size();
  }

  public List<String> members(int clusterId) {
    List<String> m = clusters.get(clusterId);
    if (m == null) {
      throw new IllegalArgumentException("Unknown cluster id: " + clusterId);
    }
    return m;
  }

  public int clusterOf(String seriesId) {
    Integer c = assignment.get(seriesId);
    if (c == null) {
      throw new IllegalArgumentException("Unknown series id: " + seriesId);
    }
    return c.intValue();
  }

  public Map<Integer, List<String>> asMap() {
    return Collections.unmodifiableMap(clusters);
  }

  /**
   * Fraction of series whose true label matches the plurality label in their assigned cluster.
   */
  public double labelPurity(Map<String, String> trueLabels) {
    if (assignment.isEmpty()) {
      return 0.0d;
    }
    int correct = 0;
    for (Entry<Integer, List<String>> cluster : clusters.entrySet()) {
      if (cluster.getValue().isEmpty()) {
        continue;
      }
      Map<String, Integer> counts = new HashMap<String, Integer>();
      for (String id : cluster.getValue()) {
        String label = trueLabels.get(id);
        if (label == null) {
          throw new IllegalArgumentException("Missing true label for series id: " + id);
        }
        Integer n = counts.get(label);
        counts.put(label, n == null ? 1 : n + 1);
      }
      int max = 0;
      for (Integer n : counts.values()) {
        if (n > max) {
          max = n;
        }
      }
      correct += max;
    }
    return (double) correct / (double) assignment.size();
  }
}
