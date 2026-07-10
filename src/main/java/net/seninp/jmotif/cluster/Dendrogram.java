package net.seninp.jmotif.cluster;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of hierarchical clustering — a merge tree over series ids.
 */
public final class Dendrogram {

  private final ClusterNode root;

  Dendrogram(ClusterNode root) {
    this.root = root;
  }

  public ClusterNode root() {
    return root;
  }

  /** Newick representation (wrap in parentheses for a standalone tree). */
  public String toNewick() {
    return root.toNewick();
  }

  /**
   * Greedy {@code k}-way cut: repeatedly split the largest internal subtree until {@code k} leaf
   * groups remain (or no splittable node is left).
   */
  public ClusterAssignments partition(int k) {
    if (k < 1) {
      throw new IllegalArgumentException("k must be >= 1 (got " + k + ")");
    }
    List<ClusterNode> active = new ArrayList<ClusterNode>();
    active.add(root);
    while (active.size() < k) {
      int splitIdx = -1;
      int maxSize = -1;
      String splitTieBreak = null;
      for (int i = 0; i < active.size(); i++) {
        ClusterNode node = active.get(i);
        if (node.isLeaf()) {
          continue;
        }
        int size = node.memberIds().size();
        String firstId = node.memberIds().first();
        if (size > maxSize
            || (size == maxSize && (splitTieBreak == null || firstId.compareTo(splitTieBreak) < 0))) {
          maxSize = size;
          splitIdx = i;
          splitTieBreak = firstId;
        }
      }
      if (splitIdx < 0) {
        break;
      }
      ClusterNode node = active.remove(splitIdx);
      active.add(splitIdx, node.left);
      active.add(splitIdx + 1, node.right);
    }

    Map<Integer, List<String>> groups = new LinkedHashMap<Integer, List<String>>();
    for (int i = 0; i < active.size(); i++) {
      groups.put(Integer.valueOf(i), new ArrayList<String>(active.get(i).memberIds()));
    }
    return ClusterAssignments.of(groups);
  }
}
