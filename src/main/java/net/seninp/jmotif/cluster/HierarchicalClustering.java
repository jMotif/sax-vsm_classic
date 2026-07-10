package net.seninp.jmotif.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;
import net.seninp.jmotif.text.CosineDistanceMatrix;

/**
 * Agglomerative hierarchical clustering over SAX-VSM tf·idf vectors.
 */
public final class HierarchicalClustering {

  private HierarchicalClustering() {
  }

  public static Dendrogram cluster(Map<String, Map<String, Double>> tfidf, Linkage linkage) {
    if (tfidf == null || tfidf.isEmpty()) {
      throw new IllegalArgumentException("tfidf must not be empty");
    }
    if (tfidf.size() == 1) {
      return new Dendrogram(new ClusterNode(tfidf.keySet().iterator().next()));
    }

    HashMap<String, HashMap<String, Double>> data = new HashMap<String, HashMap<String, Double>>();
    for (Map.Entry<String, Map<String, Double>> e : tfidf.entrySet()) {
      data.put(e.getKey(), new HashMap<String, Double>(e.getValue()));
    }

    CosineDistanceMatrix distanceMatrix = new CosineDistanceMatrix(data);
    distanceMatrix.transformForHC();

    List<ClusterNode> active = new ArrayList<ClusterNode>();
    for (String key : new TreeSet<String>(data.keySet())) {
      active.add(new ClusterNode(key));
    }

    Stack<ClusterNode> stack = new Stack<ClusterNode>();
    while (active.size() > 1) {
      if (stack.isEmpty()) {
        stack.push(active.get(0));
      }

      ClusterNode top = stack.peek();
      ClusterNode nearest = nearestNeighbor(top, active, distanceMatrix, linkage);

      if (stack.size() >= 2 && nearest.equals(stack.get(stack.size() - 2))) {
        ClusterNode a = stack.pop();
        ClusterNode b = stack.pop();
        active.remove(a);
        active.remove(b);
        ClusterNode merged = new ClusterNode();
        merged.merge(a, b, a.distanceTo(b, distanceMatrix, linkage));
        active.add(merged);
        stack.clear();
      }
      else {
        stack.push(nearest);
      }
    }

    return new Dendrogram(active.get(0));
  }

  private static ClusterNode nearestNeighbor(ClusterNode pivot, List<ClusterNode> active,
      CosineDistanceMatrix distanceMatrix, Linkage linkage) {
    ClusterNode nearest = null;
    double minDistance = Double.MAX_VALUE;
    for (ClusterNode candidate : active) {
      if (pivot.equals(candidate)) {
        continue;
      }
      double distance = pivot.distanceTo(candidate, distanceMatrix, linkage);
      if (distance < minDistance
          || (distance == minDistance && (nearest == null
              || candidate.memberIds().first().compareTo(nearest.memberIds().first()) < 0))) {
        nearest = candidate;
        minDistance = distance;
      }
    }
    return nearest;
  }
}
