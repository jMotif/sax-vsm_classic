package net.seninp.jmotif.cluster;

/**
 * Agglomerative linkage rules supported by {@link HierarchicalClustering}.
 */
public enum Linkage {
  /** Minimum pairwise distance between members. */
  SINGLE,
  /** Maximum pairwise distance between members. */
  COMPLETE
}
